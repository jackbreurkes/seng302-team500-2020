import { logout, getCurrentUser, saveUser, updateCurrentPassword, addEmail, updatePrimaryEmail, deleteUserEmail } from '../models/user.model'
import { UserApiFormat } from '@/scripts/User';
import { checkFirstnameValidity, checkLastnameValidity, checkMiddlenameValidity, checkNicknameValidity, checkBioValidity, checkDobValidity, checkGenderValidity, checkPasswordValidity } from '../scripts/FormValidator';
import * as FormValidator from '../scripts/FormValidator';

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

    if (!user.passports) {
        user.passports = []
    }
    if (user.passports.includes(countryName)) {
        throw new Error("you already have this as a passport country")
    }

    user.passports.push(countryName);
    await saveUser(user);

}

let loggedInUser: UserApiFormat = {};

export async function fetchCurrentUser(force = false) {
    if (force || !loggedInUser.primary_email) {
        loggedInUser = await getCurrentUser();
        if (loggedInUser === null) {
            throw new Error("no active user found");
        }
    }
    return loggedInUser;
}


export async function updatePassword(oldPassword: string, newPassword: string, repeatPassword: string) {
    if (!checkPasswordValidity(newPassword)) {
        throw new Error("new password must be at least 8 characters")
    }
    if (newPassword !== repeatPassword){
        throw new Error("new password and repeat password do not match");
    }
    await updateCurrentPassword(oldPassword, newPassword, repeatPassword);
}



export async function setFitnessLevel(fitnessLevel: number, profileId: number) {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("no active user found");
    }
    if (user.fitness !== fitnessLevel) {
        console.log("User fitness level changed");
        user.fitness = fitnessLevel;
    }

    await saveUser(user);
}

export async function addNewEmail(newEmail: string) {
    if(FormValidator.isValidEmail(newEmail)) {
        let user = await getCurrentUser();
        console.log(user)
        if (user === null) {
            throw new Error("no active user found");
        }
        if (user.additional_email === undefined) {
            user.additional_email = []
        } else if (user.additional_email.length >= 4) {
            throw new Error("Maximum number of emails reached (5).");
        }
        console.log(user.additional_email)
        await addEmail(newEmail); 
    }
}

export async function deleteEmail(email: string) {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("no active user found");
    }

    if (user.additional_email !== undefined) {
        await deleteUserEmail(email);
    } else {
        throw new Error("No additional emails to delete.");
    }
}

export async function setPrimary(primaryEmail: string) {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("No active user found");
    }
    if (user.additional_email !== undefined && user.additional_email.length > 0) {
        await updatePrimaryEmail(primaryEmail);
    } else {
        throw new Error("Must have additional emails to update it with.");
    }
}

export async function editProfile(user: UserApiFormat) {
    if (await checkProfileValidity(user)) {
        await saveUser(user);
        return true;
    } else {
        return false;
    }
}



function checkProfileValidity(formData: UserApiFormat) {
    
    return checkFirstnameValidity(formData["firstname"]) &&
    checkLastnameValidity(formData["lastname"]) &&
    checkMiddlenameValidity(formData["middlename"]) &&
    checkNicknameValidity(formData["nickname"]) &&
    checkBioValidity(formData["bio"]) &&
    checkDobValidity(formData["date_of_birth"]) &&
    checkGenderValidity(formData["gender"]);
  }

