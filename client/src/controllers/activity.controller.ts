import { CreateActivityRequest } from '../scripts/Activity';
import * as activityModel from '../models/activity.model'
import { UserApiFormat } from '@/scripts/User';
import { BoundingBoxInterface } from '@/scripts/BoundingBoxInterface';
import { getAddressFormattedString } from '../models/location.model';
import { LocationInterface } from '@/scripts/LocationInteface';
import { LocationCoordinatesInterface } from '@/scripts/LocationCoordinatesInterface';


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
    createActivityRequest: CreateActivityRequest) {
  if (!validateActivityName(createActivityRequest.activity_name)) {
    throw new Error("Please enter an activity name of 4-50 characters long");
  }
  if (createActivityRequest.continuous === undefined) {
    throw new Error("Please select a time frame");
  }
   if (!createActivityRequest.continuous) {
     if (sDate === "") {
       throw new Error("Duration based activities must have a start date");
     }
     if (!isValidTime(sTime) && sTime !== "") {
      throw new Error("Start time is not in valid format");
    }
     if (eDate === "") {
       throw new Error("Duration based activities must have an end date");
     }
     if (!isValidTime(eTime) && eTime != "") {
       throw new Error("End time is not in valid format");
     }
     if (!isValidEndDate(sDate, eDate, sTime, eTime)) {
       throw new Error("End date must be after start date");
     }
     createActivityRequest.start_time = setStartDate(sDate, sTime);
     createActivityRequest.end_time = setEndDate(eDate, eTime);
   }
  if (!validateDescription(createActivityRequest.description)) {
    throw new Error("Description must be at least 8 characters");
  }
  if (createActivityRequest.location === undefined) {
    throw new Error("Please enter the location of the activity");
  }
  else {

    let locationObject = await getAddressFormattedString(createActivityRequest.location)
    if (locationObject[0] === undefined) {
      throw new Error("Can't find a valid location with that address, try again");
    } else {
      createActivityRequest.location = locationObject[0].display_name
    }

  }
  if (createActivityRequest.activity_type === [] || createActivityRequest.activity_type === undefined) {
    throw new Error("Please select at least one activity type");
  }

}

/**
 * Takes a location string and checks if it is valid
 * @param location 
 */
export async function validateLocation(location : string){
  let locationObject = await getAddressFormattedString(location);
  return locationObject;
}

/**
 * Edit an activity
 * @param createActivityRequest Data related to the activity to edit
 * @param profileId Profile ID this activity belongs to
 * @param activityId Activity ID to edit
 */
export async function editOrCreateActivity(createActivityRequest: CreateActivityRequest, profileId: number, activityId: number | undefined, isEditing: boolean) {
  if (isEditing && activityId !== undefined && !isNaN(activityId)) {
    await activityModel.editActivity(createActivityRequest, profileId, activityId);
  } else {
    await activityModel.createActivity(createActivityRequest, profileId);
  }
}

/**
 * Records a participant's results in an activity's activityoutcome
 * @param activityId ID of the activity to record against
 * @param outcomeId ID of the individual activity outcome to record against
 * @param score The user's score in this outcome
 * @param completedTimestamp The time the user claims to have completed the activity
 */
export async function createParticipantResult(activityId: number, outcomeId: number, score: string, completedTimestamp: string) {
  if (score.length == 0 || score.length > 30) {
    throw new Error("The given result should be between 0 and 30 characters");
  }

  await activityModel.createParticipantOutcome(activityId, outcomeId, score, completedTimestamp);
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
  return dateString.substring(0, 10);
}

/**
 * Returns only the time of the given ISO format date in HH:MM format
 * @param dateString the ISO timestamp to get the time of
 * @return the hours and minutes in HH:MM format
 */
