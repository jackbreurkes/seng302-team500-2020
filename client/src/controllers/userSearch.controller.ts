import { searchAppUsers } from '../models/userSearch.model';
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';

export async function searchUsers(searchTerms: Dictionary<string>) {
    return await searchAppUsers(searchTerms);
  }

export function getShortenedActivitiesString(activities: string[], searchTerms : Dictionary<string>){
  let activitiesString = "";
  //here not working
  if(searchTerms["activity"] !== undefined){
    //activities
    const tempSearchActivities : string [] = searchTerms["activity"].split(" ");
    tempSearchActivities.forEach(element => {
      if(activities.includes(element) && activities.length < 3){
        activitiesString += element + ", ";
      }
    });
    return activitiesString.trim().substring(0, activitiesString.length-2);
  }else{
    if (activities == undefined || activities.length == 0) {
      return "";
    } else if (activities.length <= 3) {
      for (let activityIndex in activities) {
        activitiesString = activitiesString + activities[activityIndex] + ", ";
      }
      activitiesString.trim();
      activitiesString = activitiesString.substring(0, activitiesString.length-2);
      return activitiesString;
    } else {
      for (let i = 0; i < 3; i++) {
        activitiesString = activitiesString + activities[i] + ", ";
      }
      activitiesString.trim();
      activitiesString = activitiesString.substring(0, activitiesString.length-2);
      return activitiesString+"...";
    }
  }
}