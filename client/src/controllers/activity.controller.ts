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

export async function createNewActivity(sDate: string, sTime: string, eDate: string, eTime: string, 
    createActivityRequest: CreateActivityRequest, profileId: number) {
  if (!validateActivityName(createActivityRequest.activity_name)) {
    throw new Error("Please enter an activity name of 4-30 characters long");
  }
  if (!hasTimeFrame(createActivityRequest.continuous)) {
    throw new Error("Please select a time frame");
  }
   if (!createActivityRequest.continuous) {
     if (sDate == "" || sDate == undefined) {
       throw new Error("Duration based activities must have a start date");
     }
     if (!isValidTime(sTime) && sTime != "") {
      throw new Error("Start time is not in valid format");
    }
     if (eDate == "" || eDate == undefined) {
       throw new Error("Duration based activities must have an end date");
     }
     if (!isValidEndDate(sDate, eDate)) {
       throw new Error("End date must be after start date")
     }
     if (!isValidTime(eTime) && eTime != "") {
       throw new Error("End time is not in valid format");
     }
     createActivityRequest.start_time = setStartDate(sDate, sTime);
     createActivityRequest.end_time = setEndDate(eDate, eTime);
   }
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
export function setStartDate(dateString: string, time: string) {
  if (time == "") {
    time = "00:00"
  }
  let date = new Date(dateString)
  let offset = date.getTimezoneOffset();
  let offSetString = offset.toString();
  let newOffsetString;
  if (offSetString.length == 4) {
    newOffsetString = offSetString.slice(0, 1) + "0" + offSetString.slice(1, 2) + ":" + offSetString.slice(2, 4);
  } else {
    newOffsetString = offSetString.slice(0, 3) + ":" + offSetString.slice(3, 5);
  }
  return  dateString + "T" + time + ":00" + newOffsetString;
}

/**
 * Returns only the date of the given ISO format date
 * @param dateString full ISO format date string
 * @returns only the date as string
 */
export function getDateFromISO(dateString: string | number): string {
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
export function setEndDate(dateString: string, time: string) {
  if (time == "") {
    time = "00:00"
  }
  let date = new Date(dateString)
  let offset = date.getTimezoneOffset();
  let offSetString = offset.toString();
  let newOffsetString;
  if (offSetString.length == 4) {
    newOffsetString = offSetString.slice(0, 1) + "0" + offSetString.slice(1, 2) + ":" + offSetString.slice(2, 4);
  } else {
    newOffsetString = offSetString.slice(0, 3) + ":" + offSetString.slice(3, 5);
  }
  return dateString + "T" + time + ":00" + newOffsetString;
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
  let endDate = new Date(endDateString).getTime();
  let startDate = new Date(startDateString).getTime();
  if ((startDate - endDate) < 0) {
    return true;
  } else {
    return false
  }
}

export const INVALID_TIME_MESSAGE = "Please enter a valid time"
export function isValidTime(timeString: string): boolean {
  let re = /^\d{1,2}:\d{2}([ap]m)?$/;
  if (timeString.match(re)) {
    return true;
  } else {
    return false;
  }
}