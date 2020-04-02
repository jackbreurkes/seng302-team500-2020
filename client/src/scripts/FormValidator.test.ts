import { checkPasswordValidity, checkFirstnameValidity, checkLastnameValidity, checkMiddlenameValidity, checkNicknameValidity, checkBioValidity, checkDobValidity, checkGenderValidity } from './FormValidator'

// password 8 or more characters
test.each(["password", "securepassword", "m0r3s3cur3P455w@rd", "eggs and bacon"])(
    'expect "%s" to be a valid password', (password) => {
    expect(checkPasswordValidity(password)).toBe(true)
})

// password under 8 characters
test.each(["7charac", "eggs", ""])(
    'expect "%s" to be an invalid password', (password) => {
    expect(checkPasswordValidity(password)).toBe(false)
})

// first name more than 1 character, 30 characters at most. Contains no numbers.
test.each(["test", "e", "wowitsareallylongnamethatscool"])(
    'expect %s to be a valid first name', (name) => {
        expect(checkFirstnameValidity(name)).toBe(true)
    }
)

// first name more than 30 characters
test.each(["AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "wowitsareallylongnamethatsreallycool"])(
    'expect %s to be a valid first name', (name) => {
        expect(checkFirstnameValidity(name)).toBe(false)
    }
)

// first name more than 1 character, 30 characters at most. Contains number(s).
test.each(["H4rry", "Brianna42", "8"])(
    'expect %s to be a valid first name', (name) => {
        expect(checkFirstnameValidity(name)).toBe(false)
    }
)

// first name empty string.
test('expect %s to be a valid first name', () => {
        expect(checkFirstnameValidity("")).toBe(false)
    }
)

// first name is undefined.
test('expect %s to be a valid first name', () => {
    expect(checkFirstnameValidity(undefined)).toBe(false)
}
)

// Last name is at least 1 character and less than or equal to 30 characters. Contains no numbers.
test.each(["Williams", "E", "Smith", "Lee", "Wowitsareallylongnamethatscool"])(
    'expect %s to be a valid last name', (name) => {
        expect(checkLastnameValidity(name)).toBe(true)
    }
)

// Last name is over 30 characters. Contains no numbers.
test.each(["WilliamsIsNotAReallyLongLastNameNormally", "Wowitsareallylongnamethatiscool"])(
    'expect %s to be an invalid last name', (name) => {
        expect(checkLastnameValidity(name)).toBe(false)
    }
)

// Last name is at least 1 character and less than or equal to 30 characters. Contains number(s).
test.each(["William5", "1", "Chan09"])(
    'expect %s to be an invalid last name', (name) => {
        expect(checkLastnameValidity(name)).toBe(false)
    }
)

// Last name is the empty string.
test('expect %s to be an invalid last name', () => {
        expect(checkLastnameValidity("")).toBe(false)
    }
)

// Last name is undefined.
test('expect %s to be an invalid last name', () => {
    expect(checkLastnameValidity(undefined)).toBe(false)
}
)

// Middle name is at least 1 character and less than or equal to 30 characters. Contains no numbers.
test.each(["Robin", "A", "John", "Anna", "Wowitsareallylongnamethatscool"])(
    'expect %s to be a valid middle name', (name) => {
        expect(checkMiddlenameValidity(name)).toBe(true)
    }
)

// Middle name is over 30 characters. Contains no numbers.
test.each(["SomeReallyLongMiddleNameThatIsActuallyRidiculous", "Wowitsareallylongnamethatiscool"])(
    'expect %s to be an invalid middle name', (name) => {
        expect(checkMiddlenameValidity(name)).toBe(false)
    }
)

// Middle name is at least 1 character and less than or equal to 30 characters. Contains number(s).
test.each(["William5", "1", "Chan09"])(
    'expect %s to be an invalid middle name', (name) => {
        expect(checkMiddlenameValidity(name)).toBe(false)
    }
)

// Middle name is the empty string.
test('expect %s to be a valid middle name', () => {
        expect(checkMiddlenameValidity("")).toBe(true)
    }
)

// Middle name is undefined.
test('expect %s to be a valid middle name', () => {
    expect(checkMiddlenameValidity(undefined)).toBe(true)
}
)

// Nickname is at least 6 characters and less than or equal to 30 characters. Contains no whitespace.
test.each(["NewUser64", "123456", "AAAAAA", "Wowitsareallylongnamethatsgr8!", "Wowitsareallylongnamethatscool"])(
    'expect %s to be a valid nickname', (name) => {
        expect(checkNicknameValidity(name)).toBe(true)
    }
)

// Nickname is over 30 characters. Contains no whitespace.
test.each(["SomeReallyLongNicknameThatIsActuallyRidiculous!", "Wowitsareallylongnamethatiscool"])(
    'expect %s to be an invalid nickname', (name) => {
        expect(checkNicknameValidity(name)).toBe(false)
    }
)

// Nickname is less than 6 characters (but at least one character). Contains no whitespace.
test.each(["Six!", "Short", "A"])(
    'expect %s to be an invalid nickname', (name) => {
        expect(checkNicknameValidity(name)).toBe(false)
    }
)

// Nickname is at least 6 characters and less than or equal to 30 characters. Contains whitespace.
test.each(["I am a new user", " Whitespace", "Whitespace ", "\tTabbyTabTab", "\nAWholeNewLine"])(
    'expect %s to be an invalid nickname', (name) => {
        expect(checkNicknameValidity(name)).toBe(false)
    }
)

// Nickname is the empty string.
test('expect %s to be a valid nickname', () => {
        expect(checkNicknameValidity("")).toBe(true)
    }
)

// Nickname is undefined.
test('expect %s to be a valid nickname', () => {
    expect(checkNicknameValidity(undefined)).toBe(true)
}
)

// Bio is at least 8 characters.
test.each(["This is my bio!", "12345678", "AAAAAAAA", "A whole new bio has been made! 1234567890"])(
    'expect %s to be a valid bio', (bio) => {
        expect(checkBioValidity(bio)).toBe(true)
    }
)

// Bio less than 8 characters.
test.each(["Short!", "A", "TinyBio", "A bio."])(
    'expect %s to be an invalid bio', (bio) => {
        expect(checkBioValidity(bio)).toBe(false)
    }
)

// Bio is the empty string.
test('expect %s to be a valid bio', () => {
        expect(checkBioValidity("")).toBe(true)
    }
)

// Bio is undefined.
test('expect %s to be a valid bio', () => {
    expect(checkBioValidity(undefined)).toBe(true)
}
)

// Get today's date in local time
const today = new Date();
let month = (today.getMonth() + 1).toString();
if (month.length == 1) {
    month = "0" + month;
}
let date = today.getDate().toString();
if (date.length == 1) {
    date = "0" + date;
}
// Date of birth is in valid format and is in the past but after or on 1900-01-01.
test.each(["2000-12-29", "1900-01-01", "2000/01/01", "01-02-2003", today.getFullYear() + "-" + month + "-" + date])(
    'expect %s to be a valid date of birth', (dob) => {
        expect(checkDobValidity(dob)).toBe(true)
    }
)

// Date of birth is in valid format but does not exist.
test.each(["2000-12-32", "2001-02-31", "2000/04/31", "2000/01/00"])(
    'expect %s to be an invalid date of birth', (dob) => {
        expect(checkDobValidity(dob)).toBe(false)
    }
)
// Get tomorrow's date
const tomorrow = new Date();
tomorrow.setDate(tomorrow.getDate() + 1);
let month_tomorrow = (tomorrow.getMonth() + 1).toString();
if (month_tomorrow.length == 1) {
    month_tomorrow = "0" + month_tomorrow;
}
let date_tomorrow = tomorrow.getDate().toString();
if (date_tomorrow.length == 1) {
    date_tomorrow = "0" + date_tomorrow;
}
// Date of birth is in valid format and is in future.
test.each(["3000-12-32", "2021-02-28", tomorrow.getFullYear() + "-" + month_tomorrow + "-" + date_tomorrow])(
    'expect %s to be an invalid date of birth', (dob) => {
        expect(checkDobValidity(dob)).toBe(false)
    }
)

// Date of birth is in valid format and before 1900-01-01.
test.each(["1000-12-30", "1899-12-31", "0000-01-01"])(
    'expect %s to be an invalid date of birth', (dob) => {
        expect(checkDobValidity(dob)).toBe(false)
    }
)

// Date given is in invalid format
test.each(["Today", "Wednesday 24th June, 2000"])(
    'expect %s to be an invalid date of birth', (dob) => {
        expect(checkDobValidity(dob)).toBe(false)
    }
)

// Date given is the empty string
test('expect %s to be an invalid date of birth', () => {
        expect(checkDobValidity("")).toBe(false)
    }
)

// Date given is undefined
test('expect %s to be an invalid date of birth', () => {
        expect(checkDobValidity(undefined)).toBe(false)
    }
)

// must be one of “Male”, “Female”, “Non-binary”. casing should be ignored.
// Gender is one of three valid options (male, female, non-binary) with any casing.
test.each(["Male", "female", "non-BINARY", "FeMaLe"])(
    'expect %s to be a valid gender', (gender) => {
        expect(checkGenderValidity(gender)).toBe(true)
    }
)

// Gender is not one of three valid options (male, female, non-binary).
test.each(["n/a", "M", "woman", "nothing"])(
    'expect %s to be an invalid gender', (gender) => {
        expect(checkGenderValidity(gender)).toBe(false)
    }
)

// Gender given is the empty string.
test('expect %s to be an invalid gender', () => {
        expect(checkGenderValidity("")).toBe(false)
    }
)

// Gender given is undefined.
test('expect %s to be an invalid gender', () => {
    expect(checkGenderValidity(undefined)).toBe(false)
}
)