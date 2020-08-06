package com.springvuegradle.model.data;

import com.springvuegradle.model.requests.CreateActivityRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@Disabled // TODO activate
public class ActivityChangeLogTest {

	final String TIME_FRAME_FORMAT = "{\"start_time\": \"%s\", \"end_time\": \"%s\"}";

	ActivityType testWalkingActivityType;
	ActivityType testJoggingActivityType;
	CreateActivityRequest continuousCreateActivityRequest;
	CreateActivityRequest durationCreateActivityRequest;

	@BeforeEach
	void beforeEach() {
		testWalkingActivityType = new ActivityType("Walking");
		testJoggingActivityType = new ActivityType("Jogging");

		continuousCreateActivityRequest = new CreateActivityRequest();
		continuousCreateActivityRequest.setActivityName("Continuous Create Activity Request");
		continuousCreateActivityRequest.setContinuous(true);
		continuousCreateActivityRequest.setDescription("This is a continuous create activity request!");
		continuousCreateActivityRequest.setLocation("Earth II");
		continuousCreateActivityRequest.setActivityTypes(Arrays.asList(
				testWalkingActivityType.getActivityTypeName(),
				testJoggingActivityType.getActivityTypeName()));

		durationCreateActivityRequest = new CreateActivityRequest();
		durationCreateActivityRequest.setActivityName("Duration Create Activity Request");
		durationCreateActivityRequest.setContinuous(false);
		durationCreateActivityRequest.setStartTime("test start time");
		durationCreateActivityRequest.setStartTime("test end time");
		durationCreateActivityRequest.setDescription("This is a duration-based create activity request!");
		durationCreateActivityRequest.setLocation("Earth II");
		durationCreateActivityRequest.setActivityTypes(Arrays.asList(
				testWalkingActivityType.getActivityTypeName(),
				testJoggingActivityType.getActivityTypeName()));
	}

    @Test
    void testGetLogsForUpdateActivity_ContinuousToContinuous_MultipleChanges() {
		Activity oldActivityInfo = new Activity("Test Activity", false, "Test Location", Mockito.mock(Profile.class), Set.of(testWalkingActivityType));
		oldActivityInfo.setId(3L);
		oldActivityInfo.setDescription("this is an old description");
		User updatingUser = new User(5L);
		List<ChangeLog> changes = ActivityChangeLog.getLogsForUpdateActivity(oldActivityInfo, continuousCreateActivityRequest, updatingUser);

		ChangeLog existenceChange = changes.stream().filter(changeLog -> changeLog.changedAttribute == ChangedAttribute.ACTIVITY_EXISTENCE).findAny().orElse(null);
		ChangeLog nameChange = changes.stream().filter(changeLog -> changeLog.changedAttribute == ChangedAttribute.ACTIVITY_NAME).findAny().orElse(null);
		ChangeLog descriptionChange = changes.stream().filter(changeLog -> changeLog.changedAttribute == ChangedAttribute.ACTIVITY_DESCRIPTION).findAny().orElse(null);
		ChangeLog timeFrameChange = changes.stream().filter(changeLog -> changeLog.changedAttribute == ChangedAttribute.ACTIVITY_TIME_FRAME).findAny().orElse(null);
		ChangeLog locationChange = changes.stream().filter(changeLog -> changeLog.changedAttribute == ChangedAttribute.ACTIVITY_LOCATION).findAny().orElse(null);

		assertNull(existenceChange);
		assertNull(timeFrameChange);
		assertNotNull(nameChange);
		assertNotNull(descriptionChange);
		assertNotNull(locationChange);

		assertTrue(changes.stream().allMatch(changeLog -> changeLog.getEntity() == ChangeLogEntity.ACTIVITY));
		assertTrue(changes.stream().allMatch(changeLog -> changeLog.getEntityId() == oldActivityInfo.getId()));
		assertTrue(changes.stream().allMatch(changeLog -> changeLog.getActionType() == ActionType.UPDATED));
		assertTrue(changes.stream().allMatch(changeLog -> changeLog.getEditingUser() == updatingUser));

		assertEquals(oldActivityInfo.getActivityName(), nameChange.getOldValue());
		assertEquals(durationCreateActivityRequest.getActivityName(), nameChange.getNewValue());
	}

