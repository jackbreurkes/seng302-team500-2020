package com.springvuegradle.endpoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.SubscriptionRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.responses.ActivityResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetActivitiesByCreatorTest {
    @InjectMocks
    private ActivitiesController activitiesController;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ActivityRepository activityRepository;
    
    @Mock
    private SubscriptionRepository subscriptionRepo;
    
    @Mock
    private UserActivityRoleRepository userActivityRoleRepository;

    private User mockUser;
    private Profile mockProfile;
    private Activity mockActivity;
    private MockHttpServletRequest request;

    @BeforeAll
    void setup(){
        MockitoAnnotations.initMocks(this);
        
        Mockito.when(subscriptionRepo.getFollowerCount(Mockito.anyLong())).thenReturn(0L);
        Mockito.when(userActivityRoleRepository.getParticipantCountByActivityId(Mockito.anyLong())).thenReturn(0L);
    }

    @BeforeEach
    void beforeEach(){
        this.mockUser = new User(1L);
        this.mockProfile = new Profile(mockUser, "Mocko", "Mockitoson", LocalDate.EPOCH, Gender.NON_BINARY);
        ActivityType mockWalking = new ActivityType("Walking Mock");
        ActivityType mockRunning = new ActivityType("Running Mock");
        mockActivity = new Activity("Mock activity 1", false, "Kaikoura", mockProfile, new HashSet<ActivityType>(Arrays.asList(mockWalking, mockRunning)));
        Mockito.when(profileRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockProfile));
        Mockito.when(activityRepository.findActivitiesByCreator(this.mockProfile)).thenReturn(Arrays.asList(mockActivity));

        this.request = new MockHttpServletRequest();
        this.request.setAttribute("authenticatedid", mockUser.getUserId());
    }

    @Test
    void testGetActivitiesByCreator() throws UserNotAuthenticatedException, RecordNotFoundException {
        List<ActivityResponse> activities = activitiesController.getActivitiesByCreator(this.mockUser.getUserId(), request);
        assertEquals(1, activities.size());
        ActivityResponse responseActivity = activities.get(0);
        assertEquals(mockActivity.getActivityName(), responseActivity.getActivityName());
        
        assertTrue(responseActivity.getActivityTypes().contains("Walking Mock"));
        assertTrue(responseActivity.getActivityTypes().contains("Running Mock"));
        assertEquals(2, responseActivity.getActivityTypes().size());
        
        assertEquals(mockActivity.getCreator().getUser().getUserId(), responseActivity.getCreatorId());
        assertEquals(mockActivity.getDescription(), responseActivity.getDescription());
        assertEquals(mockActivity.getLocation(), responseActivity.getLocation());
        assertEquals(mockActivity.isDuration(), !responseActivity.isContinuous());
    }

    @Test
    void testGetByCreatorWithNoActivities() throws UserNotAuthenticatedException, RecordNotFoundException {
        Mockito.when(activityRepository.findActivitiesByCreator(this.mockProfile)).thenReturn(new ArrayList<>());
        List<ActivityResponse> activities = activitiesController.getActivitiesByCreator(this.mockUser.getUserId(), request);
        assertEquals(0, activities.size());
    }

    @Test
    void testGetByNonExistingCreator() {
        Mockito.when(profileRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RecordNotFoundException.class, () -> {
            activitiesController.getActivitiesByCreator(2L, request);
        });
    }
}
