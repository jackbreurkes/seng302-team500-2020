import * as searchController from "./activitySearch.controller";


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
    [`"`, []],
    [`""`, []],
    [`"""`, []],
    [`"""""`, []],
    [`""""""`, []],
    [`"" " """"`, []],
    [`"   ""   "`, []]
])('expect getSearchArguments to split the query %s with edge case quotation marks into the list %s',
    (query: string, expectedWords: string[]) => {
        expect(searchController.getSearchArguments(query)).toEqual(expectedWords)
    }
)