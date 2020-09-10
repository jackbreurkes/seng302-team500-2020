package com.springvuegradle.endpoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

<<<<<<< server/src/test/java/com/springvuegradle/endpoints/GetActivityTest.java
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.requests.SearchActivityRequest;
import com.springvuegradle.model.responses.ProfileResponse;
=======
>>>>>>> server/src/test/java/com/springvuegradle/endpoints/GetActivityTest.java
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
import com.springvuegradle.model.data.ActivityRole;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.data.UserActivityRole;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.SubscriptionRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.responses.ActivityResponse;
import com.springvuegradle.model.responses.UserActivityRoleResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetActivityTest {

    @InjectMocks
    private ActivitiesController activitiesController;

    @InjectMocks
    private ActivitySearchController activitySearchController;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SubscriptionRepository subscriptionRepo;

    @Mock
    private UserActivityRoleRepository userActivityRoleRepository;

    private Profile profile;

    @BeforeAll
    public void setUp() {
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
        profile = new Profile(new User(1L), "David", "Clarke", LocalDate.now(), Gender.FEMALE);
    }

    @Test
    void testAuthorisedUser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        Mockito.when(profileRepository.findById(profile.getUser().getUserId())).thenReturn(Optional.ofNullable(profile));
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));
        ActivityResponse response = activitiesController.getActivity(1L, 2L, request);
        assertEquals(response.getActivityName(), "Test");
    }

    @Test
    void testActivityCreatorNotFound() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        long nonExistentCreatorId = 5L;
        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        Mockito.when(profileRepository.findById(nonExistentCreatorId)).thenReturn(Optional.empty());
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));
        assertThrows(RecordNotFoundException.class, () -> {
            activitiesController.getActivity(nonExistentCreatorId, 2L, request);
        });
    }


    @Test
    void testUnauthorisedUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(UserNotAuthenticatedException.class, () -> {
            activitiesController.getActivity(2L, 2L, request);
        });
    }

    @Test
    void testActivityDoesntExist() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            activitiesController.getActivity(1L, 2L, request);
        });
    }

    // --------- Test for GET /activities/activityId -------------------------

    @Test
    void testAuthorisedUserGetActivityWithoutCreatorId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));
        ActivityResponse response = activitiesController.getSingleActivity(2L, request);
        assertEquals(response.getActivityName(), "Test");
    }


    @Test
    void testUnauthorisedUserGetActivityWithoutCreatorId() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(UserNotAuthenticatedException.class, () -> {
            activitiesController.getSingleActivity(2L, request);
        });
    }

    @Test
    void testGetNonexistentActivityWithoutCreatorId() {
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

    @Test
    void testGetActivityParticipantsWithNoParticipants() throws UserNotAuthenticatedException, RecordNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        activity.setId(4L);

        Mockito.when(activityRepository.getOne(4L)).thenReturn(activity);

        List<UserActivityRoleResponse> profiles = activitiesController.getProfilesInvolvedWithActivity(4L, request);

        assertEquals(1, profiles.size()); //1 because the creator is always participating in their activity
    }

    @Test
    void testGetActivityParticipantsReturnsTwo() throws UserNotAuthenticatedException, RecordNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        activity.setId(2L);

        Mockito.when(activityRepository.getOne(2L)).thenReturn(activity);

        User user = new User(3l);
        Profile profile = new Profile(user, "Misha", "Morgun", LocalDate.now(), Gender.MALE);

        List<User> userList = new ArrayList<>();
        userList.add(user);
        
        Mockito.when(userActivityRoleRepository.getInvolvedUsersByActivityId(2l)).thenReturn(userList);
        
        UserActivityRole role = new UserActivityRole(activity, user, ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(3l, 2l)).thenReturn(Optional.of(role));

        Mockito.when(profileRepository.findById(3l)).thenReturn(Optional.of(profile));

        List<UserActivityRoleResponse> profiles = activitiesController.getProfilesInvolvedWithActivity(2L, request);
        
        assertEquals(2, profiles.size(), "Activity should have 2 people involved: the creator and the person participating");
    }
}