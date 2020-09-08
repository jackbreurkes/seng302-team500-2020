import * as activityController from "./activity.controller";
import { CreateActivityRequest } from "../scripts/Activity";

const activityModel = require("../models/activity.model");
var today = new Date().toISOString().slice(0, 10);
activityModel.createActivity = jest.fn();

const mockActivityTypes: string[] = [
  "Walking",
  "Running",
  "Swimming",
  "Dancing",
];

const mockCreateActivityRequest: CreateActivityRequest = {
  activity_name: "Raid Area 52",
  description: "Naruto run through area 52",
  activity_type: mockActivityTypes,
  continuous: true,
  start_time: "",
  end_time: "",
  location: "Christchurch, New Zealand",
};

activityModel.loadAvailableActivityTypes = jest.fn(async () => {
  return mockActivityTypes;
});

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
    // TODO implement expects for post requests being called
    // expect(instance.post).toHaveBeenCalledTimes(0); // changes should not be persisted automatically
  }
);

test.each(["test", "walking", "hiking"])(
  "expect throw an error if activity type has not been added to the activity",
  (missingActivityType) => {
    let activityData: CreateActivityRequest = {};
    expect(() => {
      activityController.removeActivityType(missingActivityType, activityData);
    }).toThrow(
      new Error(`${missingActivityType} has not been added to the activity`)
    );
  }
);

test.each(["test", "walking", "hiking"])(
  "expect removeActivityType to remove activity type if it has been added to the activity",
  (addedActivityType) => {
    let activityData: CreateActivityRequest = {
      activity_type: [addedActivityType],
    };
    expect(
      activityController.removeActivityType(addedActivityType, activityData)
    ).toBe(undefined);
    expect(activityData.activity_type).toHaveLength(0);
  }
);

//expect activity name to be at least 4 characters and at most 30 characters
test.each(["test", "lorem ipsum", "asdasdasdasdasdasdasdasdasdasd"])(
  "test that activity names between 4 and 30 characters inclusive are valid",
  (validName) => {
    const validatorReturns = activityController.validateActivityName(validName);
    expect(validatorReturns).toBe(true);
  }
);

test.each(["tes", "rat"])(
  "test that activity names under 4 characters are invalid",
  (validName) => {
    const validatorReturns = activityController.validateActivityName(validName);
    expect(validatorReturns).toBe(false);
  }
);

test.each([
  "asdasdasdasdasdasdasdasdasdasds",
  "asdasdasdasdasdasdasdasdasdasdsaaaa",
])("test that activity names over 30 characters are invalid", (validName) => {
  const validatorReturns = activityController.validateActivityName(validName);
  expect(validatorReturns).toBe(false);
});

//activity description must be at least 8 chars if specified
// description is at least 8 characters.
test.each([
  "This is my description!",
  "12345678",
  "AAAAAAAA",
  "A whole new description has been made! 1234567890",
])("expect %s to be a valid description", (description) => {
  expect(activityController.validateDescription(description)).toBe(true);
});

// description less than 8 characters.
test.each(["Short!", "A", "Tiny", "A test"])(
  "expect %s to be an invalid description",
  (description) => {
    expect(activityController.validateDescription(description)).toBe(false);
  }
);

// description is the empty string.
test('expect "" to be a invalid description', () => {
  expect(activityController.validateDescription("")).toBe(false);
});

// description is undefined.
test("expect undefined to be a invalid description", () => {
  expect(activityController.validateDescription(undefined)).toBe(false);
});

// Date is in the future
test.each(["2021-12-29", "2020-12-31"])(
  "expect %s to be a valid date",
  (date) => {
    expect(activityController.isFutureDate(date)).toBe(true);
  }
);

// Check date if date is valid
test.each(["2021-12-29", "2020-12-31"])(
  "expect %s to be a valid date",
  (date) => {
    expect(activityController.isValidDate(date)).toBe(true);
  }
);

//Date is in the past.
test.each(["2001-12-32", "2001-02-28"])(
  "expect %s to be an invalid date",
  (date) => {
    expect(activityController.isFutureDate(date)).toBe(false);
  }
);

// Date given is in invalid format
test.each(["Today", "Wednesday 24th June, 2021", "30-04-31"])(
  "expect %s to be an invalid start date",
  (date) => {
    expect(activityController.isValidDate(date)).toBe(false);
  }
);

// Date given is the empty string
test('expect "" to be an invalid start date', () => {
  expect(activityController.isValidDate("")).toBe(false);
});

// End date is in valid format and is after start date
test.each([
  ["2021-12-25", "2022-02-27", "12:00", "12:00"],
  ["2021-02-25", "2022-02-18", "12:00", "11:59"],
  ["2021-02-25", "2021-02-25", "12:00", "12:01"],
  ["2021-02-25", "2021-02-25", "12:00", "13:00"],
])("expect %s to be valid end date", (startDate, endDate, startTime, endTime) => {
  expect(activityController.isValidEndDate(startDate, endDate, startTime, endTime)).toBe(true);
});

// End date is in valid format and is before start date
test.each([
  ["2022-12-25", "2021-02-27", "12:00", "12:00"],
  ["2022-02-25", "2021-02-18", "12:00", "12:00"],
  ["2021-02-25", "2021-02-25", "12:00", "11:59"],
  ["2021-02-25", "2021-02-25", "12:00", "11:00"],
  ["2022-02-25", "2021-02-25", "12:00", "12:01"],
  ["2022-02-25", "2021-02-25", "12:00", "12:00"],
])("expect %s to be an invalid end date", (startDate, endDate, startTime, endTime) => {
  expect(activityController.isValidEndDate(startDate, endDate, startTime, endTime)).toBe(false);
});