	@Test
	void testGetLogsUpdateActivity_ChangedName() {
		// below ensures that only the name is updated
		CreateActivityRequest newInfo = continuousCreateActivityRequest;
		Activity oldActivityInfo = new Activity("Old Activity Name", newInfo.isContinuous(), newInfo.getLocation(), Mockito.mock(Profile.class), Set.of(testWalkingActivityType));
		oldActivityInfo.setId(3L);
		oldActivityInfo.setDescription(newInfo.getDescription());
		newInfo.setActivityTypes(Collections.singletonList(testWalkingActivityType.getActivityTypeName()));

		User updatingUser = new User(5);
		List<ChangeLog> changes = ActivityChangeLog.getLogsForUpdateActivity(oldActivityInfo, continuousCreateActivityRequest, updatingUser);
		assertEquals(1, changes.size());
		ChangeLog nameChange = changes.get(0);
		assertEquals(ChangedAttribute.ACTIVITY_NAME, nameChange.getChangedAttribute());
		assertEquals(oldActivityInfo.getActivityName(), nameChange.getOldValue());
		assertEquals(continuousCreateActivityRequest.getActivityName(), nameChange.getNewValue());
	}

	@Test
	void testGetLogsUpdateActivity_ChangedDescription() {
		// below ensures that only the description is updated
		CreateActivityRequest newInfo = continuousCreateActivityRequest;
		Activity oldActivityInfo = new Activity(newInfo.getActivityName(), newInfo.isContinuous(), newInfo.getLocation(), Mockito.mock(Profile.class), Set.of(testWalkingActivityType));
		oldActivityInfo.setId(3L);
		oldActivityInfo.setDescription("this is the old description");
		newInfo.setActivityTypes(Collections.singletonList(testWalkingActivityType.getActivityTypeName()));

		User updatingUser = new User(5);
		List<ChangeLog> changes = ActivityChangeLog.getLogsForUpdateActivity(oldActivityInfo, newInfo, updatingUser);
		assertEquals(1, changes.size());
		ChangeLog descriptionChange = changes.get(0);
		assertEquals(ChangedAttribute.ACTIVITY_DESCRIPTION, descriptionChange.getChangedAttribute());
		assertEquals(oldActivityInfo.getDescription(), descriptionChange.getOldValue());
		assertEquals(newInfo.getDescription(), descriptionChange.getNewValue());
	}

	@Test
	void testGetLogsUpdateActivity_ChangedLocation() {
		// below ensures that only the location is updated
		CreateActivityRequest newInfo = continuousCreateActivityRequest;
		Activity oldActivityInfo = new Activity(newInfo.getActivityName(), newInfo.isContinuous(), "Old Location", Mockito.mock(Profile.class), Set.of(testWalkingActivityType));
		oldActivityInfo.setId(3L);
		oldActivityInfo.setDescription(newInfo.getDescription());
		newInfo.setActivityTypes(Collections.singletonList(testWalkingActivityType.getActivityTypeName()));

		User updatingUser = new User(5);
		List<ChangeLog> changes = ActivityChangeLog.getLogsForUpdateActivity(oldActivityInfo, newInfo, updatingUser);
		assertEquals(1, changes.size());
		ChangeLog locationChange = changes.get(0);
		assertEquals(ChangedAttribute.ACTIVITY_LOCATION, locationChange.getChangedAttribute());
		assertEquals(oldActivityInfo.getLocation(), locationChange.getOldValue());
		assertEquals(newInfo.getLocation(), locationChange.getNewValue());
	}

