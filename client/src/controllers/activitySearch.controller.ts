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

/**
 * splits a search query, possibly containing quotation marks, into words or quoted chunks.
 * @param searchQuery the search query to split into words
 */
export function getSearchArguments(searchQuery: string): string[] {
  // eslint-disable-next-line no-useless-escape
  const regex = /\([^\)]+?[\)]|[\""].+?[\""]|[^ ]+/g;
  const matches = searchQuery.match(regex);
  if (matches === null) {
    return [];
  }
  let result: string[] = [];
  matches.forEach(match => {
    // eslint-disable-next-line no-useless-escape
    const quotesOrWhitespaceRegex = /^[\""\s]*$/; // matches strings are only quotes and/or whitespace
    if (quotesOrWhitespaceRegex.test(match)) {
      return;
    }
    while (match.length > 0 && quotesOrWhitespaceRegex.test(match[0])) {
      match = match.slice(1);
    }
    while (match.length > 0 && quotesOrWhitespaceRegex.test(match[match.length-1])) {
      match = match.slice(0, match.length-1);
    }
    if (match.length > 0) {
      result.push(match);
    }
  })
  return result;
}
