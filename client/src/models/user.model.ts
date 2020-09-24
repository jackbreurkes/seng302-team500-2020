import { RegisterFormData } from "@/controllers/register.controller";
import { UserApiFormat } from "@/scripts/User";

import instance from "../services/axios.service";
import * as auth from "../services/auth.service";
import { LocationCoordinatesInterface } from '@/scripts/LocationCoordinatesInterface';

/**
 * Attempts to retrieve the current user's info using the saved ID via GET /{{SERVER_URL}}/profiles/<their ID>
 * For more endpoint information see file team-500/*.yaml
 */
export async function getCurrentUser() {
  let res = await instance.get("profiles/" + auth.getMyUserId());
  return res.data as UserApiFormat;
}

/**
 * Attempts to retrieve a profile's info using the given ID via GET /{{SERVER_URL}}/profiles/<their ID>
 * For more endpoint information see file team-500/*.yaml
 */
export async function getProfileById(profileId: number) {
  let res = await instance.get("profiles/" + profileId);
  return res.data as UserApiFormat;
}

/**
 * creates a user given the data from the register form. Returns the new user's profile ID.
 * For more endpoint information see file team-500/*.yaml
 * @param formData the register form data to use to create the user
 * @returns {number} the newly created user's user id
 */
export async function create(formData: RegisterFormData): Promise<number> {
  let userData = {
    lastname: formData.lastName,
    middlename: formData.middleName,
    firstname: formData.firstName,
    nickname: formData.nickname,
    primary_email: formData.email,
    date_of_birth: formData.dateOfBirth,
    bio: formData.bio,
    gender: formData.gender,
    passports: [],
    fitness: -1,
    additional_email: [],
    password: formData.password,
  };
  let res = await instance.post("profiles", userData);
  let user: UserApiFormat = res.data;
  return user.profile_id as number;
}

/**
 * Takes a user and then saves their profile information under the user id in local storage.
 * For more endpoint information see file team-500/*.yaml
 */
export async function saveUser(user: UserApiFormat, profileId: number) {
  if (profileId !== user.profile_id) {
    throw new Error(
      "cannot save a profile to an id different from that which appears in the user object"
    );
  }
  let notNullUser = user as any;
  for (let key in notNullUser) {
    if (notNullUser[key] === null) {
      delete notNullUser[key];
    }
  }
  await instance.put("profiles/" + profileId, notNullUser);
}

/**
 * sets a user's activity type interests.
 * @param selectedActivities the list of activity type names the user is interested in
 * @param profileId the id of the profile to update the interests of
 */
export async function updateActivityTypes(
  selectedActivities: string[],
  profileId: number
) {
  let activityDict = { activities: selectedActivities };
  await instance.put(
    "profiles/" + profileId + "/activity-types",
    activityDict
  );
}

/**
 * Register the specified email to the target profile by adding it to the list of additional emails.
 * @param newEmails the new emails to register to user
 * @param profileId the id of the profile to update
 */
export async function updateEmailList(newEmails: string[], profileId: number) {
  // TODO there should be no business logic in the model class
  let user = await getProfileById(profileId);
  let emails = user.additional_email;
  if (emails === undefined) {
    emails = newEmails;
  } else {
    emails = newEmails;
  }
  let emailDict = { additional_email: emails };
  await instance.post("profiles/" + profileId + "/emails", emailDict);
}

/**
 * update the user's emails by sending a request to the back end
 * @param primaryEmail the new primary email for the user
 * @param additionalEmails the list of secondary emails for the user
 * @param profileId the profile id of the user whose emails should be updated
 */
export async function updateEmails(
  primaryEmail: string,
  additionalEmails: string[],
  profileId: number
) {
  await instance.put("profiles/" + profileId + "/emails", null, {
    data: { additional_email: additionalEmails, primary_email: primaryEmail },
  });
}

/**
 * Saves a user's activity type interests using PUT /profiles/{profileId}/activity-types endpoint
 * @param user UserApiFormat user with already-updated (in controller class) list of activities
 * @param profileId the id of the profile whose activity type interests should be updated
 */
export async function saveActivityTypes(
  user: UserApiFormat,
  profileId: number
) {
  let body = { activities: user.activities };
  await instance.put(`profiles/${profileId}/activity-types`, body);
}

/**
 * updates a user's password to a new password.
 * @param old_password the user's old password
 * @param new_password the user's desired new password
 * @param repeat_password a repeat of the user's desired new password
 * @param profileId the profile id of the user whose password should be updated
 */
export async function updateCurrentPassword(
  old_password: string,
  new_password: string,
  repeat_password: string,
  profileId: number
) {
  await instance.put("profiles/" + profileId + "/password", {
    old_password,
    new_password,
    repeat_password,
  });
}

export async function deleteAccount(profileId: number) {
  await instance.delete("profiles/" + profileId);
}

/**
 * Gets the specified user's profile including lat/lon.
 * 
 * @param profileId Profile ID to get the city location of
 */
export async function getUserLocation(profileId: number) {
  let res = await instance.get("/profiles/"+profileId+"/latlon");
  return res.data as LocationCoordinatesInterface;
}