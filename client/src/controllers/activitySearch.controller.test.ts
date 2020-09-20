import * as searchController from "./activitySearch.controller";

test.each([
    [["running", "walking"], "running, walking"],
    [["testing"], "testing"],
    [["running", "jumping", "climbing"], "climbing, jumping, running"],
])("expect getShortenedActivityTypesString to return lists of three or less strings as comma separated strings in alphabetical order", (activityTypes, expected) => {
    expect(searchController.getShortenedActivityTypesString(activityTypes)).toBe(expected);
})

test.each([
    [["flying", "running", "swimming", "walking"], "flying, running, swimming, ..."],
    [Array(50).fill("type"), "type, type, type, ..."],
])("expect getShortenedActivityTypesString to return lists of over three strings in alphabetical order with an ellipsis", (activityTypes, expected) => {
    expect(searchController.getShortenedActivityTypesString(activityTypes)).toBe(expected);
})

test("expect getShortenedActivityTypesString not to sort the original list", () => {
    let list = ["b", "a"];
    expect(searchController.getShortenedActivityTypesString(list)).toBe("a, b");
    expect(list[0]).toBe("b");
    expect(list[1]).toBe("a");
})

test.each([
    [`test`, [`test`]],
    [`test multiple words`, [`test`, `multiple`, `words`]],
    [`"with double quotes"`, [`with double quotes`]],
    [`'single quotes'`, [`'single`, `quotes'`]],
    [`quotes "at the end"`, [`quotes`, `at the end`]],
    [`"starting with" quotes`, [`starting with`, `quotes`]],
    [`"quotesNoSpaces"`, [`quotesNoSpaces`]],
    [`activity "Activity 2" run "Activity 16"`, [`activity`, `Activity 2`, `run`, `Activity 16`]],
    [`"Bob's activity"`, [`Bob's activity`]],
    [`empty quotes ""`, [`empty`, `quotes`]],
    [`myFun"Act"ivity`, [`myFun"Act"ivity`]],
    [``, []],
    [`   multiple \t  \t\n  whitespaces   \n`, [`multiple`, `whitespaces`]],
])('expect getSearchArguments to split the query %s into the list %s',
    (query: string, expectedWords: string[]) => {
        expect(searchController.getSearchArguments(query)).toEqual(expectedWords)
    }
)

test.each([
    [`"two on right""`, [`two on right`]],
    [`""two on left"`, [`two on left`]],
    [`"gap on side" "`, [`gap on side`]],
    [`""quotes within quotes""`, [`quotes within quotes`]],
    [`"quotes as words" """`, [`quotes as words`]],
    [`"  spaces \t within \n quotes  "`, [`spaces`, `within`, `quotes`]],
    [`"\t\nwhitespace\n\t"`, [`whitespace`]],
    [`"`, []],
    [`""`, []],
    [`"""`, []],
    [`"""""`, []],
    [`""""""`, []],
    [`"" " """"`, []],
    [`"   ""   "`, []],
])('expect getSearchArguments to split the query %s with edge case quotation marks into the list %s',
    (query: string, expectedWords: string[]) => {
        expect(searchController.getSearchArguments(query)).toEqual(expectedWords)
    }
)