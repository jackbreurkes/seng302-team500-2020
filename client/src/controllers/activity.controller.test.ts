import * as activityController from "./activity.controller";
import { CreateActivityRequest } from "../scripts/Activity";

const locationModel = require("../models/location.model");
const activityModel = require("../models/activity.model");
var today = new Date().toISOString().slice(0, 10);
activityModel.createActivity = jest.fn();

const mockActivityTypes: string[] = [
  "Walking",
  "Running",
  "Swimming",
  "Dancing",
];

let mockCreateActivityRequest: CreateActivityRequest;

activityModel.loadAvailableActivityTypes = jest.fn(async () => {
  return mockActivityTypes;
});

beforeEach(() => {
  mockCreateActivityRequest = {
    activity_name: "Raid Area 52",
    description: "Naruto run through area 52",
    activity_type: mockActivityTypes,
    continuous: true,
    start_time: "",
    end_time: "",
    location: "Christchurch, New Zealand",
  };
})

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
  "x".repeat(51), //51 x's
  "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx".repeat(65), //65 x's
])("test that activity names over 50 characters are invalid", (validName) => {
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
test.each(["half past one", "12:30:20", "7PM", "10:00pm", "001:00", "10:000"])(
  `expect %s to be an invalid time`,
  (time) => {
    expect(activityController.isValidTime(time)).toBe(false);
  }
);

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
      new Error("The given result should be between 0 and 30 characters")
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


test.each([
  //        START            |           END             |            TIME          //
  ["2020-02-20T00:00:00+1300", "2020-02-21T00:00:00+1300", "2020-02-20T12:00:00+1300"],
  ["2020-02-20T00:00:00+1300", "2020-02-20T00:00:02+1300", "2020-02-20T00:00:01+1300"],
  ["2020-02-20T10:00:00+1300", "2021-11-05T01:00:00+1300", "2020-09-01T09:00:00+1300"],
])(
  "expect timeIsWithinRange to return true for arguments %s, %s, %s",
  (start, end, time) => {
    expect(activityController.timeIsWithinRange(start, end, time)).toBe(true);
  }
)


test.each([
  //        START            |           END             |            TIME          //
  ["2020-02-20T00:00:00+1300", "2020-02-21T00:00:00+1300", "2020-02-21T11:00:00+1300"],
  ["2020-02-20T00:00:00+1300", "2020-02-20T00:00:02+1300", "2020-02-20T00:00:03+1300"],
  ["2020-02-20T10:00:00+1300", "2020-11-05T01:00:00+1300", "2021-09-01T09:00:00+1300"],
])(
  "expect timeIsWithinRange to return false for arguments %s, %s, %s",
  (start, end, time) => {
    expect(activityController.timeIsWithinRange(start, end, time)).toBe(false);
  }
)


test.each([
  ["11:00", "2020-02-21T11:00:00+1300"],
  ["00:50", "2564-02-20T00:50:13+1300"],
  ["00:00", "1911-09-01T00:00:00+1300"],
])(
  "expect getTimeFromISO to return %s for an ISO timestamp of %s",
  (time, isoTimestamp) => {
    expect(activityController.getTimeFromISO(isoTimestamp)).toBe(time);
  }
)


test.each(["", "sm", "this one is way too long and shouldnt be alloweduyuytfytfytf"])(
  "expect validateNewActivity to throw an error if the activity name is invalid",
  async (name) => {
    mockCreateActivityRequest.activity_name = name;
    await expect((activityController.validateNewActivity("", "", "", "", mockCreateActivityRequest))
    ).rejects.toThrow("Please enter an activity name of 4-50 characters long")
  }
)

test(
  "expect validateNewActivity to throw an error if the activity has no time frame",
  async () => {
    delete mockCreateActivityRequest.continuous;
    await expect((activityController.validateNewActivity("", "", "", "", mockCreateActivityRequest))
    ).rejects.toThrow("Please select a time frame")
  }
)

test(
  "expect validateNewActivity to throw an error if a duration activity has no start date",
  async () => {
    mockCreateActivityRequest.continuous = false;
    await expect((activityController.validateNewActivity("", "", "", "", mockCreateActivityRequest))
    ).rejects.toThrow("Duration based activities must have a start date")
  }
)

test(
  "expect validateNewActivity to throw an error if a duration activity has no end date",
  async() => {
    mockCreateActivityRequest.continuous = false;
    await expect((activityController.validateNewActivity("2020-10-10", "", "", "", mockCreateActivityRequest))
    ).rejects.toThrow("Duration based activities must have an end date")
  }
)

test.each(["12pm", "midnight", "0800", "10:00pm", "5"])(
  "expect validateNewActivity to throw an error for malformed start time %s",
  async (startTime) => {
    mockCreateActivityRequest.continuous = false;
    await expect((activityController.validateNewActivity("2020-10-10", startTime, "", "", mockCreateActivityRequest))
    ).rejects.toThrow("Start time is not in valid format")
  }
)

test(
  "expect validateNewActivity to throw an error if end date is before start date",
  async () => {
    mockCreateActivityRequest.continuous = false;
    await expect((activityController.validateNewActivity("2020-10-10", "", "2020-01-01", "", mockCreateActivityRequest))
    ).rejects.toThrow("End date must be after start date")
  }
)

test.each(["12pm", "midnight", "0800", "10:00pm", "5"])(
  "expect validateNewActivity to throw an error for malformed end time %s",
  async (endTime) => {
    mockCreateActivityRequest.continuous = false;
    await expect(activityController.validateNewActivity("2020-10-10", "10:10", "2020-11-11", endTime, mockCreateActivityRequest)
    ).rejects.toThrow("End time is not in valid format")
  }
)


test(
  "expect validateNewActivity to throw an error if the end date is before the start date",
  async() => {
    mockCreateActivityRequest.continuous = false;
    await expect(activityController.validateNewActivity("2020-10-10", "", "2020-01-11", "", mockCreateActivityRequest)
    ).rejects.toThrow(
      new Error("End date must be after start date")
    );
  }
)


test.each([
  ["2020-10-10", "", "2020-11-11", ""]
])(
  "expect validateNewActivity to set the activity start and end times if the timeframe is duration",
  (startDate, startTime, endDate, endTime) => {
    mockCreateActivityRequest.continuous = false;
    activityController.validateNewActivity(startDate, startTime, endDate, endTime, mockCreateActivityRequest)
    expect(mockCreateActivityRequest.start_time).toBe(activityController.setStartDate(startDate, startTime))
    expect(mockCreateActivityRequest.end_time).toBe(activityController.setEndDate(endDate, endTime))
  }
)

test(
  "expect validateNewActivity to throw an error if location is undefined",
  async() => {
    mockCreateActivityRequest.location = undefined;
    await expect(activityController.validateNewActivity("2020-10-10", "", "2020-01-11", "", mockCreateActivityRequest)
    ).rejects.toThrow(
      new Error("Please enter the location of the activity")
    );
  }
)

test.each([
  ["iygtiiutgiut5g4f8uy", "~!#^&%&^@", "awdadwqadwadada", "qwertyuio"]
  ])(
  "expect validateNewActivity to throw an error if location could not be found",
  async(invalidLocation) => {
    mockCreateActivityRequest.location = invalidLocation;
    await expect(activityController.validateNewActivity("2020-10-10", "", "2020-01-11", "", mockCreateActivityRequest)
    ).rejects.toThrow(
      new Error("Can't find a valid location with that address, try again")
    );
  }
)
test.each([
  ["Christchurch new Zealand", "18b CLYDE ROAD UPPER RICCARTON", "Jack Erskine"]
  ])(
  "expect validateNewActivity to find and validate these locations",
  async(validLocation) => {
    
    mockCreateActivityRequest.location = validLocation;
    let result = await locationModel.getAddressFormattedString(validLocation)
    expect(result).toHaveLength(1)
  }
)