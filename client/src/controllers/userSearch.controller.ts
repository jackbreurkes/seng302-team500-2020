import { searchAppUsers } from '../models/userSearch.model';
// eslint-disable-next-line no-unused-vars
import { Dictionary } from 'vue-router/types/router';

export async function searchUsers(searchTerms: Dictionary<string>) {
    return await searchAppUsers(searchTerms);
  }
