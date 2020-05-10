import * as activityController from "./activity.controller";
import { CreateActivityRequest } from "../scripts/Activity";

import axios from "axios";

jest.mock("axios");

const activityModel = require("../models/activity.model");
var today = new Date().toISOString().slice(0, 10);
activityModel.createActivity = jest.fn();

const mockActivityTypes: string[] = [
  "Walking",
  "Running",
  "Swimming",
  "Dancing",
];
activityModel.loadAvailableActivityTypes = jest.fn(async () => {
  return mockActivityTypes;
});

// describe.skip("activity controller test suite", () => {
// --------- ACTIVITY TYPES ---------- //
test.each(["Walking", "Existing", "Whatever"])(
  "expect addActivityType to throw an error if %s is already added to the activity",
  async (existingActivityType) => {
    let activityData: CreateActivityRequest = {
      activity_type: [existingActivityType],
    };
    await expect(
      activityController.addActivityType(existingActivityType, activityData)
    ).rejects.toThrow(
      new Error(`${existingActivityType} is already added to the activity`)
    );
  }
);

test.each(["Fake Activity Type", "Does Not Exist", "Nothing", ""])(
  "expect addActivityType to throw an error if trying to add a non-existent activity type",
  async (fakeActivityType) => {
    let activityData: CreateActivityRequest = {};
    await expect(
      activityController.addActivityType(fakeActivityType, activityData)
    ).rejects.toThrow(
      new Error(`activity type ${fakeActivityType} does not exist`)
    );
  }
);

test.each(mockActivityTypes)(
    "expect addActivityType to add valid activity types to an activity without creating the activity",
    async (validActivityType) => {
      let activityData: CreateActivityRequest = {};
      await expect(
        activityController.addActivityType(validActivityType, activityData)
      ).resolves.toBe(undefined);
      expect(activityData.activity_type).toHaveLength(1);
      expect(activityData.activity_type).toContain(validActivityType);
      // this expect doesn't work, but will only throw an error if axios.post is called, meaning it does what we need it to. ¯\_(ツ)_/¯
      expect(axios.post).toHaveBeenCalledTimes(0); // changes should not be persisted automatically
    }
  );

  test.each(["test", "walking", "hiking"])(
    "expect throw an error if activity type has not been added to the activity",
    (missingActivityType) => {
      let activityData: CreateActivityRequest = {};
      expect(() => {activityController.removeActivityType(missingActivityType, activityData)}).toThrow(new Error(`${missingActivityType} has not been added to the activity`));
    }

  );

  test.each(["test", "walking", "hiking"])(
    "expect removeActivityType to remove activity type if it has been added to the activity",
    (addedActivityType) => {
      let activityData: CreateActivityRequest = {activity_type: [addedActivityType]};
      expect(activityController.removeActivityType(addedActivityType, activityData)).toBe(undefined);
      expect(activityData.activity_type).toHaveLength(0);
    }

  )


//expect activity name to be at least 4 characters and at most 30 characters
test.each(["test", "lorem ipsum", "asdasdasdasdasdasdasdasdasdasd"])(
  "test that activity names between 4 and 30 characters inclusive are valid",
  (validName) => {
    const validatorReturns = activityController.validateActivityName(validName);
    expect(validatorReturns).toBe(true);
  }
)

test.each(["tes", "rat"])(
  "test that activity names under 4 characters are invalid",
  (validName) => {
    const validatorReturns = activityController.validateActivityName(validName);
    expect(validatorReturns).toBe(false);
  }
)

test.each(["asdasdasdasdasdasdasdasdasdasds", "asdasdasdasdasdasdasdasdasdasdsaaaa"])(
  "test that activity names over 30 characters are invalid",
  (validName) => {
    const validatorReturns = activityController.validateActivityName(validName);
    expect(validatorReturns).toBe(false);
  }
)

//activity description must be at least 8 chars if specified
// description is at least 8 characters.
test.each(["This is my description!", "12345678", "AAAAAAAA", "A whole new description has been made! 1234567890"])(
  'expect %s to be a valid description', (description) => {
      expect(activityController.validateDescription(description)).toBe(true)
  }
)

// description less than 8 characters.
test.each(["Short!", "A", "Tiny", "A test"])(
  'expect %s to be an invalid description', (description) => {
      expect(activityController.validateDescription(description)).toBe(false)
  }
)

// description is the empty string.
test('expect "" to be a valid description', () => {
      expect(activityController.validateDescription("")).toBe(true)
  }
)

// description is undefined.
test('expect undefined to be a valid description', () => {
  expect(activityController.validateDescription(undefined)).toBe(true)
}
)

// Start date is in valid format and is in today or in the future.
test.skip.each(["2021-12-29", today])( //TODO make tests pass forever
  'expect %s to be a valid start date', (startDate) => {
      expect(activityController.isFutureDate(startDate)).toBe(true)
  }
);

// Start date is in valid format but does not exist.
test.skip.each(["2021-12-32", "2021-02-31"])(
  'expect %s to be an invalid start date', (startDate) => {
      expect(activityController.isFutureDate(startDate)).toBe(false)
  }
)

// Start date is in valid format and is in past.
test.skip.each(["2001-12-32", "2001-02-28"])(
  'expect %s to be an invalid start date', (startDate) => {
      expect(activityController.isFutureDate(startDate)).toBe(false)
  }
)

// Date given is in invalid format
test.skip.each(["Today", "Wednesday 24th June, 2021", "2021/01/01", "01-02-2021", "30-04-31", "2021-1-1"])(
  'expect %s to be an invalid start date', (date) => {
      expect(activityController.isFutureDate(date)).toBe(false)
  }
)

// Date given is the empty string
test.skip('expect "" to be an invalid start date', () => {
      expect(activityController.isFutureDate("")).toBe(false)
  }
)

// Date given is undefined
// test.skip('expect undefined to be an invalid start date', () => {
//       expect(activityController.validateDate(undefined)).toBe(false)
//   }
// )

// End date is in valid format and is after start date
// test.skip.each([["2021-12-25", "2022-02-27"], ["2021-02-25", "2022-02-18"]])( //TODO make tests pass forever
//   'expect %s to be valid end date', (startDate, endDate) => {
//       expect(activityController.validateEndDate(startDate, endDate)).toBe(true)
//   }
// );
//TODO time tests
//merging date and time to api format
//end date validation
//start and end time validation
//location validation TODO after U9

// });