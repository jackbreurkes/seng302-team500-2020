import { logout, getCurrentUser, saveCurrentUser } from '../models/user.model'
import { hasNumber, hasWhiteSpace, isValidEmail } from '@/scripts/LoginRegisterHelpers';
import { UserApiFormat } from '@/scripts/User';


export async function logoutCurrentUser() {
    await logout();
}

export async function addPassportCountry(country: any, userEmail: string) {
    const countryName = country.name || null
    if (countryName === null) {
        throw new Error("country not found");
    }

    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("current user not found")
    }
    console.log(user)
    if (!user.passports) {
        user.passports = []
    }
    if (user.passports.includes(countryName)) {
        throw new Error("you already have this as a passport country")
    }

    user.passports.push(countryName);
    await saveCurrentUser();

}


export async function fetchCurrentUser() {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("no active user found");
    }
    return user;
}


/*export async function updatePassword(oldPassword: string, newPassword: string, repeatPassword: string) {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("no active user found");
    }

    if(user.password === oldPassword){
        if(newPassword === repeatPassword){
            user.password = newPassword
          alert("password changed")
        }
        else {
          alert("passwords do not match")
        }
      }
      else{
        alert("incorrect current password")
      }
      
    await saveCurrentUser(user);
}*/



export async function setFitnessLevel(fitnessLevel: number, profileId: number) {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("no active user found");
    }
    if (user.fitness !== fitnessLevel) {
        console.log("User fitness level changed");
        user.fitness = fitnessLevel;
    }

    await saveCurrentUser();
}

export async function addEmail(newEmail: string) {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("no active user found");
    }
    if (!user.additional_email) {
        user.additional_email = []
    }
    user.additional_email.push(newEmail);
    await saveCurrentUser();
}

export async function deleteEmail(email: string) {
    let user = await getCurrentUser();
    if (user.additional_email) {
        if (user === null) {
            throw new Error("no active user found");
        }
        for (let index = 0; index < user.additional_email.length; index++) {
            if (email === user.additional_email[index]) {  
                user.additional_email.splice(index, 1);
            }
        }
        await saveCurrentUser();
    } else {
        throw new Error("No additional emails to delete.");
    }
}

export async function setPrimary(email: string) {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("no active user found");
    }
    if (user.additional_email) {
        if (user.primary_email) {
            user.additional_email.push(user.primary_email);
            user.primary_email = email;
            for (let index = 0; index < user.additional_email.length; index++) {
                if (email === user.additional_email[index]) {  
                    user.additional_email.splice(index, 1);
                }
            }
        }
    }
    await saveCurrentUser();
}

export async function editProfile(user: UserApiFormat) {
    await checkProfileValidity(user);
    await saveCurrentUser();
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

export function checkGenderValidity(gender: any) {
    if (!gender) {
        throw new Error("no gender given");
    }
    
        return true;
    }

export function returnValidityIssue(func: any, value: any) {
    try {
        func(value);
        return true;
    } catch (err) {
        return err.message;
    }
}

function checkProfileValidity(formData: UserApiFormat) {
    checkFirstnameValidity(formData["firstname"]);
    checkLastnameValidity(formData["lastname"]);
    checkMiddlenameValidity(formData["middlename"]);
    checkNicknameValidity(formData["nickname"]);
    checkBioValidity(formData["bio"]);
    checkDobValidity(formData["date_of_birth"]);
    checkGenderValidity(formData["gender"]);

  }