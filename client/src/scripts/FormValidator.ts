import { UserApiFormat } from './User';

export let firstnameErrorString = "";
export let lastnameErrorString = "";
export let middlenameErrorString = "";
export let nicknameErrorString = "";
export let bioErrorString = "";
export let dobErrorString = "";
export let genderErrorString = "";

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


export function checkFirstnameValidity(firstname: string | undefined) {
    if (!firstname || firstname.length < 1) {
        firstnameErrorString = "No first name given";
        return false;
    }

    if (firstname.length > 30) {
        firstnameErrorString = "First name must be less than 30 characters";
        return false;
    }

    if (hasNumber(firstname)) {
        firstnameErrorString = "First name cannot contain numbers";
        return false;
    }

    return true;
}

export function checkLastnameValidity(lastname: string | undefined) {
    if (!lastname || lastname.length < 1) {
        lastnameErrorString = "No last name given";
        return false;
    }
  
    if (lastname.length > 30) {
        lastnameErrorString = "Last name must be less than 30 characters";
        return false;
    }
  
    if (hasNumber(lastname)) {
        lastnameErrorString = "Last name cannot contain numbers";
        return false;
    }

    return true;

    }

export function checkMiddlenameValidity(middlename: string | undefined) {
        if (middlename && middlename.length > 30) {
            middlenameErrorString = "Middle name must be less than 30 characters";
            return false;
        }
    
        if (middlename && hasNumber(middlename)) {
            middlenameErrorString = "Middle name cannot contain numbers";
            return false;
        }
        return true;
    }

export function checkNicknameValidity(nickname: string | undefined) {
        if (nickname && nickname.length < 6) {
            nicknameErrorString = "Nickname must be at least 6 characters long";
            return false;
        }

        if (nickname && nickname.length > 30) {
            nicknameErrorString = "Nickname must be at most 30 characters long";
            return false;
        }
    
        if (nickname && hasWhiteSpace(nickname)) {
            nicknameErrorString = "Nickname cannot contain white space";
            return false;
        }
    
        return true;
    }

export function checkBioValidity(bio: string | undefined) {
        if (bio && bio.length < 8) {
            bioErrorString = "Bio must be at least 8 characters";
            return false;
        }
    
        return true;
    }

export function checkDobValidity(date_of_birth: string | undefined) {
        if (!date_of_birth) {
            dobErrorString = "Date of birth cannot be empty";
            return false;
        }
        const date = Date.parse(date_of_birth);
            if (isNaN(date)) {
                dobErrorString = 'Valid date not given';
                return false;
}

        if (date > Date.now()) {
            dobErrorString = "Date of birth cannot be in the future";
            return false;
        }

        if (date < Date.parse("1900/01/01")) {
            dobErrorString = "Date of birth cannot be before 1900-01-01";
            return false;
        }
    
        return true;
    }

export function checkGenderValidity(gender: string | undefined) {
    if (!gender) {
        genderErrorString = "No gender given";
        return false;
    }

    let genders = ["male", "female", "non-binary"];
    if (!genders.includes(gender.toLowerCase())) {
        genderErrorString = "Gender must be either Male, Female or Non-Binary.";
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
