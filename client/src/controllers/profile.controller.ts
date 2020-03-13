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

export async function editProfile(user: UserApiFormat) {
    try {
        await checkProfileValidity(user);
        await saveCurrentUser(user);
    } catch (err) {
        alert(err);
    }    
}

async function checkProfileValidity(formData: UserApiFormat) {
    if (!formData.lastname || formData.lastname.length < 1) {
        throw new Error("no last name given")
      }
  
      if (formData.lastname.length > 30) {
          throw new Error("last name must be less than 30 characters")
      }
  
      if (hasNumber(formData.lastname)) {
          throw new Error("last name cannot contain numbers")
      }
  
      if (!formData.firstname || formData.firstname.length < 1) {
          throw new Error("no first name given")
      }
  
      if (formData.firstname.length > 30) {
          throw new Error("first name must be less than 30 characters")
      }
  
      if (hasNumber(formData.firstname)) {
          throw new Error("first name cannot contain numbers")
      }
  
      if (formData.middlename && formData.middlename.length > 30) {
          throw new Error("middle name must be less than 30 characters")
      }
  
      if (formData.middlename && hasNumber(formData.middlename)) {
          throw new Error("middle name cannot contain numbers")
      }
  
      if (formData.nickname && formData.nickname.length < 6) {
          throw new Error("nick name must be at least 6 characters long")
      }
  
      if (formData.nickname && hasWhiteSpace(formData.nickname)) {
          throw new Error("nickname cannot contain white space")
      }

      if (formData.bio && formData.bio.length < 8) {
          throw new Error("Bio must be at least 8 characters")
      }
  
      if (!formData.date_of_birth) {
          throw new Error("date of birth cannot be empty")
      }
      // date of birth input format is YYYY-MM-DD
      const date = Date.parse(formData.date_of_birth);
          if (isNaN(date)) {
          throw new Error('valid date not given')
      }
  
      if (date > Date.now()) {
          throw new Error("date of birth cannot be in the future")
      }
  
      if (!formData.gender) {
          throw new Error("no gender given")
      }

  }