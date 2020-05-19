import { submitForm, LoginFormData } from './login.controller'
const loginPage = require('../services/auth.service');

loginPage.login = jest.fn();

test('expect submitForm to resolve and call login',
    async () => {
        let testFormData: LoginFormData = {email: "test@google.com", password: "password"}
        await expect(submitForm(testFormData)).resolves.toBe(undefined);
        expect(loginPage.login.mock.calls.length).toBe(1);
    }
)