import { UserApiFormat } from './User';

export const MIN_DATE = '1900-01-01';

export const FIRST_NAME_ERROR_STRING = "First name must be between 1 and 30 characters (inclusive) and contain no numbers.";
export const LAST_NAME_ERROR_STRING = "Last name must be between 1 and 30 characters (inclusive) and contain no numbers.";
export const MIDDLE_NAME_ERROR_STRING = "Middle name must be between 1 and 30 characters (inclusive) and contain no numbers.";
export const NICKNAME_ERROR_STRING = "Nickname must be at least 6 characters and contain no whitespace characters.";
export const BIO_ERROR_STRING = "Bio must be at least 8 characters.";
export const DOB_ERROR_STRING = "Date of birth must be before the current date, after " + MIN_DATE + "and given in the format yyyy-mm-dd";
export const GENDER_ERROR_STRING = "Gender must be one of: male, female, or non-binary.";

export function isValidEmail(email: string) {
    // RegEx taken from https://emailregex.com/
    // eslint-disable-next-line no-useless-escape
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }
  
  export function hasNumber(myString: string) {
    return /\d/.test(myString);
  }
  
  export function hasWhiteSpace(myString: string) {
    return /\s/g.test(myString);
  }

function checkNameValidity(name: string) {
    if (name.length < 1) {
        return false;
    }

    if (name.length > 30) {
        return false;
    }

    if (hasNumber(name)) {
        return false;
    }

    return true;
}


export function checkFirstnameValidity(firstname: string | undefined) {
    if (!firstname) {
        return false;
    } else {
        return checkNameValidity(firstname);
    }
}

export function checkLastnameValidity(lastname: string | undefined) {
    if (!lastname) {
        return false;
    } else {
        return checkNameValidity(lastname);
    }

    }

export function checkMiddlenameValidity(middlename: string | undefined) {
        if (!middlename) {
            return true;
        } else {
            return checkNameValidity(middlename);
        }
    }

export function checkNicknameValidity(nickname: string | undefined) {
        if (nickname && nickname.length < 6) {
            return false;
        }

        if (nickname && nickname.length > 30) {
            return false;
        }
    
        if (nickname && hasWhiteSpace(nickname)) {
            return false;
        }
    
        return true;
    }

export function checkBioValidity(bio: string | undefined) {
        if (bio && bio.length < 8) {
            return false;
        }
    
        return true;
    }

export function checkDobValidity(date_of_birth: string | undefined) {
        if (!date_of_birth) {
            return false;
        }

        if (date_of_birth.length != 10 || date_of_birth[4] != "-" || date_of_birth[7] != "-") {
            return false;
        }

        console.log(date_of_birth.slice(0, 4))
        const date = Date.parse(date_of_birth);
            if (isNaN(date)) {
                return false;
        }

        const dateObj = new Date(date_of_birth);
        if (!(dateObj.getFullYear() == parseInt(date_of_birth.slice(0, 4)) && dateObj.getMonth() == parseInt(date_of_birth.slice(5, 7)) - 1 && dateObj.getDate() == parseInt(date_of_birth.slice(8, 10)))) {
            return false;
        }

        if (date > Date.now()) {
            return false;
        }

        if (date < Date.parse(MIN_DATE)) {
            return false;
        }
    
        return true;
    }

export function checkGenderValidity(gender: string | undefined) {
    if (!gender) {
        return false;
    }

    let genders = ["male", "female", "non-binary"];
    if (!genders.includes(gender.toLowerCase())) {
        return false;
    }
    
    return true;
}

/**
 * checks if a password is valid.
 * @param password the password to test as a string
 * @returns true if valid, else false
 */
export function checkPasswordValidity(password: string): boolean {
    if (password.length < 8) {
        return false;
    }

    return true;
}
