import FormValidator from './FormValidator'

let formValidator = new FormValidator();
var today = new Date().toISOString().slice(0, 10);
var tomorrow = new Date();
tomorrow.setDate(tomorrow.getDate() + 1);
var tomorrow_string = tomorrow.toISOString().slice(0, 10);

// password 8 or more characters
test.each(["password", "securepassword", "m0r3s3cur3P455w@rd", "eggs and bacon"])(
    'expect "%s" to be a valid password', (password) => {
    expect(formValidator.checkPasswordValidity(password)).toBe(true)
})

// password under 8 characters
test.each(["7charac", "eggs", ""])(
    'expect "%s" to be an invalid password', (password) => {
    expect(formValidator.checkPasswordValidity(password)).toBe(false)
})

// first name more than 1 character, 30 characters at most. Contains no numbers.
test.each(["test", "e", "wowitsareallylongnamethatscool"])(
    'expect %s to be a valid first name', (name) => {
        expect(formValidator.checkFirstnameValidity(name)).toBe(true)
    }
)

// valid first name as above and has a space character.
test.each(["Leigh Anne", "Bobby John"])(
    'expect %s to be a valid first name', (name) => {
        expect(formValidator.checkFirstnameValidity(name)).toBe(true)
    }
)

// valid first name as above and has a "-" character.
test.each(["Leigh-Anne", "Bobby-John"])(
    'expect %s to be a valid first name', (name) => {
        expect(formValidator.checkFirstnameValidity(name)).toBe(true)
    }
)

