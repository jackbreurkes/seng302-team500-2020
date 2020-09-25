import { searchAppUsers } from '../models/userSearch.model';
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';

export async function searchUsers(searchTerms: Dictionary<string>) {
    return await searchAppUsers(searchTerms);
  }

export function getShortenedActivitiesString(activities: string[], searchTerms : Dictionary<string>){

  let activitiesString = "";

  if(searchTerms["activity"] !== undefined){    // If the user is searching by activity interests
    const tempSearchActivities : string [] = searchTerms["activity"].split(" ");
    let total_added = 0;

    for (let i = 0; total_added < 3 && i < tempSearchActivities.length; i++) {
      if (activities.includes(tempSearchActivities[i])) {     // If the activity is an interest of the user
        activitiesString = activitiesString + tempSearchActivities[i] + ", ";
        total_added++;
      }
    }
    activitiesString = activitiesString.substring(0, activitiesString.length-2);
    if (activities.length > total_added) {    // If the user has more activities still
      activitiesString += "...";
    }
    return activitiesString;

  }else{    // The user is searching either by name, nickname, or email
    if (activities == undefined || activities.length == 0) {  // They have no activities
      return "";
    } else if (activities.length <= 3) {    // Has less than 3 activities so include all in summary
      for (let activityIndex in activities) {
        activitiesString = activitiesString + activities[activityIndex] + ", ";
      }
      activitiesString = activitiesString.substring(0, activitiesString.length-2);
      return activitiesString;
    } else {      // Get only the first three activities for the interests summary
      for (let i = 0; i < 3; i++) {
        activitiesString = activitiesString + activities[i] + ", ";
      }
      activitiesString = activitiesString.substring(0, activitiesString.length-2);
      return activitiesString+"...";
    }
  }
}
