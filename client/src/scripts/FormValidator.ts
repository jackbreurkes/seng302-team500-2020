import { UserApiFormat } from './User';

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


export function checkFirstnameValidity(firstname: any) {
    if (!firstname || firstname.length < 1) {
        return "no first name given";
    }

    if (firstname.length > 30) {
        return "first name must be less than 30 characters";
    }

    if (hasNumber(firstname)) {
        return "first name cannot contain numbers";
    }

    return true;
}

export function checkLastnameValidity(lastname: any) {
    if (!lastname || lastname.length < 1) {
        return "no last name given";
      }
  
    if (lastname.length > 30) {
        return "last name must be less than 30 characters";
    }
  
    if (hasNumber(lastname)) {
        return "last name cannot contain numbers";
    }

    return true;

    }

export function checkMiddlenameValidity(middlename: any) {
        if (middlename && middlename.length > 30) {
            return "middle name must be less than 30 characters";
        }
    
        if (middlename && hasNumber(middlename)) {
            return "middle name cannot contain numbers";
        }
        return true;
    }

export function checkNicknameValidity(nickname: any) {
        if (nickname && nickname.length < 6) {
            return "nick name must be at least 6 characters long";
        }
    
        if (nickname && hasWhiteSpace(nickname)) {
            return "nickname cannot contain white space";
        }
    
        return true;
    }

export function checkBioValidity(bio: any) {
        if (bio && bio.length < 8) {
            return "Bio must be at least 8 characters";
        }
    
        return true;
    }

export function checkDobValidity(date_of_birth: any) {
        if (!date_of_birth) {
            return "date of birth cannot be empty";
        }
        const date = Date.parse(date_of_birth);
            if (isNaN(date)) {
                return 'valid date not given';
        }

        if (date > Date.now()) {
            return "date of birth cannot be in the future";
        }
    
        return true;
    }

export function checkGenderValidity(gender: string | undefined) {
    if (!gender) {
        return "no gender given";
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

/*export function validateUserProfile(user: UserApiFormat) {
    return (typeof checkFirstnameValidity(user.firstname) === Boolean) &&
    checkLastnameValidity(user.lastname) &&
    checkMiddlenameValidity(user.middlename) &&
    checkNicknameValidity(user.nickname) &&
    checkBioValidity(user.bio) &&
    checkDobValidity(user.date_of_birth) &&
    checkGenderValidity(user.gender);
}*/