import { searchAppActivities } from '../models/activitySearch.model';
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';

export async function searchActivities(searchTerms: string[]) {
  return await searchAppActivities(searchTerms);
}

/**
 * returns the list of activity types given as a single string.
 * @param activityTypes the list of activity types to join together
 */
export function getShortenedActivityTypesString(activityTypes: string[]) {
  return activityTypes.join(", ")
}