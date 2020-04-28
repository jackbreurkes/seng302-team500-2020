import { updatePassword } from './profile.controller'

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
        await expect(updatePassword("password", "newpassword", "wrongrepeat", 1)).rejects.toThrow(
            new Error("new password and repeat password do not match")
            );
    }
)

test('expect updatePassword to throw an error if newPassword is an invalid password',
    async () => {
        await expect(updatePassword("password", "INVALID", "INVALID", 1)).rejects.toThrow(
            new Error("new password must be at least 8 characters")
        );
    }
)

test('expect updatePassword to send update password request if newPassword is valid and matches repeatPassword',
    async () => {
        userModel.updateCurrentPassword = jest.fn(); // mocks updateCurrentPassword function

        await expect(updatePassword("password", "newpassword", "newpassword", 1)).resolves.toBe(undefined);
        expect(userModel.updateCurrentPassword.mock.calls.length).toBe(1);
    }
)

// the below test ensures that changes to the password rules do not make old passwords unable to be changed
test('expect updatePassword send update password request even if old password does not match rules',
    async () => {
        userModel.updateCurrentPassword = jest.fn();

        await expect(updatePassword("INVALID", "newpassword", "newpassword", 1)).resolves.toBe(undefined);
        expect(userModel.updateCurrentPassword.mock.calls.length).toBe(1);
    }
)