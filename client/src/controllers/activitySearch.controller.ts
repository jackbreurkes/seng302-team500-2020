import { searchAppActivities } from '../models/activitySearch.model';
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';

export async function searchActivities(searchTerms: Dictionary<string>) {
    return await searchAppActivities(searchTerms);
  }