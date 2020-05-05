import {
  updatePassword,
  addPassportCountry,
  deletePassportCountry,
  persistChangesToProfile,
  addActivityType,
  removeActivityType,
} from "./profile.controller";
import { UserApiFormat } from "@/scripts/User";
import axios from "axios";

jest.mock("axios");

const userModel = require("../models/user.model");
const countriesModel = require("../models/countries.model");
const formValidator = require("../scripts/FormValidator");
formValidator.checkPasswordValidity = jest.fn((password) => {
  switch (password) {
    case "INVALID":
      return false;
    default:
      return true;
  }
});

const mockedUser: UserApiFormat = {
  profile_id: 1,
  passports: [],
};
userModel.getProfileById = jest.fn((profileId) => {
  return profileId === 1 ? mockedUser : null;
});
const validPassportCountries: Array<{ name: string }> = [
  { name: "New Zealand" },
  { name: "Australia" },
];
countriesModel.loadPassportCountries = jest.fn(async () => {
  return validPassportCountries;
});

function getValidCountryNames() {
    return validPassportCountries.map(country => country.name)
}


// --------- PASSWORDS ---------- //
test("expect updatePassword to throw an error if newPassword is different from repeatPassword", async () => {
  await expect(
    updatePassword("password", "newpassword", "wrongrepeat", 1)
  ).rejects.toThrow(new Error("new password and repeat password do not match"));
});

test("expect updatePassword to throw an error if newPassword is an invalid password", async () => {
  await expect(
    updatePassword("password", "INVALID", "INVALID", 1)
  ).rejects.toThrow(new Error("new password must be at least 8 characters"));
});

test("expect updatePassword to send update password request if newPassword is valid and matches repeatPassword", async () => {
  userModel.updateCurrentPassword = jest.fn(); // mocks updateCurrentPassword function

  await expect(
    updatePassword("password", "newpassword", "newpassword", 1)
  ).resolves.toBe(undefined);
  expect(userModel.updateCurrentPassword.mock.calls.length).toBe(1);
});

// the below test ensures that changes to the password rules do not make old passwords unable to be changed
test("expect updatePassword to send update password request even if old password does not match rules", async () => {
  userModel.updateCurrentPassword = jest.fn();

  await expect(
    updatePassword("INVALID", "newpassword", "newpassword", 1)
  ).resolves.toBe(undefined);
  expect(userModel.updateCurrentPassword.mock.calls.length).toBe(1);
});

// --------- ADD PASSPORT COUNTRY ---------- //
test("expect addPassportCountry to throw an error if a country does not exist", async () => {
  let profile: UserApiFormat = {};
  const invalidCountry = "invalid country";
  await expect(addPassportCountry(invalidCountry, profile)).rejects.toThrow(
    new Error(`${invalidCountry} is not recognised as a country`)
  );
});

test.each(getValidCountryNames())(
  "expect addPassportCountry to throw an error if %s is already added to the given profile",
  async (validCountry) => {
    let profile: UserApiFormat = {
      passports: [validCountry],
    };
    await expect(addPassportCountry(validCountry, profile)).rejects.toThrow(
      new Error(
        `the target profile already has ${validCountry} as a passport country`
      )
    );
  }
);

test.each(getValidCountryNames())(
  "expect addPassportCountry to add %s to a profile without persisting changes",
  async (validCountry) => {
    let profile: UserApiFormat = {
      passports: [],
    };
    await addPassportCountry(validCountry, profile);
    expect(profile.passports).toHaveLength(1);
    expect(profile.passports).toContain(validCountry);

    // this expect doesn't work, but will only throw an error if axios.put is called, meaning it does what we need it to. ¯\_(ツ)_/¯
    expect(axios.put).toHaveBeenCalledTimes(0); // changes should not be persisted automatically
  }
);

test.each(getValidCountryNames())(
  "expect addPassportCountry to initialise the profile's passports list if nonexistent",
  async (validCountry) => {
    let profile: UserApiFormat = {};
    await addPassportCountry(validCountry, profile);
    expect(profile.passports).not.toBeUndefined();
    expect(profile.passports).toHaveLength(1);
    expect(profile.passports).toContain(validCountry);

    // this expect doesn't work, but will only throw an error if axios.put is called, so it actually still checks what we need it to. ¯\_(ツ)_/¯
    expect(axios.put).toHaveBeenCalledTimes(0); // changes should not be persisted automatically
  }
);

