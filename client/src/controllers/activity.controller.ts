import { CreateActivityRequest } from '../scripts/Activity';
import * as activityModel from '../models/activity.model'


let _availableActivityTypes: string[] | null = null;
export async function getAvailableActivityTypes(force = false) {
  if (_availableActivityTypes === null || force) {
    _availableActivityTypes = await activityModel.loadAvailableActivityTypes();
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
    throw new Error(`${activityType} is already added to the activity`)
  }
  if (!(await getAvailableActivityTypes()).includes(activityType)) {
    throw new Error(`activity type ${activityType} does not exist`)
  }
  createActivityRequest.activity_type.push(activityType);
}

/**
 * removes an activity type to an activity without persisting changes to the backend
 * @param activityType the activity type to remove from the activity
 * @param createActivityRequest the activity to remove the activity type from
 */
export function removeActivityType(activityType: string, createActivityRequest: CreateActivityRequest) {

  if (!createActivityRequest.activity_type) {
    createActivityRequest.activity_type = [];
  }

  if (!createActivityRequest.activity_type.includes(activityType)) {
    throw `${activityType} has not been added to the activity`;
  }
  for (let i = 0; i < createActivityRequest.activity_type.length; i++) {
    if (createActivityRequest.activity_type[i] === activityType) {
      createActivityRequest.activity_type.splice(i, 1);
    }
  }
  // createActivityRequest.activity_type.splice(createActivityRequest.activity_type.indexOf(activityType), 1);

}

/**
 * combines start date, time and timezone to a string as per API spec
 * @param createActivityRequest the activity to remove the activity type from
 * @param dateString start date in string format
 * @param time start time in string format
 */
export async function setStartDate(createActivityRequest: CreateActivityRequest, dateString: string, time: string) {
  let date = new Date(dateString)
  let offset = date.getTimezoneOffset();
  createActivityRequest.start_time = dateString + "T" + time + ":00" + offset;
}

/**
 * combines start date, time and timezone to a string as per API spec
 * @param createActivityRequest the activity to remove the activity type from
 * @param dateString end date in string format
 * @param time end time in string format
 */
export async function setEndDate(createActivityRequest: CreateActivityRequest, dateString: string, time: string) {
  let date = new Date(dateString)
  let offset = date.getTimezoneOffset();
  createActivityRequest.end_time = dateString + "T" + time + ":00" + offset;
}


export const INVALID_ACTIVITY_NAME_MESSAGE = "activity name must be between 4 and 30 characters"
export function validateActivityName(activityName: string): boolean {
  if (activityName.length >= 4 && activityName.length <= 30) {
    return true;
  } else {
    return false;
  }
}

export const INVALID_DESCRIPTION_MESSAGE = "description must be at least 8 characters"
export function validateDescription(activityDescription: string | undefined): boolean {
  if (activityDescription == undefined || activityDescription.length >= 8 || activityDescription == "") {
    return true;
  } else {
    return false;
  }
}

export const INVALID_ACTIVITY_TYPE = "activity type already added"
export function validateActivityType(activityType: string, createActivityRequest: CreateActivityRequest): boolean { 
  if(createActivityRequest.activity_type == undefined) {
    createActivityRequest.activity_type = [];
  }
  return createActivityRequest.activity_type.includes(activityType)
}

export async function createNewActivity(createActivityRequest: CreateActivityRequest, profileId: number) {
  await activityModel.createActivity(createActivityRequest, profileId);
}

/**
 * returns the activities created by a given creator.
 * @param creatorId the profileId of the creator of the activities
 */
export async function getActivitiesByCreator(creatorId: number) {
  return activityModel.getActivitiesByCreator(creatorId);
}

export const INVALID_DATE_MESSAGE = "date must be at least one day into the future"
export function isFutureDate(dateString: string): boolean {
  let today = new Date().getTime()
  let date = new Date(dateString).getTime()
  if ((today - date) < 0) {
    return true;
  } else {
    return false;
  }
}

/**
 * Gets an activity by a user's ID and the activity ID
 * 
 * @param {number} creatorId User the activity belongs to
 * @param {number} activityId Activity ID
 * @return {CreateActivityRequest} Retrieved activity data
 */
export async function getActivityById(creatorId: number, activityId: number) {
  return activityModel.getActivityById(creatorId, activityId);
}

/**
 * returns a reader-friendly description of the duration of a duration activity.
 * @param startTime the ISO datetime string representing the start time
 * @param endTime the ISO datetime string representing the end time
 */
export function descripeDurationTimeFrame(startTime: string, endTime: string) {
  let start = new Date(startTime);
  let end = new Date(endTime);
  const dtf = new Intl.DateTimeFormat(undefined, {
    year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric', timeZoneName: 'short'
  });
  return "from " + dtf.format(start) + " to " + dtf.format(end);
}

export const INVALID_END_DATE_MESSAGE = "end date must be after start date and in YYYY-MM-DD format"
export function isValidEndDate(startDateString: string, endDateString: string): boolean 
{
  if (isFutureDate(endDateString)) {
    let endDate = new Date(endDateString).getTime();
    let startDate = new Date(startDateString).getTime();
    if ((startDate - endDate) < 0) {
      return true;
    } else {
      return false
    }
  } else {
    return false;
  }
}

export const INVALID_TIME_MESSAGE = "please fill out completely"
export function isValidTime(time: string): boolean {
  if (time == "") {
    return false;
  } else {
    return true;
  } 
}