/**
 * service to handle storage of a user's local preferences.
 */

const prefersHorizontalSplitKey = "prefersHorizontalSplit";
const activitySearchQueryKey = "activitySearchQuery";
const activitySearchPageKey = "activitySearchPage";
const preferredResultPageSizeKey = "preferredSearchPageSize";
const defaultResultPageSize = 15;

/**
 * returns whether the user prefers the map pane to be split horizontally or vertically.
 * defaults to true if no preference has been stored or an invalid preference is stored.
 */
export function getPrefersHorizontalSplit(): boolean {
  let horizontal = localStorage.getItem(prefersHorizontalSplitKey);
  return horizontal !== "false";
}

/**
 * sets the user's screen split direction preference.
 * @param horizontal true if the user prefers their screen to be split horizontally
 */
export function setPrefersHorizontalSplit(horizontal: boolean) {
  let horizontalString = horizontal ? "true" : "false"; // explicitly store as string to ensure localStorage compatibility
  localStorage.setItem(prefersHorizontalSplitKey, horizontalString);
}

/**
 * saves the user's position information in an activity search.
 * @param searchQuery the search query to save
 * @param pageNumber the page number the user was on
 * @param pageSize the size of the pages the user was looking through
 */
export function saveActivitySearchPosition(searchQuery: string, pageNumber: number, pageSize: number) {
  localStorage.setItem(activitySearchQueryKey, searchQuery);
  localStorage.setItem(activitySearchPageKey, pageNumber.toString());
  localStorage.setItem(preferredResultPageSizeKey, pageSize.toString());
}

/**
 * returns the saved activity search query, or the empty string if none is saved.
 */
export function getSavedActivitySearchQuery(): string {
  return localStorage.getItem(activitySearchQueryKey) || "";
}

/**
 * returns the saved activity search page number, or 1 if none is saved.
 */
export function getSavedActivitySearchPage(): number {
  const savedPage = localStorage.getItem(activitySearchPageKey) || "NaN";
  const pageNumber = parseInt(savedPage);
  return isNaN(pageNumber) ? 1 : pageNumber;
}

/**
 * returns the user's preferred search result page size, or the default value if none is saved.
 */
export function getPreferredSearchPageSize(): number {
  const savedPageSize = localStorage.getItem(preferredResultPageSizeKey) || "NaN";
  const pageSize = parseInt(savedPageSize);
  return isNaN(pageSize) ? defaultResultPageSize : pageSize;
}