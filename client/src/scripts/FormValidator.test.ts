import { checkPasswordValidity } from './FormValidator'

// password 8 or more characters
test.each(["password", "securepassword", "m0r3s3cur3P455w@rd", "eggs and bacon"])
('expect "%s" to be a valid password', (password) => {
    expect(checkPasswordValidity(password)).toBe(true)
})

// password under 8 characters
test.each(["7charac", "eggs", ""])
('expect "%s" to be an invalid password', (password) => {
    expect(checkPasswordValidity(password)).toBe(false)
})
