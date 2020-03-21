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
        throw new Error("no first name given");
    }

    if (firstname.length > 30) {
        throw new Error("first name must be less than 30 characters");
    }

    if (hasNumber(firstname)) {
        throw new Error("first name cannot contain numbers");
    }

    return true;
}

export function checkLastnameValidity(lastname: any) {
    if (!lastname || lastname.length < 1) {
        throw new Error("no last name given");
      }
  
    if (lastname.length > 30) {
        throw new Error("last name must be less than 30 characters");
    }
  
    if (hasNumber(lastname)) {
        throw new Error("last name cannot contain numbers");
    }

    return true;

    }


export function checkMiddlenameValidity(middlename: any) {
        if (middlename && middlename.length > 30) {
            throw new Error("middle name must be less than 30 characters");
        }
    
        if (middlename && hasNumber(middlename)) {
            throw new Error("middle name cannot contain numbers");
        }
    
        return true;
    }

export function checkNicknameValidity(nickname: any) {
        if (nickname && nickname.length < 6) {
            throw new Error("nick name must be at least 6 characters long");
        }
    
        if (nickname && hasWhiteSpace(nickname)) {
            throw new Error("nickname cannot contain white space");
        }
    
        return true;
    }

export function checkBioValidity(bio: any) {
        if (bio && bio.length < 8) {
            throw new Error("Bio must be at least 8 characters");
        }
    
        return true;
    }

export function checkDobValidity(date_of_birth: any) {
        if (!date_of_birth) {
            throw new Error("date of birth cannot be empty");
        }
        const date = Date.parse(date_of_birth);
            if (isNaN(date)) {
                throw new Error('valid date not given');
        }

        if (date > Date.now()) {
            throw new Error("date of birth cannot be in the future");
        }
    
        return true;
    }

export function checkGenderValidity(gender: string | undefined) {
    if (!gender) {
        throw new Error("no gender given");
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