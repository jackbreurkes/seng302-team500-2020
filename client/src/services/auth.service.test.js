/* eslint-disable no-unused-vars */
/* eslint-env jest */

import * as auth from "./auth.service";
import instance from "./axios.service";
jest.mock("./axios.service.ts");

const tokenKey = "token";
const userIdKey = "userId";
const permissionLevelKey = "permissionLevel";

beforeEach(() => {
    localStorage.clear();
})

instance.interceptors.request.use = jest.fn();
instance.interceptors.response.use = jest.fn();

test(
    "expect getMyToken to return the user's token if it exists", () => {
        const fakeToken = "usertoken";
        localStorage.setItem(tokenKey, fakeToken);
        expect(auth.getMyToken()).toBe(fakeToken);
    }
);

test(
    "expect getMyToken to return the null if it does not exist", () => {
        expect(auth.getMyToken()).toBe(null);
    }
);

test.each(["four", "fake", "fifty2"])(
    "expect getMyUserId to return null if the stored user ID is not a number", (userId) => {
        localStorage.setItem(userIdKey, userId);
        expect(auth.getMyUserId()).toBe(null);
    }
);

test.each(["four", "fake", "fifty2"])(
    "expect getMyUserId to return null if no user ID is stored", (userId) => {
        localStorage.setItem(userIdKey, userId);
        expect(auth.getMyUserId()).toBe(null);
    }
);

test.each(["5", "0", "20000"])(
    "expect getMyUserId to return the user's ID as a number if one is stored", (userId) => {
        localStorage.setItem(userIdKey, userId);
        expect(auth.getMyUserId()).toBe(parseInt(userId));
    }
);

test("clearAuthInfo removes user token, id and permission level information", () => {
        auth.clearAuthInfo();
        expect(localStorage.removeItem).toHaveBeenCalledWith(tokenKey)
        expect(localStorage.removeItem).toHaveBeenCalledWith(userIdKey)
        expect(localStorage.removeItem).toHaveBeenCalledWith(permissionLevelKey)
    }
)

test.each([0, 10, 119, -20])(
    "expect isAdmin to return false if the user's permission level is under 120",
    (permissionLevel) => {
        localStorage.setItem(permissionLevelKey, permissionLevel);
        expect(auth.isAdmin()).toBe(false);
    }
)

test.each([120, 126, 127, 10000])(
    "expect isAdmin to return false if the user's permission level is under 120",
    (permissionLevel) => {
        localStorage.setItem(permissionLevelKey, permissionLevel);
        expect(auth.isAdmin()).toBe(true);
    }
)

test.each(["five", "ADMIN", null, undefined])(
    "expect isAdmin to return false if the user's permission level is not a number",
    (permissionLevel) => {
        localStorage.setItem(permissionLevelKey, permissionLevel);
        expect(auth.isAdmin()).toBe(false);
    }
)

test("expect login to store user credentials if the login was successful",
    async () => {
        instance.post.mockImplementationOnce(() => {
            return Promise.resolve({"data": {
                token: "myToken",
                permission_level: 0,
                profile_id: 1
            }})
        })
        await auth.login("email", "password")
        expect(instance.post).toBeCalledTimes(1);
        expect(localStorage.getItem(tokenKey)).toBe("myToken")
        expect(localStorage.getItem(permissionLevelKey)).toBe("0")
        expect(localStorage.getItem(userIdKey)).toBe("1")
    }    
)

test("expect verifyUserId to clear all user data if the user's id is not stored", async () => {
    localStorage.setItem(permissionLevelKey, 0)
    localStorage.setItem(tokenKey, "token")
    localStorage.removeItem(userIdKey)
    await auth.verifyUserId();
    expect(localStorage.getItem(tokenKey)).toBeNull()
    expect(localStorage.getItem(permissionLevelKey)).toBeNull()
    expect(localStorage.getItem(userIdKey)).toBeNull()
})

test("expect verifyUserId to make no changes if the user's id matches their token", async () => {
    const testProfileId = 1;
    const testToken = "token";
    const testPermissionLevel = 20;
    localStorage.setItem(userIdKey, testProfileId)
    localStorage.setItem(tokenKey, testToken)
    localStorage.setItem(permissionLevelKey, testPermissionLevel)
    instance.get.mockReset();
    instance.get.mockImplementationOnce(() => {
        return Promise.resolve({data: {profile_id: testProfileId}})
    })

    await auth.verifyUserId();

    expect(localStorage.getItem(tokenKey)).toBe(testToken)
    expect(localStorage.getItem(permissionLevelKey)).toBe(testPermissionLevel.toString())
    expect(localStorage.getItem(userIdKey)).toBe(testProfileId.toString())
})

test("expect verifyUserId to make no changes and throw an error if the server does not respond", async () => {
    const testProfileId = 1;
    const testToken = "token";
    const testPermissionLevel = 20;
    localStorage.setItem(userIdKey, testProfileId)
    localStorage.setItem(tokenKey, testToken)
    localStorage.setItem(permissionLevelKey, testPermissionLevel)
    instance.get.mockReset();
    instance.get.mockImplementationOnce(() => {
        return Promise.reject({message: "server did not respond"}) // error does not have a "response" attribute
    })

    await expect(auth.verifyUserId()).rejects.toHaveProperty("message", "server did not respond");

    expect(localStorage.getItem(tokenKey)).toBe(testToken)
    expect(localStorage.getItem(permissionLevelKey)).toBe(testPermissionLevel.toString())
    expect(localStorage.getItem(userIdKey)).toBe(testProfileId.toString())
})


test("expect verifyUserId to clear auth info if the server returns an error response", async () => {
    const testProfileId = 1;
    const testToken = "token";
    const testPermissionLevel = 20;
    localStorage.setItem(userIdKey, testProfileId)
    localStorage.setItem(tokenKey, testToken)
    localStorage.setItem(permissionLevelKey, testPermissionLevel)
    instance.get.mockReset();
    instance.get.mockImplementationOnce(() => {
        return Promise.reject({response: "this error has a 'response' attribute"})
    })

    await auth.verifyUserId();

    expect(localStorage.getItem(tokenKey)).toBeNull()
    expect(localStorage.getItem(permissionLevelKey)).toBeNull()
    expect(localStorage.getItem(userIdKey)).toBeNull()
})


test("expect logout to send logout request and clear auth info", async () => {
    const testProfileId = 1;
    const testToken = "token";
    const testPermissionLevel = 20;
    localStorage.setItem(userIdKey, testProfileId)
    localStorage.setItem(tokenKey, testToken)
    localStorage.setItem(permissionLevelKey, testPermissionLevel)
    instance.delete.mockReset();

    await auth.logout();

    expect(instance.delete).toHaveBeenCalledWith("logmeout")
    expect(instance.delete).toHaveBeenCalledTimes(1)
    expect(localStorage.getItem(tokenKey)).toBeNull()
    expect(localStorage.getItem(permissionLevelKey)).toBeNull()
    expect(localStorage.getItem(userIdKey)).toBeNull()
})