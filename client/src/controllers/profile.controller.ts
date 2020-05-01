import { loadPassportCountries } from '../models/countries.model';
import { logout, getCurrentUser, saveUser, updateCurrentPassword, addEmail, updatePrimaryEmail, deleteUserEmail, getProfileById, addUserActivityType, removeUserActivityType } from '../models/user.model'
import { UserApiFormat } from '@/scripts/User';
import FormValidator from '../scripts/FormValidator';

let formValidator = new FormValidator();

export async function logoutCurrentUser() {
    await logout();
}

let _passportCountryNames: Array<string>;  // cache for passport country names
/**
 * loads the list of valid passport country names from the cache
 * if the cache is empty, polls the rest countries API
 * @param force whether the cache should be forced to update, default false
 */
export async function getAvailablePassportCountries(force = false): Promise<Array<string>> {
    if (_passportCountryNames === undefined || force) {
        let passportCountries: Array<{name: string}> = await loadPassportCountries();
        _passportCountryNames = passportCountries.map(country => country.name);
    }
    return _passportCountryNames;
}

/**
 * adds a passport country to a profile object
 * does not persist changes to the database
 * @param countryName the name of the country to add to the profile
 * @param profile the profile to add the passport country to
 */
export async function addPassportCountry(countryName: string, profile: UserApiFormat) {
    if (!profile.passports) {
        profile.passports = []
    }
    if (!(await getAvailablePassportCountries()).includes(countryName)) {
        throw new Error(`${countryName} is not recognised as a country`)
    }
    if (profile.passports.includes(countryName)) {
        throw new Error(`the target profile already has ${countryName} as a passport country`)
    }

    profile.passports.push(countryName);
}

/**
 * adds a passport country to a profile object
 * does not persist changes to the database
 * @param countryName the name of the country to remove from the profile
 * @param profileId the profile to remove the passport country from
 */
export function deletePassportCountry(countryName: string, profile: UserApiFormat) {

    if (!profile.passports) {
        profile.passports = []
    }

    if (!profile.passports.includes(countryName)) {
        throw `the target user does not have ${countryName} as a passport country`;
    }

    profile.passports.splice(profile.passports.indexOf(countryName), 1);
}

let loggedInUser: UserApiFormat = {};

/**
 * implemented by Alex Hobson, seems to cache the current user and save it to a class variable
 * @param force force a cache update
 */
export async function fetchCurrentUser(force = false) {
    if (force || !loggedInUser.primary_email) {
        loggedInUser = await getCurrentUser();
        if (loggedInUser === null) {
            throw new Error("no active user found");
        }
    }
    return loggedInUser;
}


/**
 * fetches the profile of the user with the given ID
 */
export async function fetchProfileWithId(profileId: number) {
    return await getProfileById(profileId);
}


export async function updatePassword(oldPassword: string, newPassword: string, repeatPassword: string, profileId: number) {
    if (!formValidator.checkPasswordValidity(newPassword)) {
        throw new Error("new password must be at least 8 characters")
    }
    if (newPassword !== repeatPassword){
        throw new Error("new password and repeat password do not match");
    }
    await updateCurrentPassword(oldPassword, newPassword, repeatPassword, profileId);
}


/**
 * Register supplied email to the user by adding it to their additional emails list and communicating this to the database (via user.model method).
 * Throws error if is no current user or the user has five emails registered already (including their primary email).
 * @param newEmail String of email to be registered under user's profile.
 */
export async function addNewEmail(newEmail: string, profileId: number) {
    if(formValidator.isValidEmail(newEmail)) {
        let user = await getProfileById(profileId);
        if (user === null) {
            throw new Error("user not found");
        }
        if (user.additional_email === undefined) {
            user.additional_email = []
        } else if (user.additional_email.length >= 4) {
            throw new Error("Maximum number of emails reached (5).");
        }
        await addEmail(newEmail, profileId); 
    }
}

/**
 * Remove the specified email from the user's list of additional emails.
 */
export async function deleteEmail(email: string, profileId: number) {
    let user = await getProfileById(profileId);
    if (user === null) {
        throw new Error("user not found");
    }

    if (user.additional_email !== undefined) {
        await deleteUserEmail(email, profileId);
    } else {
        throw new Error("No additional emails to delete.");
    }
}

/**
 * Email must be registered to user before it can be set as their primary email
 * @param primaryEmail the email to be set as the primary email
 */
export async function setPrimary(primaryEmail: string, profileId: number) {
    let user = await getProfileById(profileId);
    if (user === null) {
        throw new Error("user not found");
    }
    if (user.additional_email !== undefined && user.additional_email.length > 0) {  // Need additional emails available to be set as primary
        await updatePrimaryEmail(primaryEmail, profileId);
    } else {
        throw new Error("Must have additional emails to update it with.");
    }
}

/**
 * Add the given activity type to the user's profile.
 * @param activityType activity type to add to the user's profile
 */
export async function addActivityType(activityType: string) {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("No active user found.");
    }
    await addUserActivityType(activityType);
}

/**
 * Remove the supplied activity type from the user's profile.
 * @param activityType activity type to remove from user's profile
 */
export async function removeActivityType(activityType: string) {
    let user = await getCurrentUser();
    if (user === null) {
        throw new Error("No active user found.");
    }
    if (!user.activities || user.activities.length === 0) {
        throw new Error("User has no activity types associated with their profile.");
    }
    await removeUserActivityType(activityType);
}

/**
 * Update the profile information of the user supplied.
 * @param user user to update the information of
 */
export async function persistChangesToProfile(updatedProfile: UserApiFormat, profileId: number) {
    if (await checkProfileValidity(updatedProfile)) {
        await saveUser(updatedProfile, profileId);
    } else {
        throw new Error("Profile is not valid.");
    }
}

/**
 * Check if the profile information is valid according to defined rules. Returns true if valid, false if not.
 * @param formData profile information to validate, supplied in the form of a user's profile
 */
function checkProfileValidity(formData: UserApiFormat) {
    
    return formValidator.checkFirstnameValidity(formData["firstname"]) &&
    formValidator.checkLastnameValidity(formData["lastname"]) &&
    formValidator.checkMiddlenameValidity(formData["middlename"]) &&
    formValidator.checkNicknameValidity(formData["nickname"]) &&
    formValidator.checkBioValidity(formData["bio"]) &&
    formValidator.checkDobValidity(formData["date_of_birth"]) &&
    formValidator.checkGenderValidity(formData["gender"]);
  }

