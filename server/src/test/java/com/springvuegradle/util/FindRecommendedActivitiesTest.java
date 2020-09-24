package com.springvuegradle.util;

import com.springvuegradle.endpoints.HomeFeedController;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FindRecommendedActivitiesTest {
    @InjectMocks
    private HomeFeedController homeFeedController;

    @Mock
    ChangeLogRepository changeLogRepository;

    @Mock
    ActivityPinRepository activityPinRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserActivityRoleRepository userActivityRoleReporsitory;

    @Mock
    ActivityRepository activityRepository;

    @Mock
    ProfileRepository profileRepository;

    @Mock
    SubscriptionRepository subscriptionRepository;

    private Profile selfProfile;
    private ActivityType selfActivityType;
    private Profile otherProfile;

    private static float BOUNDING_BOX_SIZE = 0.2f;

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
        selfProfile = new Profile(new User(1L), "David", "Clarke", LocalDate.now(), Gender.FEMALE);
        selfProfile.setLocation(new Location("Christchurch", "Canterbury", "New Zealand", 10, 10));
        selfActivityType = new ActivityType("Eating");
        selfProfile.setActivityTypes(Arrays.asList(selfActivityType));
        otherProfile = new Profile(new User(2L), "John", "Smith", LocalDate.now(), Gender.NON_BINARY);
    }

    @Test
    void findRecommendedActivities_NoneFound(){
        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(List.of());

        assertEquals(0, homeFeedController.findRecommendedActivities(selfProfile).size());
    }

    @Test
    void findRecommendedActivitiesDifferentActivityTypes_NoneFound(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(new ActivityType("Running"));

        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));

        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        assertEquals(0, homeFeedController.findRecommendedActivities(selfProfile).size());
    }

    @Test
    void findRecommendedActivities_OneFound(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(selfActivityType);

        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));

        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        assertEquals(1, homeFeedController.findRecommendedActivities(selfProfile).size());
    }

    @Test
    void findRecommendedActivitiesDifferentActivityTypes_OneFound(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(new ActivityType("Running"));

        Set<ActivityType> activityTypes2 = new HashSet<>();
        activityTypes2.add(selfActivityType);


        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(new Activity("Activity Name 2", false, "Nowhere", otherProfile, activityTypes2), 9.5f, 9.5f, 0,0,0,0 ));



        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        assertEquals(1, homeFeedController.findRecommendedActivities(selfProfile).size());
    }

    @Test
    void findRecommendedActivitiesSameCreator_NoneFound(){
        Set<ActivityType> activityTypes2 = new HashSet<>();
        activityTypes2.add(selfActivityType);

        List<ActivityPin> returnList = new ArrayList<>();
        Activity tempActivity = new Activity("Activity Name 2", false, "Nowhere", selfProfile, activityTypes2);
        tempActivity.setId(4L);
        returnList.add(new ActivityPin(tempActivity, 9.5f, 9.5f, 0,0,0,0 ));

        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);
        Mockito.when(userActivityRoleReporsitory.getRoleEntryByUserId(1L, 4L)).thenReturn(Optional.of(new UserActivityRole(tempActivity, selfProfile.getUser(), ActivityRole.PARTICIPANT)));

        assertEquals(0, homeFeedController.findRecommendedActivities(selfProfile).size());
    }

    @Test
    void findRecommendedActivitiesExcludingSameCreator_OneFound(){
        Set<ActivityType> activityTypes2 = new HashSet<>();
        activityTypes2.add(selfActivityType);

        List<ActivityPin> returnList = new ArrayList<>();
        Activity tempActivity = new Activity("Activity Name 2", false, "Nowhere", selfProfile, activityTypes2);
        tempActivity.setId(4L);
        returnList.add(new ActivityPin(tempActivity, 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(new Activity("Activity Name 2", false, "Nowhere", otherProfile, activityTypes2), 9.5f, 9.5f, 0,0,0,0 ));

        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);
        Mockito.when(userActivityRoleReporsitory.getRoleEntryByUserId(1L, 4L)).thenReturn(Optional.of(new UserActivityRole(tempActivity, selfProfile.getUser(), ActivityRole.PARTICIPANT)));

        assertEquals(1, homeFeedController.findRecommendedActivities(selfProfile).size());
    }

    @Test
    void findRecommendedActivitiesSameActivityType_MultipleFound(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(new ActivityType("Running Fast"));

        Set<ActivityType> activityTypes2 = new HashSet<>();
        activityTypes2.add(selfActivityType);

        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes2), 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes2), 9.5f, 9.5f, 0,0,0,0 ));

        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        assertEquals(2, homeFeedController.findRecommendedActivities(selfProfile).size());
    }

    @Test
    void findRecommendedActivitiesStartInPast_NoneFound(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(selfActivityType);

        Activity tempActivity = new Activity("Activity Name", true, "Nowhere", otherProfile, activityTypes1);
        tempActivity.setStartTime("1990-11-01T12:11:28+1200");

        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(tempActivity, 9.5f, 9.5f, 0,0,0,0 ));

        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        assertEquals(0, homeFeedController.findRecommendedActivities(selfProfile).size());
    }

    @Test
    void findRecommendedActivitiesOneInPast_OneFound(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(selfActivityType);

        Activity tempActivity = new Activity("Activity Name", true, "Nowhere", otherProfile, activityTypes1);
        tempActivity.setStartTime("1990-11-01T12:11:28+1200");

        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(tempActivity, 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(new Activity("Activity Name 2", false, "Nowhere", otherProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(new Activity("Activity Name 2", false, "Nowhere", selfProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));


        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        assertEquals(1, homeFeedController.findRecommendedActivities(selfProfile).size());
    }
}
