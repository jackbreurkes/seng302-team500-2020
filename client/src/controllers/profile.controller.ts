import { getMyPermissionLevel, logout, getCurrentUser, saveUser, updateCurrentPassword, addEmail, updatePrimaryEmail, deleteUserEmail, getProfileById, updateEmailList, saveActivityTypes, updateEmails } from '../models/user.model'
import { loadPassportCountries } from '../models/countries.model';
import { UserApiFormat } from '@/scripts/User';
import FormValidator from '../scripts/FormValidator';
import { getAvailableActivityTypes } from './activity.controller';

let formValidator = new FormValidator();

export async function logoutCurrentUser() {
    await logout();
    loggedInUser = null;
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
 * deletes a passport country from a profile object
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

/**
 * adds an activity type to a profile object
 * does not persist changes to the database
 * @param activityType the name of the activity type to add to the profile
 * @param profile the profile to add the activity type to
 */
export async function addActivityType(activityType: string, profile: UserApiFormat) {
    if (!profile.activities) {
        profile.activities = []
    }
    if (!(await getAvailableActivityTypes()).includes(activityType)) {
        throw new Error(`${activityType} is not recognised as an activity type`)
    }
    if (profile.activities.includes(activityType)) {
        throw new Error(`the target profile already has ${activityType} as an interest`)
    }

    profile.activities.push(activityType);
}

/**
 * removes an activity type from a profile object
 * does not persist changes to the database
 * @param activityType the name of the activity type to remove from the profile
 * @param profile the profile to remove the activity type from
 */
export function deleteActivityType(activityType: string, profile: UserApiFormat) {

    if (!profile.activities) {
        profile.activities = []
    }

    if (!profile.activities.includes(activityType)) {
        throw `the target user does not have ${activityType} as an interest`;
    }

    profile.activities.splice(profile.activities.indexOf(activityType), 1);
}




let loggedInUser: UserApiFormat|null = null;

/**
 * implemented by Alex Hobson, seems to cache the current user and save it to a class variable
 * @param force force a cache update
 */
export async function fetchCurrentUser(force = false) {
    if (force || !loggedInUser) {
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

export function getPermissionLevel(): number {
    return getMyPermissionLevel();
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

export async function updateNewEmailList(newEmails: string[], profileId: number) {
    let user = await getProfileById(profileId);
    if (user === null) {
        throw new Error("user not found");
    }
    // if (user.additional_email === undefined) {
    //     user.additional_email = []
    // }
    await updateEmailList(newEmails, profileId);
}

export async function updateUserEmails(primaryEmail: string, additionalEmails: string[], profileId: number) {
    console.log(primaryEmail)
    console.log(additionalEmails)
    let user = await getProfileById(profileId);
    if (user === null) {
        throw new Error("user not found");
    }

    if (primaryEmail == null || !formValidator.isValidEmail(primaryEmail)) {
        throw new Error("Invalid primary email " + primaryEmail);
    }
    for (let i = 0; i < additionalEmails.length; i++) {
        console.log(additionalEmails[i])
        console.log(additionalEmails)
        if (additionalEmails[i] == null || !formValidator.isValidEmail(additionalEmails[i])) {
            throw new Error("Invalid email " + additionalEmails[i]);
        }
    }

    if (additionalEmails.length >= 5) { // This is currently including primary emails in the 5 email limit
        throw new Error("Exceeds maximum number of emails allowed (5)");
    }

    await updateEmails(primaryEmail, additionalEmails, profileId);
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
export async function addAndSaveActivityType(activityType: string, profileId: number) {
    let user = await getProfileById(profileId);
    if (user === null) {
        throw new Error("No active user found.");
    }
    if (user.activities === undefined) {
        user.activities = [activityType];
      } else {
        user.activities.push(activityType);
      }
    await saveActivityTypes(user, profileId);
}

/**
 * Remove the supplied activity type from the user's profile.
 * @param activityType activity type to remove from user's profile
 */
export async function removeAndSaveActivityType(activityType: string, profileId: number) {
    let user = await getProfileById(profileId);
    if (user === null) {
        throw new Error("No active user found.");
    }
    if (!user.activities || user.activities.length === 0) {
        throw new Error("User has no activity types associated with their profile.");
    }

    let index = user.activities.indexOf(activityType);
    if (user.activities.indexOf(activityType) == -1) {
      throw new Error("Activity is not associated with user's profile.");
    } else {
      user.activities.splice(index, 1);
    }
    await saveActivityTypes(user, profileId);
}

/**
 * Update the profile information of the user supplied.
 * @param user user to update the information of
 */
export async function persistChangesToProfile(updatedProfile: UserApiFormat, profileId: number) {
    if (await checkProfileValidity(updatedProfile)) {
        if (updatedProfile.activities === undefined) {
            updatedProfile.activities = []
        }
        await saveActivityTypes(updatedProfile, profileId);
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