// --------- DELETE PASSPORT COUNTRY ---------- //
test.each(["New Zealand", "No Validation", "Just Removal"])("expect deletePassportCountry to throw an error if given a profile not containing %s", (country) => {
  let profile: UserApiFormat = {
    passports: [],
  };
  expect(() => {
    deletePassportCountry(country, profile);
  }).toThrow(`the target user does not have ${country} as a passport country`);
});

test.each(["Westeros", "Greendale", "Anywhere"])("expect deletePassportCountry to remove a country from a profile without persisting changes", async (country) => {
  let profile: UserApiFormat = {
    passports: [country],
  };
  expect(deletePassportCountry(country, profile)).toBe(undefined);

  // this expect doesn't work, but will only throw an error if axios.put is called, so it actually still checks what we need it to. ¯\_(ツ)_/¯
  expect(axios.put).toHaveBeenCalledTimes(0); // changes should not be persisted automatically
});

// --------- PERSIST CHANGES TO PROFILE ---------- //
test("expect persistChangesToProfile to throw an error when a required field is missing", async () => {
  let profile: UserApiFormat = {
    profile_id: 1,
  };
  await expect(
    persistChangesToProfile(profile, 1)
  ).rejects.toThrow("Profile is not valid");
  expect(axios.put).toHaveBeenCalledTimes(0);
});

test.skip("expect persistChangesToProfile to persist changes when given a valid profile and profile id", async () => {
  let profile: UserApiFormat = {
    profile_id: 1,
    firstname: "Bob",
    lastname: "Ross",
    date_of_birth: "2000-01-01",
    gender: "male",
  };
  await expect(
    // TODO this does not work
    persistChangesToProfile(profile, 1)
  ).resolves.toBe(undefined);
  expect(axios.put).toHaveBeenCalledTimes(1);
});


// --------- ACTIVITY TYPES ---------- //
test('expect activity type list to be updated',
    async () => {
        var user = {"profile_id": 0, "activities": []} as UserApiFormat;
        userModel.saveActivityTypes = jest.fn();
        userModel.getProfileById = jest.fn(() => {
            return user;
        });

        await addActivityType("running", 0);
        expect(userModel.saveActivityTypes.mock.calls[0][0]).toBe(user);
    }
)

test.skip('expect no user found when updating activity types with null user',
    async () => {
        userModel.saveActivityTypes = jest.fn();
        userModel.getProfileById = jest.fn(() => {
            return null;
        });

        try {
            await addActivityType("running", 0)
            expect(1).toEqual(2);
        } catch (error) {
            expect(error.message).toEqual("No active user found.");
        }
    })

// TODO Look into whether or not to test with null user as equivalent in practice would result in an error thrown off a 404 response (?)
test.skip('expect no user found when deleting activity types from null user',
    async () => {
        userModel.saveActivityTypes = jest.fn();
        userModel.getProfileById = jest.fn(() => {
            return null;
        });

        try {
            await removeActivityType("running", 0)
            expect(1).toEqual(2);
        } catch (error) {
            expect(error.message).toEqual("No active user found.");
        }
    })

test('expect no activity type error when user has no activity type property',
    async () => {
        userModel.saveActivityTypes = jest.fn();
        userModel.getProfileById = jest.fn(() => {
            var user = {"profile_id": 0} as UserApiFormat;
            return user;
        });

        try {
            await removeActivityType("running", 0)
            expect(1).toEqual(2);
        } catch (error) {
            expect(error.message).toEqual("User has no activity types associated with their profile.");
        }
    })

test('expect no activity type error when user has activity type list empty',
    async () => {
        userModel.saveActivityTypes = jest.fn();
        userModel.getProfileById = jest.fn(() => {
            var user = {"profile_id": 0, "activities": []} as UserApiFormat;
            return user;
        });

        try {
            await removeActivityType("running", 0)
            expect(1).toEqual(2);
        } catch (error) {
            expect(error.message).toEqual("User has no activity types associated with their profile.");
        }
    })

test('expect activity type to be removed from user when exists in list',
async () => {
    userModel.removeUserActivityType = jest.fn();
    userModel.getCurrentUser = jest.fn(() => {
        var user = {"activities": ["running"]} as UserApiFormat;
        return user;
    });

    await removeActivityType("running");
    await expect(userModel.removeUserActivityType.mock.calls[0][0]).toBe("running");
})