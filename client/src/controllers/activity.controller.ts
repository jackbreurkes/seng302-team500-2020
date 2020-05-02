import { CreateActivityRequest } from '../scripts/Activity';
import { loadAvailableActivityTypes } from '../models/activity.model'


let _availableActivityTypes: string[] | null = null;
export async function getAvailableActivityTypes(force = false) {
    if (_availableActivityTypes === null || force) {
        _availableActivityTypes = await loadAvailableActivityTypes();
    }
    return _availableActivityTypes;
}


/**
 * adds an activity type to an activity without persisting changes to the backend
 * @param activityType the activity type to add to the create activity request
 * @param createActivityRequest the create activity request to add the activity type to
 */
export async function addActivityType(activityType: string, createActivityRequest: CreateActivityRequest) {
    if (!createActivityRequest.activity_type) {
        createActivityRequest.activity_type = []
    }
    if (createActivityRequest.activity_type.includes(activityType)) {
        throw new Error(`${activityType} is already added to the activity`);
    }
    if (!(await getAvailableActivityTypes()).includes(activityType)) {
        throw new Error(`activity type ${activityType} does not exist`);
    }
    createActivityRequest.activity_type.push(activityType);
}

/**
 * removes an activity type to an activity without persisting changes to the backend
 * @param activityType the activity type to remove from the activity
 * @param createActivityRequest the activity to remove the activity type from
 */
export function removeActivityType(
  activityType: string,
  createActivityRequest: CreateActivityRequest
) {
  if (!createActivityRequest.activity_type) {
    createActivityRequest.activity_type = [];
  }
  for (let i = 0; i < createActivityRequest.activity_type.length; i++) {
    if (createActivityRequest.activity_type[i] === activityType) {
      createActivityRequest.activity_type.splice(i, 1);
    }
  }
}
