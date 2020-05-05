package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdateActivityRequest;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        UpdateActivityRequest updateActivityRequest = new UpdateActivityRequest();
        updateActivityRequest.setActivityName("222");

        assertThrows(InvalidRequestFieldException.class, () -> {
           activitiesController.putActivity(1L, 2L, updateActivityRequest, request);
        });
    }

    @Test
    void testMissingField(){
        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        UpdateActivityRequest updateActivityRequest = new UpdateActivityRequest();
        updateActivityRequest.setActivityName("Activity Name");

        assertThrows(InvalidRequestFieldException.class, () -> {
           activitiesController.putActivity(1L, 2L, updateActivityRequest, request);
        });
    }

    @Test
    void testActivityDoesntExist(){
        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        UpdateActivityRequest updateActivityRequest = createValidUpdateRequest();
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
           activitiesController.putActivity(1L, 2L, updateActivityRequest, request);
        });
    }

    @Test
    void testActivityTypeDoesntExists(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        UpdateActivityRequest updateActivityRequest = createValidUpdateRequest();
        updateActivityRequest.setActivityTypes(new ArrayList<>(Arrays.asList("Invalid")));

        Activity activity = new Activity();
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        assertThrows(RecordNotFoundException.class, () -> {
           activitiesController.putActivity(1L, 2L, updateActivityRequest, request);
        });
    }

    @Test
    void testUpdatingOthersActivity(){
        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        UpdateActivityRequest updateActivityRequest = createValidUpdateRequest();
        Activity activity = new Activity();
        Mockito.when(activityRepository.findById(3L)).thenReturn(Optional.of(activity));
        assertThrows(UserNotAuthenticatedException.class, () -> {
            activitiesController.putActivity(2L, 3L, updateActivityRequest, request);
        });
    }

    @Test
    @Disabled
    void testUpdateActivityAsAdmin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        //need to set as admin
        user.setPermissionLevel(126);

        UpdateActivityRequest updateActivityRequest = createValidUpdateRequest();
        Activity activity = new Activity();
        activity.setActivityTypes(new ArrayList<>());

        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        assertEquals(activitiesController.putActivity(3L, 2L, updateActivityRequest, request), HttpStatus.OK);
    }

    @Test
    @Disabled
    void testUpdateActivity() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        UpdateActivityRequest updateActivityRequest = createValidUpdateRequest();
        Activity activity = new Activity();
        activity.setActivityTypes(new ArrayList<>());

        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        assertEquals(activitiesController.putActivity(1L, 2L, updateActivityRequest, request), HttpStatus.OK);
    }

    /**
     * Helper function for creating valid update request
     */
    UpdateActivityRequest createValidUpdateRequest(){
        UpdateActivityRequest ret = new UpdateActivityRequest();

        ret.setActivityName("Activity Name");
        ret.setActivityTypes(new ArrayList<String>(Arrays.asList("Running", "Swimming")));
        ret.setContinuous(true);
        ret.setLocation("Alabama");
        ret.setDescription("Test Description");
        return ret;
    }

}
