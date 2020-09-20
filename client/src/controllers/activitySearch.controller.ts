import { searchAppActivities } from '../models/activitySearch.model';

/**
 * sends a request to search activities based on the search query entered by the user.
 * @param searchQuery the search query entered by the user
 * @param pageNumber the page number to fetch
 * @param pageSize the max number of results to fetch on this page
 */
export async function searchActivities(searchQuery: string, pageNumber: number, pageSize: number) {
  const searchTerms = getSearchArguments(searchQuery);
  return await searchAppActivities(searchTerms, pageNumber, pageSize);
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
