import { CreateActivityRequest } from "../scripts/Activity";
import instance from "../services/axios.service";
import { AxiosResponse } from 'axios';

/**
 * creates an activity.
 * @param data the data to use when creating the activity
 * @param profileId the id of the profile to be set as the activity creator
 */
export async function createActivity(
  data: CreateActivityRequest,
  profileId: number
) {
  let res = await instance.post(`/profiles/${profileId}/activities`, data);
  // TODO handle response
}

/**
 * updates an existing activity.
 * @param data the data to update the activity to
 * @param profileId the activity creator's profile id
 * @param activityId the id of the activity to update
 */
export async function editActivity(data: CreateActivityRequest, profileId: number,
  activityId: number) {
  let res = await instance.put(`/profiles/${profileId}/activities/${activityId}`, data);
  // TODO handle response
}

/**
 * loads the list of available activity types.
 * @returns the list of available activity type names
 */
export async function loadAvailableActivityTypes(): Promise<string[]> {
  let res = await instance.get("/activity-types");
  return res.data;
}

/**
 * returns all the activities created by a given creator.
 * @param creatorId the creator to return all the activities of
 */
export async function getActivitiesByCreator(creatorId: number): Promise<CreateActivityRequest[]> {
  let res = await instance.get(`/profiles/${creatorId}/activities`);
  return res.data;
}

/**
 * Gets an activity by its creator's id and the activity id
 * @param {number} creatorId user the activity was created by
 * @param {number} activityId id of the activity to retrieve
 * @return {CreateActivityRequest} retrieved activity data
 */
export async function getActivity(creatorId: number, activityId: number): Promise<CreateActivityRequest> {
  let res = await instance.get(`/profiles/${creatorId}/activities/${activityId}`);
  return res.data;
}


/**
 * Deletes an activity by its creator's id and the activity id
 * @param {number} creatorId user the activity was created by
 * @param {number} activityId the id of the activity to delete
 */
export async function deleteActivityById(creatorId: number, activityId: number) {
  await instance.delete(`/profiles/${creatorId}/activities/${activityId}`);
}

/**
 * Gets whether user follows the given activity
 * @param profileId profile id of the user
 * @param activityId id of the activity
 * @returns information about the server's response - data property includes whether the user is subscribed
 */
export async function getFollowingActivity(profileId: number, activityId: number): Promise<{"subscribed": boolean}> {
  let res = await instance.get(`/profiles/${profileId}/subscriptions/activities/${activityId}`)
  return res.data
} 

/**
 * Send request to have user follow the given activity
 * @param profileId profile id of the user
 * @param activityId id of the activity to follow
 */
export async function addActivityFollower(profileId: number, activityId: number) {
  let res = await instance.post(`/profiles/${profileId}/subscriptions/activities/${activityId}`)
} 

/**
 * Send request to have user unfollow the given activity
 * @param profileId profile id of the user
 * @param activityId id of the activity to unfollow
 */
export async function removeActivityFollower(profileId: number, activityId: number) {
  let res = await instance.delete(`/profiles/${profileId}/subscriptions/activities/${activityId}`)
} 

/**
 * gets the role of a user in a particular activity
 * @param profileId the profile to get the role of
 * @param activityId the activity the role is associated with
 */
export async function getActivityRole(profileId: number, activityId: number): Promise<string | null> {
  let res;
  try {
    res = await instance.get(`/activities/${activityId}/roles/${profileId}`)
  } catch (error) {
    if (error.response && error.response.status === 404) {
      return null;
    } else {
      throw error;
    }
  }
  return res.data;
}

/**
 * sets a users role in an activity
 * @param profileId the profile to set the role of
 * @param activityId the activity the role is associated with
 * @param role the role the profile should have
 */
export async function setActivityRole(profileId: number, activityId: number, role: string) {
  const data = {"role": role.toUpperCase()}
  await instance.put(`/activities/${activityId}/roles/${profileId}`, data)
}

/**
 * clears a user's role in a particular activity
 * @param profileId the profile whose roles should be cleared
 * @param activityId the activity the profile should be cleared from
 */
export async function removeActivityRole(profileId: number, activityId: number) {
  await instance.delete(`/activities/${activityId}/roles/${profileId}`)
}