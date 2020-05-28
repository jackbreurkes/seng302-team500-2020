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
 * Validates create activity request to be in proper syntax and format. Throws descriptive error
 * upon finding an element of the request not in expected form. Also sets start and end times if 
 * applicable
 * @param sDate user inputted start date (if not continuous)
 * @param sTime user inputted start time (optional)
 * @param eDate user inputted end date (if not continuous)
 * @param eTime ser inputted start time (optional)
 * @param createActivityRequest request form consisting of all other elements of the form
 * @param profileId user's id 
 */
export async function validateNewActivity(sDate: string, sTime: string, eDate: string, eTime: string, 
    createActivityRequest: CreateActivityRequest, profileId: number, isEditing: boolean, activityId: number | undefined) {
  if (!validateActivityName(createActivityRequest.activity_name)) {
    throw new Error("Please enter an activity name of 4-30 characters long");
  }
  if (!hasTimeFrame(createActivityRequest.continuous)) {
    throw new Error("Please select a time frame");
  }
   if (!createActivityRequest.continuous) {
     if (sDate === "" || sDate === undefined) {
       throw new Error("Duration based activities must have a start date");
     }
     if (!isValidTime(sTime) && sTime !== "") {
      throw new Error("Start time is not in valid format");
    }
     if (eDate === "" || eDate === undefined) {
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
    throw new Error("Description must be at least 8 characters");
  }
  if (createActivityRequest.location === undefined) {
    throw new Error("Please enter the location of the activity")
  }
  if (createActivityRequest.activity_type === [] || createActivityRequest.activity_type === undefined) {
    throw new Error("Please select at least one activity type");
  }
  if (isEditing && activityId !== undefined) {
    await editActivity(createActivityRequest, profileId, activityId)
  } else {
    await activityModel.createActivity(createActivityRequest, profileId);
  }
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
  return getApiDateTimeString(dateString, time);
}

/**
 * Returns only the date of the given ISO format date
 * @param dateString full ISO format date string
 * @returns only the date as string
 */
export function getDateFromISO(dateString: string): string {
  var date = new Date(dateString);
  return date.toISOString().substring(0, 10);
}

/**
 * Returns only the time of the given ISO format date in
 * HH-MM format
 * @param dateString 
 */
export function getTimeFromISO(dateString: string): string {
  var date = new Date(dateString);
  return date.toISOString().substring(11, 16);
}

/**
 * combines start date, time and timezone to a string as per API spec
 * @param createActivityRequest the activity to remove the activity type from
 * @param dateString end date in string format
 * @param time end time in string format
 */
export function setEndDate(dateString: string, time: string) {
  return getApiDateTimeString(dateString, time);
}

/**
 * combines the date, time and timezones given into ISO 8601 format
 * @param createActivityRequest the activity to remove the activity type from
 * @param dateString end date in string format
 * @param timeString end time in string format
 */
export function getApiDateTimeString(dateString: string, timeString: string) {
  if (timeString === "" || timeString === undefined) {
    timeString = "00:00";
  }
  let date = new Date(dateString)
  let offset = -date.getTimezoneOffset();
  let hours = Math.floor(offset / 60);
  let minutes = offset % 60;
  let minuteString = minutes.toString();
  let hourString = hours.toString();
  if (minutes < 10) {
    minuteString += "0";
  }
  if (hours < 10) {
    hourString += "0";
  }
  let offSetString = hourString + minuteString;
  if (offset >= 0) {
    offSetString = "+" + offSetString
  }
  return dateString + "T" + timeString + ":00" + offSetString;
}

export const INVALID_ACTIVITY_NAME_MESSAGE = "activity name must be between 4 and 30 characters"
/**
 * Validates that activity name is in compliance with our specification of 
 * having between 4 to 30 characters
 * @param activityName create activity request's activity name
 */
export function validateActivityName(activityName: string | undefined): boolean {
  if (activityName === undefined) {
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
  if (activityDescription !== undefined && activityDescription.length >= 8) {
    return true;
  } else {
    return false;
  }
}

export const INVALID_ACTIVITY_TYPE = "activity type already added"
export function validateActivityType(activityType: string, createActivityRequest: CreateActivityRequest): boolean { 
  if(createActivityRequest.activity_type === undefined) {
    createActivityRequest.activity_type = [];
  }
  return createActivityRequest.activity_type.includes(activityType)
}




/**
 * Deletes an activity
 * @param profileId Profile ID this activity belongs to
 * @param activityId Activity ID to delete
 */
export async function deleteActivity(profileId: number, activityId: number) {
  await activityModel.deleteActivityById(profileId, activityId);
}

/**
 * returns the activities created by a given creator.
 * @param creatorId the profileId of the creator of the activities
 */
export async function getActivitiesByCreator(creatorId: number) {
  return activityModel.getActivitiesByCreator(creatorId);
}

export const INVALID_DATE_MESSAGE = "date must be at least one day into the future"
/**
 * Checks if dateString given is a date in the future
 * if it is valid
 * @param dateString date in question in string form
 * @return true or false
 */
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
 * Checks if date is in valid format of YYYY-MM-DD
 * @param dateString date in question
 */
export function isValidDate(dateString: string) {
  var dateRegEx = /^\d{4}-\d{2}-\d{2}$/;
  if(!dateString.match(dateRegEx)) {
    return false; 
  }
  var d = new Date(dateString);
  var dNum = d.getTime();
  if(!dNum && dNum !== 0) {
    return false;
  }
  return d.toISOString().slice(0,10) === dateString;
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
  return "Starts at: " + dtf.format(start) + "Ends: " + dtf.format(end);
}

export const INVALID_CONTINUOUS_MESSAGE = "please pick between continuous or duration"
/**
 * Checks if the create activity request field "continuous" exists
 * @param timeFrame the createActivityRequest.continuous field
 */
export function hasTimeFrame(timeFrame: boolean | undefined): boolean {
  if (timeFrame === undefined) {
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
/**
 * Checks if time string input in the format of HH:MM
 * @param timeString time string to be checked
 */
export function isValidTime(timeString: string): boolean {
  let timeRegEx = /^\d{1,2}:\d{2}([ap]m)?$/;
  if (timeString.match(timeRegEx)) {
    return true;
  } else {
    return false;
  }
}


/**
 * Returns a list of duration based activities  from a list of activities
 * @param activityList a list of activities
 */
export function getDurationActivities(activityList: CreateActivityRequest[]): CreateActivityRequest[] {
  let durationActivities = activityList.filter(activity => activity.continuous === false);
  return durationActivities;
}


/**
 * Returns a list of continuous activities  from a list of activities
 * @param activityList a list of activities
 */
export function getContinuousActivities(activityList: CreateActivityRequest[]): CreateActivityRequest[] {
  let continuousActivities = activityList.filter(activity => activity.continuous === true);
  return continuousActivities;
}