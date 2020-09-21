import { searchAppActivities } from '../models/activitySearch.model';

/**
 * sends a request to search activities based on the search query entered by the user.
 * @param searchQuery the search query entered by the user
 * @param pageNumber the page number to fetch
 * @param pageSize the max number of results to fetch on this page
 * @returns the list of activities that were matched by the search
 */
export async function searchActivities(searchQuery: string, pageNumber: number, pageSize: number) {
  const searchTerms = getSearchArguments(searchQuery);
  return await searchAppActivities(searchTerms, pageNumber, pageSize);
}

/**
 * takes a list of activity types and returns a user-readable string representation.
 * sorts the activity types and displays at most three.
 * @param activityTypes the list of activity types to join together
 * @returns a string of comma separated activity types
 */
export function getShortenedActivityTypesString(activityTypes: string[]): string {
  activityTypes = [...activityTypes];
  activityTypes.sort();
  if (activityTypes.length > 3) {
    activityTypes[3] = "...";
  }
  return activityTypes.slice(0, 4).join(", ");
}

/**
 * splits a search query, possibly containing quotation marks, into words or quoted chunks.
 * @param searchQuery the search query to split into words
 * @returns the list of search terms, exluding any leading or trailing whitespaces or quotation marks
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
    let firstValidCharLeft = 0;
    while (firstValidCharLeft < match.length && quotesOrWhitespaceRegex.test(match[firstValidCharLeft])) {
      firstValidCharLeft += 1;
    }
    let firstValidCharRight = match.length - 1;
    while (firstValidCharRight > 0 && quotesOrWhitespaceRegex.test(match[firstValidCharRight])) {
      firstValidCharRight -= 1;
    }
    match = match.slice(firstValidCharLeft, firstValidCharRight + 1);
    if (match.length > 0) {
      result.push(match);
    }
  })
  return result;
}
