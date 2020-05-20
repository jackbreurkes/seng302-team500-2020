import { CreateActivityRequest } from "../scripts/Activity";
import instance from "../services/axios.service";

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
export async function getActivityById(creatorId: number, activityId: number): Promise<CreateActivityRequest> {
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
