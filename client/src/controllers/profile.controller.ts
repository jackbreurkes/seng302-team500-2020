import { logout, getCurrentUser, saveCurrentUser } from '../models/user.model'


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
}

export async function setFitnessLevel(fitnessLevel: number, userEmail: string) {
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
    user.additional_email.push(newEmail);
    await saveCurrentUser(user);
}