// first name more than 30 characters
test.each(["AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "wowitsareallylongnamethatsreallycool"])(
    'expect %s to be an invalid first name', (name) => {
        expect(formValidator.checkFirstnameValidity(name)).toBe(false)
    }
)

// first name more than 1 character, 30 characters at most. Contains number(s).
test.each(["H4rry", "Brianna42", "8"])(
    'expect %s to be an invalid first name', (name) => {
        expect(formValidator.checkFirstnameValidity(name)).toBe(false)
    }
)

// first name empty string.
test('expect "" to be an invalid first name', () => {
        expect(formValidator.checkFirstnameValidity("")).toBe(false)
    }
)

// first name is undefined.
test('expect undefined to be an invalid first name', () => {
    expect(formValidator.checkFirstnameValidity(undefined)).toBe(false)
}
)

// Last name is at least 1 character and less than or equal to 30 characters. Contains no numbers.
test.each(["Williams", "E", "Smith", "Lee", "Wowitsareallylongnamethatscool"])(
    'expect %s to be a valid last name', (name) => {
        expect(formValidator.checkLastnameValidity(name)).toBe(true)
    }
)

// Last name is valid as above and contains a space character.
test.each(["Pevensie Smith", "ALastnameA LastnameB LastnameC"])(
    'expect %s to be a valid last name', (name) => {
        expect(formValidator.checkLastnameValidity(name)).toBe(true)
    }
)

// Last name is valid as above and contains a "-" character.
test.each(["Smith-Lee", "Hyphen-Hyphen-Hyphen"])(
    'expect %s to be a valid last name', (name) => {
        expect(formValidator.checkLastnameValidity(name)).toBe(true)
    }
)

// Last name is over 30 characters. Contains no numbers.
test.each(["WilliamsIsNotAReallyLongLastNameNormally", "Wowitsareallylongnamethatiscool"])(
    'expect %s to be an invalid last name', (name) => {
        expect(formValidator.checkLastnameValidity(name)).toBe(false)
    }
)

// Last name is at least 1 character and less than or equal to 30 characters. Contains number(s).
test.each(["William5", "1", "Chan09"])(
    'expect %s to be an invalid last name', (name) => {
        expect(formValidator.checkLastnameValidity(name)).toBe(false)
    }
)

// Last name is the empty string.
test('expect "" to be an invalid last name', () => {
        expect(formValidator.checkLastnameValidity("")).toBe(false)
    }
)

// Last name is undefined.
test('expect undefined to be an invalid last name', () => {
    expect(formValidator.checkLastnameValidity(undefined)).toBe(false)
}
)

// Middle name is at least 1 character and less than or equal to 30 characters. Contains no numbers.
test.each(["Robin", "A", "John", "Anna", "Wowitsareallylongnamethatscool"])(
    'expect %s to be a valid middle name', (name) => {
        expect(formValidator.checkMiddlenameValidity(name)).toBe(true)
    }
)

// Middle name is valid as above and contains a space character.
test.each(["Anna Lee", "Robert John"])(
    'expect %s to be a valid middle name', (name) => {
        expect(formValidator.checkMiddlenameValidity(name)).toBe(true)
    }
)

// Middle name is valid as above and contains a "-" character.
test.each(["Anna-Lee", "Robert-John"])(
    'expect %s to be a valid middle name', (name) => {
        expect(formValidator.checkMiddlenameValidity(name)).toBe(true)
    }
)

// Middle name is over 30 characters. Contains no numbers.
test.each(["SomeReallyLongMiddleNameThatIsActuallyRidiculous", "Wowitsareallylongnamethatiscool"])(
    'expect %s to be an invalid middle name', (name) => {
        expect(formValidator.checkMiddlenameValidity(name)).toBe(false)
    }
)

// Middle name is at least 1 character and less than or equal to 30 characters. Contains number(s).
test.each(["William5", "1", "Chan09"])(
    'expect %s to be an invalid middle name', (name) => {
        expect(formValidator.checkMiddlenameValidity(name)).toBe(false)
    }
)

// Middle name is the empty string.
test('expect "" to be a valid middle name', () => {
        expect(formValidator.checkMiddlenameValidity("")).toBe(true)
    }
)

// Middle name is undefined.
test('expect undefined to be a valid middle name', () => {
    expect(formValidator.checkMiddlenameValidity(undefined)).toBe(true)
}
)

// Nickname is at least 6 characters and less than or equal to 30 characters. Contains no whitespace.
test.each(["NewUser64", "123456", "AAAAAA", "Wowitsareallylongnamethatsgr8!", "Wowitsareallylongnamethatscool"])(
    'expect %s to be a valid nickname', (name) => {
        expect(formValidator.checkNicknameValidity(name)).toBe(true)
    }
)

// Nickname is over 30 characters. Contains no whitespace.
test.each(["SomeReallyLongNicknameThatIsActuallyRidiculous!", "Wowitsareallylongnamethatiscool"])(
    'expect %s to be an invalid nickname', (name) => {
        expect(formValidator.checkNicknameValidity(name)).toBe(false)
    }
)

// Nickname is less than 6 characters (but at least one character). Contains no whitespace.
test.each(["Six!", "Short", "A"])(
    'expect %s to be an invalid nickname', (name) => {
        expect(formValidator.checkNicknameValidity(name)).toBe(false)
    }
)

// Nickname is at least 6 characters and less than or equal to 30 characters. Contains whitespace.
test.each(["I am a new user", " Whitespace", "Whitespace ", "\tTabbyTabTab", "\nAWholeNewLine"])(
    'expect %s to be an invalid nickname', (name) => {
        expect(formValidator.checkNicknameValidity(name)).toBe(false)
    }
)

// Nickname is the empty string.
test('expect "" to be a valid nickname', () => {
        expect(formValidator.checkNicknameValidity("")).toBe(true)
    }
)

// Nickname is undefined.
test('expect undefined to be a valid nickname', () => {
    expect(formValidator.checkNicknameValidity(undefined)).toBe(true)
}
)

// Bio is at least 8 characters.
test.each(["This is my bio!", "12345678", "AAAAAAAA", "A whole new bio has been made! 1234567890"])(
    'expect %s to be a valid bio', (bio) => {
        expect(formValidator.checkBioValidity(bio)).toBe(true)
    }
)

// Bio less than 8 characters.
test.each(["Short!", "A", "TinyBio", "A bio."])(
    'expect %s to be an invalid bio', (bio) => {
        expect(formValidator.checkBioValidity(bio)).toBe(false)
    }
)

// Bio is the empty string.
test('expect "" to be a valid bio', () => {
        expect(formValidator.checkBioValidity("")).toBe(true)
    }
)

// Bio is undefined.
test('expect undefined to be a valid bio', () => {
    expect(formValidator.checkBioValidity(undefined)).toBe(true)
}
)

// Date of birth is in valid format and is in the past and after or on 1900-01-01.
test.each(["2000-12-29", formValidator.MIN_DATE, today])(
    'expect %s to be a valid date of birth', (dob) => {
        expect(formValidator.checkDobValidity(dob)).toBe(true)
    }
);

// Date of birth is in valid format but does not exist.
test.each(["2000-12-32", "2001-02-31"])(
    'expect %s to be an invalid date of birth', (dob) => {
        expect(formValidator.checkDobValidity(dob)).toBe(false)
    }
)

// Date of birth is in valid format and is in future.
test.each(["3000-12-25", "2021-02-28", tomorrow_string])(
    'expect %s to be an invalid date of birth', (dob) => {
        expect(formValidator.checkDobValidity(dob)).toBe(false)
    }
)

// Date of birth is in valid format and before 1900-01-01.
test.each(["1000-12-30", "1899-12-31", "0000-01-01"])(
    'expect %s to be an invalid date of birth', (dob) => {
        expect(formValidator.checkDobValidity(dob)).toBe(false)
    }
)

// Date given is in invalid format
test.each(["Today", "Wednesday 24th June, 2000", "2000/01/01", "01-02-2003", "00-04-31", "2000-1-1"])(
    'expect %s to be an invalid date of birth', (dob) => {
        expect(formValidator.checkDobValidity(dob)).toBe(false)
    }
)

// Date given is the empty string
test('expect "" to be an invalid date of birth', () => {
        expect(formValidator.checkDobValidity("")).toBe(false)
    }
)

// Date given is undefined
test('expect undefined to be an invalid date of birth', () => {
        expect(formValidator.checkDobValidity(undefined)).toBe(false)
    }
)

// must be one of “male”, “female”, “non-binary”. casing should be ignored.
// Gender is one of three valid options (male, female, non-binary) with any casing.
test.each(["Male", "female", "non-BINARY", "FeMaLe"])(
    'expect %s to be a valid gender', (gender) => {
        expect(formValidator.checkGenderValidity(gender)).toBe(true)
    }
)

// Gender is not one of three valid options (male, female, non-binary).
test.each(["n/a", "M", "woman", "nothing"])(
    'expect %s to be an invalid gender', (gender) => {
        expect(formValidator.checkGenderValidity(gender)).toBe(false)
    }
)

// Gender given is the empty string.
test('expect "" to be an invalid gender', () => {
        expect(formValidator.checkGenderValidity("")).toBe(false)
    }
)

// Gender given is undefined.
test('expect undefined to be an invalid gender', () => {
    expect(formValidator.checkGenderValidity(undefined)).toBe(false)
}
)