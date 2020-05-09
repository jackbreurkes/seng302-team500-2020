package com.springvuegradle.endpoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.CreateActivityRequest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PutActivityTest {
    @InjectMocks
    private ActivitiesController activitiesController;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivityTypeRepository activityTypeRepository;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeAll
    void beforeAll(){
        activitiesController = new ActivitiesController();
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void beforeEach(){
        user = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ActivityType running = new ActivityType("Running");
        ActivityType swimming = new ActivityType("Swimming");

        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Running")).thenReturn(Optional.of(running));
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Swimming")).thenReturn(Optional.of(swimming));
    }

    @Test
    void testInvalidActivityName(){
        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        CreateActivityRequest CreateActivityRequest = new CreateActivityRequest();
        CreateActivityRequest.setActivityName("222");

        assertThrows(InvalidRequestFieldException.class, () -> {
           activitiesController.putActivity(1L, 2L, CreateActivityRequest, request);
        });
    }

    @Test
    void testMissingField(){
        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        CreateActivityRequest CreateActivityRequest = new CreateActivityRequest();
        CreateActivityRequest.setActivityName("Activity Name");

        assertThrows(InvalidRequestFieldException.class, () -> {
           activitiesController.putActivity(1L, 2L, CreateActivityRequest, request);
        });
    }

    @Test
    void testActivityDoesntExist(){
        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        CreateActivityRequest CreateActivityRequest = createValidUpdateRequest();
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
           activitiesController.putActivity(1L, 2L, CreateActivityRequest, request);
        });
    }

    @Test
    void testActivityTypeDoesntExists(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        CreateActivityRequest CreateActivityRequest = createValidUpdateRequest();
        CreateActivityRequest.setActivityTypes(new ArrayList<>(Arrays.asList("Invalid")));

        Activity activity = new Activity();
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        assertThrows(RecordNotFoundException.class, () -> {
           activitiesController.putActivity(1L, 2L, CreateActivityRequest, request);
        });
    }

    @Test
    void testUpdatingOthersActivity(){
        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        CreateActivityRequest CreateActivityRequest = createValidUpdateRequest();
        Activity activity = new Activity();
        Mockito.when(activityRepository.findById(3L)).thenReturn(Optional.of(activity));
        assertThrows(UserNotAuthenticatedException.class, () -> {
            activitiesController.putActivity(2L, 3L, CreateActivityRequest, request);
        });
    }

    @Test
    @Disabled
    void testUpdateActivityAsAdmin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        //need to set as admin
        user.setPermissionLevel(126);

        CreateActivityRequest CreateActivityRequest = createValidUpdateRequest();
        Activity activity = new Activity();
        activity.setActivityTypes(new HashSet<>());

        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        assertEquals(activitiesController.putActivity(3L, 2L, CreateActivityRequest, request), HttpStatus.OK);
    }

    @Test
    @Disabled
    void testUpdateActivity() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        CreateActivityRequest CreateActivityRequest = createValidUpdateRequest();
        Activity activity = new Activity();
        activity.setActivityTypes(new HashSet<>());

        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        assertEquals(activitiesController.putActivity(1L, 2L, CreateActivityRequest, request), HttpStatus.OK);
    }

    /**
     * Helper function for creating valid update request
     */
    CreateActivityRequest createValidUpdateRequest(){
        CreateActivityRequest ret = new CreateActivityRequest();

        ret.setActivityName("Activity Name");
        ret.setActivityTypes(new ArrayList<String>(Arrays.asList("Running", "Swimming")));
        ret.setContinuous(true);
        ret.setLocation("Alabama");
        ret.setDescription("Test Description");
        return ret;
    }

}
