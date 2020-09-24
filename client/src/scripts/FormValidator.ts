import { UserApiFormat } from './User';
export default class FormValidator {
    MIN_DATE = '1900-01-01';
    MIN_DATE_STRING = '01/01/1900';
    FIRST_NAME_ERROR_STRING = "First name must be between 1 and 30 characters (inclusive) and contain no numbers";
    LAST_NAME_ERROR_STRING = "Last name must be between 1 and 30 characters (inclusive) and contain no numbers";
    MIDDLE_NAME_ERROR_STRING = "Middle name must be between 1 and 30 characters (inclusive) and contain no numbers";
    NICKNAME_ERROR_STRING = "Nickname must be at least 6 characters and contain no whitespace characters";
    BIO_ERROR_STRING = "Bio must be at least 8 characters";
    DOB_ERROR_STRING = "Date of birth must be before the current date, after " + this.MIN_DATE_STRING + " and given in the format dd/mm/yyyy";
    GENDER_ERROR_STRING = "Gender must be one of: male, female, or non-binary";
    EMAIL_ERROR_STRING = "Email is not valid";
    PASSWORD_ERROR_STRING = "Password must be at least 8 characters";

    isValidEmail(email: string) {
        // RegEx taken from https://emailregex.com/
        // eslint-disable-next-line no-useless-escape
        var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }
      
    hasNumber(myString: string) {
        return /\d/.test(myString);
    }
      
    hasWhiteSpace(myString: string) {
        return /\s/g.test(myString);
    }
    
    checkNameValidity(name: string) {
        if (name.length < 1) {
            return false;
        }
    
        if (name.length > 30) {
            return false;
        }
    
        if (this.hasNumber(name)) {
            return false;
        }
    
        return true;
    }
    
    checkFirstnameValidity(firstname: string | undefined) {
        if (!firstname) {
            return false;
        } else {
            return this.checkNameValidity(firstname);
        }
    }
    
    checkLastnameValidity(lastname: string | undefined) {
        if (!lastname) {
            return false;
        } else {
            return this.checkNameValidity(lastname);
        }
    
    }
    
    checkMiddlenameValidity(middlename: string | undefined) {
        if (!middlename) {
            return true;
        } else {
            return this.checkNameValidity(middlename);
        }
    }
    
    checkNicknameValidity(nickname: string | undefined) {
        if (nickname && nickname.length < 6) {
            return false;
        }

        if (nickname && nickname.length > 30) {
            return false;
        }
    
        if (nickname && this.hasWhiteSpace(nickname)) {
            return false;
        }
    
        return true;
    }
    
    checkBioValidity(bio: string | undefined) {
        if (bio && bio.length < 8) {
            return false;
        }
    
        return true;
    }
    
    checkDobValidity(date_of_birth: string | undefined) {
        if (!date_of_birth) {
            return false;
        }

        if (date_of_birth.length != 10 || date_of_birth[4] != "-" || date_of_birth[7] != "-") {
            return false;
        }

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

        if (date < Date.parse(this.MIN_DATE)) {
            return false;
        }
    
        return true;
    }
    
    checkGenderValidity(gender: string | undefined) {
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
    checkPasswordValidity(password: string): boolean {
        if (password.length < 8) {
            return false;
        }
    
        return true;
    }

    /**
     * checks if the result of an activityoutcome is valid
     * @param result result to validate
     * @returns true if valid, else false
     */
    checkResultValidity(result: string): boolean {
        return result.length > 0 && result.length <= 30;
    }
}