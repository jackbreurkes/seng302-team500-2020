package com.springvuegradle.model.data;

import com.springvuegradle.model.requests.CreateActivityRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ActivityChangeLogTest {

	ActivityType testWalkingActivityType;
	ActivityType testJoggingActivityType;
	CreateActivityRequest continuousCreateActivityRequest;
	CreateActivityRequest durationCreateActivityRequest;

	@BeforeEach
	void beforeEach() {
		testWalkingActivityType = new ActivityType("Walking");
		testJoggingActivityType = new ActivityType("Jogging");

		continuousCreateActivityRequest = new CreateActivityRequest();
		continuousCreateActivityRequest.setActivityName("A New Name");
		continuousCreateActivityRequest.setContinuous(true);
		continuousCreateActivityRequest.setDescription("This is a continuous activity!");
		continuousCreateActivityRequest.setLocation("Earth II");
		continuousCreateActivityRequest.setActivityTypes(Arrays.asList(
				testWalkingActivityType.getActivityTypeName(),
				testJoggingActivityType.getActivityTypeName()));
	}

    @Test
    void testGetLogsForUpdateActivity_ContinuousToContinuous_MultipleChanges() {
		Activity oldActivityInfo = new Activity("Test Activity", false, "Test Location", Mockito.mock(Profile.class), Set.of(testWalkingActivityType));
		oldActivityInfo.setId(3L);
		User updatingUser = new User(5L);
		List<ChangeLog> changes = ActivityChangeLog.getLogsForUpdateActivity(oldActivityInfo, continuousCreateActivityRequest, updatingUser);

    }
}
