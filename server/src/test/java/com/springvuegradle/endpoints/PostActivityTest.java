package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.requests.CreateActivityRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PostActivityTest {

//    private ActivitiesController activitiesController;
//    private CreateActivityRequest createActivityRequest;
//
//    @BeforeEach
//    public void beforeEach() {
//        activitiesController = new ActivitiesController();
//
//        createActivityRequest = new CreateActivityRequest();
//        createActivityRequest.setActivityName("Kaikoura Coast Track race");
//        createActivityRequest.setDescription("A big and nice race on a lovely peninsula");
//        createActivityRequest.setActivityTypes(Arrays.asList("Walking", "Running"));
//        createActivityRequest.setContinuous(false);
//        createActivityRequest.setStartTime("2020-02-20T08:00:00+1300");
//        createActivityRequest.setEndTime("2020-02-20T08:00:00+1300");
//        createActivityRequest.setLocation("Kaikoura, NZ");
//    }
//
//    @Test
//    public void testCreateActivity() throws InvalidRequestFieldException, RecordNotFoundException {
//        String activityName = createActivityRequest.getActivityName();
//        Activity createdActivity = activitiesController.createActivity(1L, createActivityRequest);
//        assertEquals(activityName, createdActivity.getActivityName());
//    }
//
//    @Test
//    public void testInvalidRequestErrorIfNameMissing() {
//        createActivityRequest.setActivityName(null);
//        assertThrows(InvalidRequestFieldException.class, () -> {
//            activitiesController.createActivity(1L, createActivityRequest);
//        });
//    }
//
//    @Test
//    public void testInvalidRequestErrorIfDurationAndNoEndTime() {
//        createActivityRequest.setContinuous(false);
//        createActivityRequest.setEndTime(null);
//        assertThrows(InvalidRequestFieldException.class, () -> {
//            activitiesController.createActivity(1L, createActivityRequest);
//        });
//    }
//
//    @Test
//    public void testErrorIfNameUnderFourCharacters() {
//        createActivityRequest.setActivityName("tes");
//        assertThrows(MethodArgumentNotValidException.class, () -> {
//            activitiesController.createActivity(1L, createActivityRequest);
//        });
//    }

}