// Time is in valid format
test.each(["01:00", "12:30", "19:30"])(`expect %s to be valid time`, (time) => {
  expect(activityController.isValidTime(time)).toBe(true);
});

// Time is not in valid format
test.each(["half past one", "12:30:20", "7PM"])(
  `expect %s to be an invalid time`,
  (time) => {
    expect(activityController.isValidTime(time)).toBe(false);
  }
);

// Time frame exists
test.each([true, false])(
  "expect activity request to have time frame",
  (timeFrame) => {
    expect(activityController.hasTimeFrame(timeFrame)).toBe(true);
  }
);

//Time frame does not exist
test("expect undefined to be an invalid timeFrame", () => {
  expect(activityController.hasTimeFrame(undefined)).toBe(false);
});

// Set end date with time and offset of ISO 8601 form. Empty time string implies no input
// ISO 8601 format is a string of length
test.each([
  ["2022-12-25", ""],
  ["2020-12-31", "12:30"],
  ["2020-12-31", "19:20"],
])("expect end date to be formatted to ISO 8601", (endDate, time) => {
  expect(activityController.setEndDate(endDate, time)).toHaveLength(24);
});

// Set start date with time and offset of ISO 8601 form. Empty time string implies no input
// ISO 8601 format is a string of length
test.each([
  ["2022-12-25", ""],
  ["2020-12-31", "12:30"],
  ["2020-12-31", "19:20"],
])("expect end date to be formatted to ISO 8601", (startDate, time) => {
  expect(activityController.setStartDate(startDate, time)).toHaveLength(24);
});

// Reverse set end date and returns just the date which is a string of length 10
test.each(["2020-02-20T08:00:00+1300", "2020-02-20T08:00:00+1300"])(
  "expect end date to be formatted to ISO 8601",
  (endDateTime) => {
    expect(activityController.getDateFromISO(endDateTime)).toHaveLength(10);
  }
);

// Reverse set end date and returns just the date which is a string of length 10
test.each([
  ["2020-01-01", "", "2020-01-01T00:00:00+1300"],
  ["2021-01-02", "01:00", "2021-01-02T01:00:00+1300"],
  ["2021-01-02", "23:00", "2021-01-02T23:00:00+1300"],
])(
  "expect end date to be formatted to ISO 8601",
  (dateString, timeString, expected) => {
    expect(
      activityController.getApiDateTimeString(dateString, timeString)
    ).toBe(expected);
  }
);

test.each([
  [""], ["really long activity outcome participant result"], ["A result that's exactly 31 long"]
])(
  "expect result with invalid length to be rejected",
  async (result) => {
    await expect(
      activityController.createParticipantResult(1, 1, result, "date string")
    ).rejects.toThrow(
      new Error("Score's length should be between 0 and 30 characters")
    );
  }
)

const continuousCreateActivityRequest: CreateActivityRequest = {
  activity_name: "Non-stop running",
  description: "Run without stopping",
  activity_type: mockActivityTypes,
  continuous: true,
  start_time: "",
  end_time: "",
  location: "Christchurch, New Zealand",
};

const durationCreateActivityRequest: CreateActivityRequest = {
  activity_name: "One day run ",
  description: "Run for a full day",
  activity_type: mockActivityTypes,
  continuous: false,
  start_time: "2020-02-20T08:00:00+1300",
  end_time: "2020-03-20T08:00:00+1300",
  location: "Christchurch, New Zealand",
};

//Result list only contains duration based activities
test("expect duration activity list to not contain continuous activities", () => {
  let activityList = [];
  activityList.push(continuousCreateActivityRequest);
  activityList.push(durationCreateActivityRequest);
  let durationActivities = activityController.getDurationActivities(
    activityList
  );
  let hasContiuousActivity = false;
  for (let i = 0; i < durationActivities.length; i++) {
    if (durationActivities[i].continuous === true) {
      hasContiuousActivity = true;
    }
  }
  expect(hasContiuousActivity).toBe(false);
});

//Result list only contains continuous activities
test("expect new continuous activity list to not contain duration based activities", () => {
  let activityList = [];
  activityList.push(continuousCreateActivityRequest);
  activityList.push(durationCreateActivityRequest);
  let continuousActivities = activityController.getContinuousActivities(
    activityList
  );
  let hasDurationActivity = false;
  for (let i = 0; i < continuousActivities.length; i++) {
    if (continuousActivities[i].continuous === false) {
      hasDurationActivity = true;
    }
  }
  expect(hasDurationActivity).toBe(false);
});

test("expect getIsParticipating to return false if no role is found for the user", async () => {
  activityModel.getActivityRole = jest.fn(() => {
    return Promise.resolve(null);
  });
  let result = await activityController.getIsParticipating(5, 10);
  expect(result).toBe(false);
});

test.each(["creator", "organiser", "follower", "something else"])(
  "expect getIsParticipating to return false if the user is not a participant",
  async (returnedRole) => {
    activityModel.getActivityRole = jest.fn(() => {
      return Promise.resolve(returnedRole);
    });
    let result = await activityController.getIsParticipating(5, 10);
    expect(result).toBe(false);
  }
);

test.each(["participant", "Participant", "PARTICIPANT", "pArtIcIpANt"])(
  "expect getIsParticipating to return true if the user is a participant",
  async (returnedRole) => {
    activityModel.getActivityRole = jest.fn(() => {
      return Promise.resolve(returnedRole);
    });
    let result = await activityController.getIsParticipating(5, 10);
    expect(result).toBe(true);
  }
);
