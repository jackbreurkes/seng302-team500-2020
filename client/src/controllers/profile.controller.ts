import { logout, getCurrentUser, saveCurrentUser, updateCurrentPassword } from '../models/user.model'
import { UserApiFormat } from '@/scripts/User';
import { checkFirstnameValidity, checkLastnameValidity, checkMiddlenameValidity, checkNicknameValidity, checkBioValidity, checkDobValidity, checkGenderValidity, checkPasswordValidity } from '@/scripts/FormValidator';


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
    await saveCurrentUser(user);

}


export async function fetchCurrentUser() {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("no active user found");
    }
    return user;
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

    await saveCurrentUser(user);
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
    await saveCurrentUser(user);
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
        await saveCurrentUser(user);
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
    await saveCurrentUser(user);
}

export async function editProfile(user: UserApiFormat) {
    try {
        await checkProfileValidity(user);
        await saveCurrentUser(user);
    } catch (err) {
        alert(err);
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