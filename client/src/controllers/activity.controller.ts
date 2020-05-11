import { CreateActivityRequest } from '../scripts/Activity';
import * as activityModel from '../models/activity.model'
import { loadAvailableActivityTypes, createActivity } from '../models/activity.model'
import { create } from '@/models/user.model';


let _availableActivityTypes: string[] | null = null;
export async function getAvailableActivityTypes(force = false) {
  if (_availableActivityTypes === null || force) {
    _availableActivityTypes = await activityModel.loadAvailableActivityTypes();
  }
  return _availableActivityTypes;
}

export async function createNewActivity(createActivityRequest: CreateActivityRequest, profileId: number) {
  if (!validateActivityName(createActivityRequest.activity_name)) {
    throw new Error("Please enter an activity name of 4-30 characters long");
  }
  if (!hasTimeFrame(createActivityRequest.continuous)) {
    throw new Error("Please select a time frame");
  }
   if (!createActivityRequest.continuous) {
     if (createActivityRequest.start_time == undefined){
       throw new Error("Please enter the start date")
       //currently not being picked up
     }
   }
     
  //   if (!isFutureDate(start_date)) {
  //     throw new Error("Start date must be in the future")
  //   }
  //   if (!isValidEndDate(start_date, end_date)) {
  //     throw new Error("End date must be in the future and after the start date");
  //   }

  // }
  if (!validateDescription(createActivityRequest.description)) {
    throw new Error("Description must be at least 8 characters or omitted completely.");
  }
  if (createActivityRequest.location == undefined) {
    throw new Error("Please enter the location of the activity")
  }
  if (createActivityRequest.activity_type == [] || createActivityRequest.activity_type == undefined) {
    throw new Error("Please select at least one activity type");
  }
  await createActivity(createActivityRequest, profileId);
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
export function setStartDate(createActivityRequest: CreateActivityRequest, dateString: string, time: string) {
  let date = new Date(dateString)
  let offset = date.getTimezoneOffset();
  createActivityRequest.start_time = dateString + "T" + time + ":00" + offset;
}

/**
 * Returns only the date of the given ISO format date
 * @param dateString full ISO format date string
 * @returns only the date as string
 */
export function getDateFromISO(dateString: string | undefined): string {
  if (dateString == undefined) {
    dateString = "";
  }
  var date = new Date(dateString);
  return date.toISOString().substring(0, 10);
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
export function validateActivityName(activityName: string | undefined): boolean {
  if (activityName == undefined) {
    return false;
  }
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


/**
 * Edit an activity
 * @param createActivityRequest Data related to the activity to edit
 * @param profileId Profile ID this activity belongs to
 * @param activityId Activity ID to edit
 */
export async function editActivity(createActivityRequest: CreateActivityRequest, profileId: number, activityId: number) {
  await activityModel.editActivity(createActivityRequest, profileId, activityId);
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
export function describeDurationTimeFrame(startTime: string, endTime: string) {
  let start = new Date(startTime);
  let end = new Date(endTime);
  const dtf = new Intl.DateTimeFormat(undefined, {
    year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric', timeZoneName: 'short'
  });
  return "from " + dtf.format(start) + " to " + dtf.format(end);
}

export const INVALID_CONTINUOUS_MESSAGE = "please pick between continuous or duration"
export function hasTimeFrame(timeFrame: boolean | undefined): boolean {
  if (timeFrame == undefined) {
    return false;
  } else {
    return true;
  }
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