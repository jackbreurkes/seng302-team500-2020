package com.springvuegradle.endpoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.SubscriptionRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.responses.ActivityResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetActivityTest {

    @InjectMocks
    private ActivitiesController activitiesController;

    @Mock
    private ActivityRepository activityRepository;
    
    @Mock
    private SubscriptionRepository subscriptionRepo;
    
    @Mock
    private UserActivityRoleRepository userActivityRoleRepository;

    private Profile profile;

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
        profile = new Profile(new User(1L), "David", "Clarke", LocalDate.now(), Gender.FEMALE);
    }

    @Test
    void testAuthorisedUser() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));
        ActivityResponse response = activitiesController.getActivity(1L,2L, request);
        assertEquals(response.getActivityName(), "Test");
    }


    @Test
    void testUnauthorisedUser(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(UserNotAuthenticatedException.class, () -> {
            activitiesController.getActivity(2L, 2L, request);
        });
    }

    @Test
    void testActivityDoesntExist(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
           activitiesController.getActivity(1L, 2L, request);
        });
    }

    // --------- Test for GET /activities/activityId -------------------------

    @Test
    void testAuthorisedUserGetActivityWithoutCreatorId() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));
        ActivityResponse response = activitiesController.getSingleActivity(2L, request);
        assertEquals(response.getActivityName(), "Test");
    }


    @Test
    void testUnauthorisedUserGetActivityWithoutCreatorId(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(UserNotAuthenticatedException.class, () -> {
            activitiesController.getSingleActivity(2L, request);
        });
    }

    @Test
    void testGetNonexistentActivityWithoutCreatorId(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            activitiesController.getSingleActivity(2L, request);
        });
    }
    
    @ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 10, 100})
    void testActivityWithFollowers(int followers) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        activity.setId(2);
        
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));
        Mockito.when(subscriptionRepo.getFollowerCount(2L)).thenReturn(Long.valueOf(followers));
        Mockito.when(userActivityRoleRepository.getParticipantCountByActivityId(2L)).thenReturn(Long.valueOf(0));
        
        ActivityResponse response = activitiesController.getSingleActivity(2L, request);
        assertEquals(followers, response.getNumFollowers());
    }
    
    @ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 10, 100})
    void testActivityWithParticipants(int participants) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        activity.setId(2);
        
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));
        Mockito.when(subscriptionRepo.getFollowerCount(2L)).thenReturn(0L);
        Mockito.when(userActivityRoleRepository.getParticipantCountByActivityId(2L)).thenReturn(Long.valueOf(participants));
        
        ActivityResponse response = activitiesController.getSingleActivity(2L, request);
        assertEquals(participants, response.getNumParticipants());
    }

}
