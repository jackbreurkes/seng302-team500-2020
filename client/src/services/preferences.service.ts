/**
 * service to handle storage of a user's local preferences.
 */

const prefersHorizontalSplitKey = "prefersHorizontalSplit"

/**
 * returns whether the user prefers the map pane to be split horizontally or vertically.
 * defaults to true if no preference has been stored.
 */
export function getPrefersHorizontalSplit(): boolean {
    let horizontal = localStorage.getItem(prefersHorizontalSplitKey) || "true";
    return horizontal === "true";
}

/**
 * sets the user's screen split direction preference.
 * @param horizontal true if the user prefers their screen to be split horizontally
 */
export function setPrefersHorizontalSplit(horizontal: boolean) {
    let horizontalString = (horizontal ? "true" : "false"); // explicitly store as string to ensure localStorage compatibility
    localStorage.setItem(prefersHorizontalSplitKey, horizontalString);
}