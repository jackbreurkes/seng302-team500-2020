import { addActivityType, removeActivityType } from "./activity.controller";
import { CreateActivityRequest } from "../scripts/Activity";

import axios from "axios";

jest.mock("axios");

const activityModel = require("../models/activity.model");
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

// --------- ACTIVITY TYPES ---------- //
test.each(["Walking", "Existing", "Whatever"])(
  "expect addActivityType to throw an error if %s is already added to the activity",
  async (existingActivityType) => {
    let activityData: CreateActivityRequest = {
      activity_type: [existingActivityType],
    };
    await expect(
      addActivityType(existingActivityType, activityData)
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
      addActivityType(fakeActivityType, activityData)
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
        addActivityType(validActivityType, activityData)
      ).resolves.toBe(undefined);
      expect(activityData.activity_type).toHaveLength(1);
      expect(activityData.activity_type).toContain(validActivityType);
      // this expect doesn't work, but will only throw an error if axios.post is called, meaning it does what we need it to. ¯\_(ツ)_/¯
      expect(axios.post).toHaveBeenCalledTimes(0); // changes should not be persisted automatically
    }
  );