	@Test
	void testGetLogsUpdateActivity_ChangedTimeFrame_DurationToDuration() {
		// below ensures that only the time frame is updated
		CreateActivityRequest newInfo = durationCreateActivityRequest;
		Activity oldActivityInfo = new Activity(newInfo.getActivityName(), true, newInfo.getLocation(), Mockito.mock(Profile.class), Set.of(testWalkingActivityType));
		oldActivityInfo.setStartTime("old start time");
		oldActivityInfo.setEndTime("old end time");
		oldActivityInfo.setId(3L);
		oldActivityInfo.setDescription(newInfo.getDescription());
		newInfo.setActivityTypes(Collections.singletonList(testWalkingActivityType.getActivityTypeName()));

		User updatingUser = new User(5);
		List<ChangeLog> changes = ActivityChangeLog.getLogsForUpdateActivity(oldActivityInfo, newInfo, updatingUser);
		assertEquals(1, changes.size());
		ChangeLog timeFrameChange = changes.get(0);
		assertEquals(ChangedAttribute.ACTIVITY_TIME_FRAME, timeFrameChange.getChangedAttribute());
		assertEquals(ActionType.UPDATED, timeFrameChange.getActionType());
		String oldTimeFrameJson = String.format(TIME_FRAME_FORMAT, oldActivityInfo.getStartTime(), oldActivityInfo.getEndTime());
		String newTimeFrameJson = String.format(TIME_FRAME_FORMAT, newInfo.getStartTime(), newInfo.getEndTime());
		assertEquals(oldTimeFrameJson, timeFrameChange.getOldValue());
		assertEquals(newTimeFrameJson, timeFrameChange.getNewValue());
	}

	@Test
	void testGetLogsUpdateActivity_ChangedTimeFrame_ContinuousToDuration() {
		// below ensures that only the time frame is updated
		CreateActivityRequest newInfo = durationCreateActivityRequest;
		Activity oldActivityInfo = new Activity(newInfo.getActivityName(), false, newInfo.getLocation(), Mockito.mock(Profile.class), Set.of(testWalkingActivityType));
		oldActivityInfo.setId(3L);
		oldActivityInfo.setDescription(newInfo.getDescription());
		newInfo.setActivityTypes(Collections.singletonList(testWalkingActivityType.getActivityTypeName()));

		User updatingUser = new User(5);
		List<ChangeLog> changes = ActivityChangeLog.getLogsForUpdateActivity(oldActivityInfo, newInfo, updatingUser);
		assertEquals(1, changes.size());
		ChangeLog timeFrameChange = changes.get(0);
		assertEquals(ChangedAttribute.ACTIVITY_TIME_FRAME, timeFrameChange.getChangedAttribute());
		assertEquals(ActionType.CREATED, timeFrameChange.getActionType());
		String newTimeFrameJson = String.format(TIME_FRAME_FORMAT, newInfo.getStartTime(), newInfo.getEndTime());
		assertNull(timeFrameChange.getOldValue());
		assertEquals(newTimeFrameJson, timeFrameChange.getNewValue());
	}

	@Test
	void testGetLogsUpdateActivity_ChangedTimeFrame_DurationToContinuous() {
		// below ensures that only the time frame is updated
		CreateActivityRequest newInfo = continuousCreateActivityRequest;
		Activity oldActivityInfo = new Activity(newInfo.getActivityName(), true, newInfo.getLocation(), Mockito.mock(Profile.class), Set.of(testWalkingActivityType));
		oldActivityInfo.setStartTime("old start time");
		oldActivityInfo.setEndTime("old end time");
		oldActivityInfo.setId(3L);
		oldActivityInfo.setDescription(newInfo.getDescription());
		newInfo.setActivityTypes(Collections.singletonList(testWalkingActivityType.getActivityTypeName()));

		User updatingUser = new User(5);
		List<ChangeLog> changes = ActivityChangeLog.getLogsForUpdateActivity(oldActivityInfo, newInfo, updatingUser);
		assertEquals(1, changes.size());
		ChangeLog timeFrameChange = changes.get(0);
		assertEquals(ChangedAttribute.ACTIVITY_TIME_FRAME, timeFrameChange.getChangedAttribute());
		assertEquals(ActionType.DELETED, timeFrameChange.getActionType());
		String oldTimeFrameJson = String.format(TIME_FRAME_FORMAT, oldActivityInfo.getStartTime(), oldActivityInfo.getEndTime());
		assertEquals(oldTimeFrameJson, timeFrameChange.getOldValue());
		assertEquals(null, timeFrameChange.getNewValue());
	}

	// TODO test activity types

}
