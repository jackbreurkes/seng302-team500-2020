import { getCurrentUser, saveUser, updateCurrentPassword, getProfileById, saveActivityTypes, updateEmails, deleteAccount, getUserLocation } from '../models/user.model'
import * as auth from "../services/auth.service";
import { loadPassportCountries } from '../models/countries.model';
import { UserApiFormat } from '@/scripts/User';
import FormValidator from '../scripts/FormValidator';
import { getAvailableActivityTypes } from './activity.controller';
import { checkCountryExistence } from '../models/location.model';
import { LocationInterface } from '@/scripts/LocationInteface'
import { LocationCoordinatesInterface } from '@/scripts/LocationCoordinatesInterface';

let formValidator = new FormValidator();

export async function logoutCurrentUser() {
    await auth.logout();
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

let _results: boolean;
/**
 * This check country validity simply checks if the user inputted string is an actual city.
 * formatting is done in back end validator
 * @param location string input from user
 * @param force default value
 */
export async function checkCountryValidity(location: string, force = false): Promise<boolean> {
    if(location == ',,'){
        return true
    }
    if (_results === undefined || force) {
        let results: Array<{class: string, type: string, address: string}> = await checkCountryExistence(location);
        for (let i=0; i < results.length; i++) {
            if ((results[i].class === "boundary" && results[i].type === "administrative") || (results[i].class === "place" && results[i].type === "city") ) {
                return true;
            }
        }
        
    }
    return false;
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
 * Update the user's primary and secondary emails by sending a request to the backend
 * Checks all emails are valid email addresses and not null
 * Throws error if no user with the profile id given, the primary email is invalid, any of the secondary emails is invalid, there are more than 5 emails total
 * @param primaryEmail email to set as the user's new primary email
 * @param additionalEmails list of secondary emails to have associated with the user
 * @param profileId the id of the profile belonging to the user being updated
 */
export async function updateUserEmails(primaryEmail: string, additionalEmails: string[], profileId: number) {
    let user = await getProfileById(profileId);
    if (user === null) {
        throw new Error("user not found");
    }

    if (primaryEmail == null || !formValidator.isValidEmail(primaryEmail)) {
        throw new Error("Invalid primary email " + primaryEmail);
    }
    for (let i = 0; i < additionalEmails.length; i++) {
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
 * Gets the specified user's profile including lat/lon, or null if the profile does not have a location.
 * @param profileId Profile ID to get the city location of
 * @returns the profile's set location, or null if the profile does not have a location
 */
export async function getProfileLocation(profileId: number): Promise<LocationCoordinatesInterface | null> {
    try {
        let location =  await getUserLocation(profileId);
        if (isNaN(location.lat) || isNaN(location.lon)) {
            return null;
        } else {
            return location;
        }
    } catch (e) {
        return null;
    }
}

/**
 * Update the profile information of the user supplied.
 * @param user user to update the information of
 */
export async function persistChangesToProfile(updatedProfile: UserApiFormat, profileId: number) {
    if (updatedProfile.location) {
        let city = updatedProfile.location.city;
        let state = updatedProfile.location.state;
        let country = updatedProfile.location.country;
        let location;
        if (state === undefined) {
            location = `${city},${country}`
        } else {
            location = `${city},${state},${country}`
        }
        if (!(await checkCountryValidity(location))) {
            throw new Error("The location you entered could not be found.")

        } else if (city !== "" && state !== "" && country !== ""){
            let validLocation: LocationInterface = {city, state, country};
            updatedProfile.location = validLocation;
        }
    }
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
 * Delete the user account associated with the given profile number
 * @param profileId ID number of profile to delete
 */
export async function deleteUserAccount(profileId: number) {
    await deleteAccount(profileId);
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

