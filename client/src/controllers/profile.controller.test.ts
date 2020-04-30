import { updatePassword, addActivityType, removeActivityType } from './profile.controller'
import { UserApiFormat } from '@/scripts/User';

const userModel = require('../models/user.model');
const formValidator = require('../scripts/FormValidator');
formValidator.checkPasswordValidity = jest.fn(password => {
    switch (password) {
        case "INVALID":
            return false
        default:
            return true
    }
})


test('expect updatePassword to throw an error if newPassword is different from repeatPassword',
    async () => {
        await expect(updatePassword("password", "newpassword", "wrongrepeat")).rejects.toThrow(
            new Error("new password and repeat password do not match")
            );
    }
)

test('expect updatePassword to throw an error if newPassword is an invalid password',
    async () => {
        await expect(updatePassword("password", "INVALID", "INVALID")).rejects.toThrow(
            new Error("new password must be at least 8 characters")
        );
    }
)

test('expect updatePassword to send update password request if newPassword is valid and matches repeatPassword',
    async () => {
        userModel.updateCurrentPassword = jest.fn(); // mocks updateCurrentPassword function

        await expect(updatePassword("password", "newpassword", "newpassword")).resolves.toBe(undefined);
        expect(userModel.updateCurrentPassword.mock.calls.length).toBe(1);
    }
)

// the below test ensures that changes to the password rules do not make old passwords unable to be changed
test('expect updatePassword send update password request even if old password does not match rules',
    async () => {
        userModel.updateCurrentPassword = jest.fn();

        await expect(updatePassword("INVALID", "newpassword", "newpassword")).resolves.toBe(undefined);
        expect(userModel.updateCurrentPassword.mock.calls.length).toBe(1);
    }
)

test('expect activity type list to be updated',
    async () => {
        userModel.addUserActivityType = jest.fn();
        userModel.getCurrentUser = jest.fn(() => {
            var user = {"activities": []} as UserApiFormat;
            return user;
        });

        await addActivityType("running");
        expect(userModel.addUserActivityType.mock.calls[0][0]).toBe("running");
    }
)

test('expect no user found when updating activity types with null user',
    async () => {
        userModel.addUserActivityType = jest.fn();
        userModel.getCurrentUser = jest.fn(() => {return null});

        //expect(await addActivityType("running")).toThrowError("No active user found.");

        try {
            await addActivityType("running")
            expect(1).toEqual(2);
        } catch (error) {
            expect(error.message).toEqual("No active user found.");
        }
    })

    test('expect no user found when deleting activity types from null user',
    async () => {
        userModel.removeUserActivityType = jest.fn();
        userModel.getCurrentUser = jest.fn(() => {return null});

        //expect(await addActivityType("running")).toThrowError("No active user found.");

        try {
            await removeActivityType("running")
            expect(1).toEqual(2);
        } catch (error) {
            expect(error.message).toEqual("No active user found.");
        }
    })

test('expect no activity type error when user has no activity type property',
async () => {
    userModel.removeUserActivityType = jest.fn();
    userModel.getCurrentUser = jest.fn(() => {
        var user = {} as UserApiFormat;
        return user;
    });

    try {
        await removeActivityType("running")
        expect(1).toEqual(2);
    } catch (error) {
        expect(error.message).toEqual("User has no activity types associated with their profile.");
    }
})

test('expect no activity type error when user has activity type list empty',
async () => {
    userModel.removeUserActivityType = jest.fn();
    userModel.getCurrentUser = jest.fn(() => {
        var user = {"activities": []} as UserApiFormat;
        return user;
    });

    try {
        await removeActivityType("running")
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