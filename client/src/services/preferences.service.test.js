/* eslint-disable no-unused-vars */
/* eslint-env jest */

import * as preferences from "./preferences.service";

const prefersHorizontalSplitKey = "prefersHorizontalSplit"

beforeEach(() => {
    localStorage.clear();
})

test(
    "expect getPrefersHorizontalSplit to return true if the user's horizontal split preference is set to 'true'", () => {
        localStorage.setItem(prefersHorizontalSplitKey, "true");
        expect(preferences.getPrefersHorizontalSplit()).toBe(true);
    }
);

test(
    "expect getPrefersHorizontalSplit to return false if the user's horizontal split preference is set to 'false'", () => {
        localStorage.setItem(prefersHorizontalSplitKey, "false");
        expect(preferences.getPrefersHorizontalSplit()).toBe(false);
    }
);

test.each(["flase", "ture", "False", "FALSE", "cats", ""])(
    "expect getPrefersHorizontalSplit to default to true if an invalid value is stored", (invalidString) => {
        localStorage.setItem(prefersHorizontalSplitKey, invalidString);
        expect(preferences.getPrefersHorizontalSplit()).toBe(true);
    }
);

test(
    "expect getPrefersHorizontalSplit to default to true if the user's horizontal split preference is not stored", () => {
        expect(preferences.getPrefersHorizontalSplit()).toBe(true);
    }
);

test.each([true, false])(
    "expect setPrefersHorizontalSplit to store the given boolean value in localstorage as a string", (value) => {
        preferences.setPrefersHorizontalSplit(value)
        expect(localStorage.getItem(prefersHorizontalSplitKey)).toBe(value ? "true" : "false");
    }
);

test.each([["my search query", 10, 5]])(
    "expect saveActivitySearchPosition to store the given search information for later retrieval", (query, pageNumber, pageSize) => {
        preferences.saveActivitySearchPosition(query, pageNumber, pageSize);
        expect(preferences.getSavedActivitySearchQuery()).toBe(query);
        expect(preferences.getSavedActivitySearchPage()).toBe(pageNumber);
        expect(preferences.getPreferredSearchPageSize()).toBe(pageSize);
    }
);

test("expect getSavedActivitySearchQuery to return the empty string by default", () => {
    expect(preferences.getSavedActivitySearchQuery()).toBe("");
})

test("expect getSavedActivitySearchPage to return 1 by default", () => {
    expect(preferences.getSavedActivitySearchPage()).toBe(1);
})

test("expect getPreferredSearchPageSize to return 15 by default", () => {
    expect(preferences.getPreferredSearchPageSize()).toBe(15);
})