export function getTimeFromISO(dateString: string): string {
  return dateString.substring(11, 16);
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

export const INVALID_ACTIVITY_NAME_MESSAGE = "Activity name must be between 4 and 50 characters"
/**
 * Validates that activity name is in compliance with our specification of 
 * having between 4 to 30 characters
 * @param activityName create activity request's activity name
 */
export function validateActivityName(activityName: string | undefined): boolean {
  if (activityName === undefined) {
    return false;
  }
  if (activityName.length >= 4 && activityName.length <= 50) {
    return true;
  } else {
    return false;
  }
}

export const INVALID_DESCRIPTION_MESSAGE = "Description must be at least 8 characters"
export function validateDescription(activityDescription: string | undefined): boolean {
  if (activityDescription !== undefined && activityDescription.length >= 8) {
    return true;
  } else {
    return false;
  }
}

export const INVALID_ACTIVITY_TYPE = "Activity type already added"
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

export const INVALID_DATE_MESSAGE = "Date must be in the future"
/**
 * Checks if dateString given is a date in the future i.e from 00:00 today
 * @param dateString date in question in string form
 * @return true or false
 */
export function isFutureDate(dateString: string): boolean {
  let today = new Date().setHours(0);
  let dateInput = new Date(dateString).getTime();
  return (today <= dateInput)
}

export function isFutureDateTime(dateString: string): boolean {
  let today = new Date()
  let dateTimeIn = new Date(dateString);
  return (today <= dateTimeIn);
}

/**
 * Checks if date is in valid format of YYYY-MM-DD
 * @param dateString date in question
 */
export function isValidDate(dateString: string) {
  const dateRegEx = /^\d{4}-\d{2}-\d{2}$/;
  return dateString.match(dateRegEx) !== null;
}

/**
 * Gets an activity by a user's ID and the activity ID
 * 
 * @param {number} creatorId User the activity belongs to
 * @param {number} activityId Activity ID
 * @return {CreateActivityRequest} Retrieved activity data
 */
export async function getActivity(creatorId: number, activityId: number) {
  return activityModel.getActivity(creatorId, activityId);
}

/**
 * Gets an activity by the activity ID
 * 
 * @param {number} activityId Activity ID
 * @return {CreateActivityRequest} Retrieved activity data
 */
export async function getActivityById(activityId: number) {
  return activityModel.getActivityById(activityId);
}


/**
 * Registers user's account to follow the activity with the given id
 * @param profileId the id of the user's profile
 * @param activityId the id of the activity to follow
 * @return whether user is following activity; true if they are, false otherwise
 */
export async function getIsFollowingActivity(profileId: number, activityId: number): Promise<boolean> {
  let data = await activityModel.getFollowingActivity(profileId, activityId);
  return data["subscribed"];
}


/**
 * Registers user's account to follow the activity with the given id
 * @param profileId the id of the user's profile
 * @param activityId the id of the activity to follow
 */
export async function followActivity(profileId: number, activityId: number) {
  await activityModel.addActivityFollower(profileId, activityId);
}


/**
 * Removes a follower with the given profile from the activity with the given id
 * @param profileId the id of the user's profile
 * @param activityId the id of the activity to follow
 */
export async function unfollowActivity(profileId: number, activityId: number) {
  await activityModel.removeActivityFollower(profileId, activityId);
}

export async function getIsParticipating(profileId: number, activityId: number) {
  let role = await activityModel.getActivityRole(profileId, activityId);
  if (role == null) {
    return false;
  }
  return role.toLowerCase() == "participant";
}

/**
 * Returns true if the user has the role organiser, false otherwise
 * @param profileId the user id to be checked
 * @param activityId the activity id to be checked
 * @returns boolean true or false 
 */
export async function getIsOrganising(profileId: number, activityId: number) {
  let role = await activityModel.getActivityRole(profileId, activityId);
  if (role == null) {
    return false;
  }
  return role.toLowerCase() == "organiser";
}

/**
 * sets a user as a participant in an activity
 * @param profileId the profile to set as a participant
 * @param activityId the activity the profile should participate in
 */
export async function participateInActivity(profileId: number, activityId: number) {
  await activityModel.setActivityRole(profileId, activityId, "participant");
}

/**
 * clears a user's role in a particular activity
 * @param profileId the profile whose roles should be cleared
 * @param activityId the activity the profile should be cleared from
 */
export async function removeActivityRole(profileId: number, activityId: number) {
  await activityModel.removeActivityRole(profileId, activityId);
}

/**
 * Get the results that a participant has entered for an activity
 * @param profileId participant whose results should be retrieved
 * @param activityId activity to retrieve the results for
 */
export async function getParticipantResults(profileId: number, activityId: number) {
  return await activityModel.fetchParticipantResults(profileId, activityId);
}

/**
 * Remove a result a user has added for an activity
 * @param activityId activity on which the result exists
 * @param outcomeId outcome that the result is associated with
 */
export async function removeParticipantResult(activityId: number, outcomeId: number) {
  await activityModel.deleteParticipantResult(activityId, outcomeId);
}

/**
 * returns a reader-friendly formatted date
 * @param dateString the ISO datetime string representing the time to be formatted
 * @return the formatted date string
 */
export function describeDate(dateString: string) {
  let start = new Date(dateString);
  const dtf = new Intl.DateTimeFormat(undefined, {
    year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric', timeZoneName: 'short'
  });
  return dtf.format(start);
}

export const INVALID_CONTINUOUS_MESSAGE = "Please pick between continuous or duration"

export const INVALID_END_DATE_MESSAGE = "End date must be after start date and in YYYY-MM-DD format"
/**
 * Checks if end date and time is after start date and time.
 * @param startDateString start date as a string
 * @param endDateString end date as a string
 * @param startTime start time as a string, initialised to 00:00 if left empty
 * @param endTime end time as a string, initialised to 00:00 if left empty
 */
export function isValidEndDate(startDateString: string, endDateString: string, startTime: string, endTime: string): boolean 
{
  if (startTime === '') {
    startTime = '00:00'
  }
  if (endTime === '') { 
    endTime = '00:00'
  }
  let endDate = new Date(endDateString).setHours(parseInt(endTime.slice(0, 2), 10), parseInt(endTime.slice(3), 10));
  let startDate = new Date(startDateString).setHours(parseInt(startTime.slice(0, 2), 10), parseInt(startTime.slice(3), 10));
  return startDate < endDate;
}

export const INVALID_TIME_MESSAGE = "Please enter a valid time"
/**
 * Checks if time string input in the format of HH:MM
 * @param timeString time string to be checked
 */
export function isValidTime(timeString: string): boolean {
  let timeRegEx = /^\d{1,2}:\d{2}?$/;
  return timeString.match(timeRegEx) !== null;
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

/**
 * returns a list of all organisers of an activity.
 * @param activityId the activity to get the emails of organisers for
 */
export async function getActivityOrganisers(activityId: number): Promise<UserApiFormat[]> {
  let profiles = await activityModel.getActivityOrganisers(activityId);
  return profiles;
}

/**
 * Returns a list of both organisers and participants of an activity
 * @param activityId the activity id
 */
export async function getParticipants(activityId: number) {
  return await activityModel.getParticipants(activityId);
}

/**
 * Takes three timestamp strings. Checks that a given time is between start and end.
 * @param start the timestamp representing the start of the time range
 * @param end the timestamp representing the end of the time range
 * @param time the time to check is between start and end
 * @returns true if time is between start and end, false otherwise
 */
export function timeIsWithinRange(start: string, end: string, time: string) {
  let startDate = new Date(start);
  let endDate = new Date(end);
  let testDate = new Date(time);
  return startDate <= testDate && testDate <= endDate;
}

/**
 * Gets activities that fall within a certain area
 * @param boundingBox Bounding box to get activities inside
 */
export async function getActivitiesInBoundingBox(boundingBox: BoundingBoxInterface) {
 return await activityModel.getActivitiesInBoundingBox(boundingBox);
}