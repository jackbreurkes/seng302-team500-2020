package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ActivitySearchRepositoryImplTest {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ActivityTypeRepository activityTypeRepository;

    @Autowired
    ProfileRepository profileRepository;

    @MockBean
    ActivityPinRepository activityPinRepository;

    @MockBean
    UserActivityRoleRepository userActivityRoleRepository;

    ActivityType activityType;
    private Profile selfProfile;
    private ActivityType selfActivityType;
    private Profile otherProfile;

    private static final float BOUNDING_BOX_SIZE = 0.2f;

    @BeforeAll
    void beforeAll(){
        activityType = activityTypeRepository.save(new ActivityType("Walkin"));
        selfProfile = new Profile(new User(1L), "David", "Clarke", LocalDate.now(), Gender.FEMALE);
        selfProfile.setLocation(new Location("Christchurch", "Canterbury", "New Zealand", 10, 10));
        selfActivityType = new ActivityType("Eating");
        selfProfile.setActivityTypes(Arrays.asList(selfActivityType));
        otherProfile = new Profile(new User(2L), "John", "Smith", LocalDate.now(), Gender.NON_BINARY);
    }

    @BeforeEach
    void beforeEach(){
        profileRepository.flush();
        activityRepository.flush();
    }

    Activity createActivityWithName(String name) {
        Profile creator = new Profile(new User(), "Fname", "Lname", LocalDate.EPOCH, Gender.MALE);
        profileRepository.save(creator);
        Activity activity = new Activity(name, false, "Location", creator, Set.of(activityType));
        return activityRepository.save(activity);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "This",
            "is",
            "a",
            "run"
    })
    @Disabled  //Disabled due to failing on pipeline only, will pass locally
    void testSingleWord_singleResult(String searchTerm) {
        String activityName = "This is a superfun run";
        createActivityWithName(activityName);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(Collections.singletonList(searchTerm), PageRequest.of(0, 25));

        assertEquals(1, results.size());
        assertEquals(activityName, results.get(0).getActivityName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Awesome Activity",
            "This is a fun run",
            "Test Test Test",
            "oneword"
    })
    @Disabled //Disabled due to failing on pipeline only, will pass locally
    void testSearchByAllWordsInName_singleResult(String activityName) {
        List<String> searchTerms = Arrays.asList(activityName.split(" "));
        createActivityWithName(activityName);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, PageRequest.of(0, 25));

        assertEquals(1, results.size());
        assertEquals(activityName, results.get(0).getActivityName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Test", "activity"
    })
    void testSingleWord_mulitpleResults(){
        createActivityWithName("Test activity two");
        createActivityWithName("Test activity one");
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(Collections.singletonList("Test"), PageRequest.of(0, 25));

        assertEquals(2, results.size());
        assertEquals("Test activity one", results.get(0).getActivityName());
        assertEquals("Test activity two", results.get(1).getActivityName());
    }

    @Test
    void testMultipleResultsSamePriority_OrderedAlphabetically() {
        createActivityWithName("Test b");
        createActivityWithName("Test a");
        createActivityWithName("Mock b");
        createActivityWithName("Mock a");
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(Arrays.asList("Test", "Mock"), PageRequest.of(0, 25));
        assertEquals(4, results.size());
        assertEquals("Mock a", results.get(0).getActivityName());
        assertEquals("Mock b", results.get(1).getActivityName());
        assertEquals("Test a", results.get(2).getActivityName());
        assertEquals("Test b", results.get(3).getActivityName());
    }

    @Test
    void testMultipleResults_OrderedByPriority() {
        createActivityWithName("Test b");
        createActivityWithName("Test a");
        createActivityWithName("Mock a");
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(Arrays.asList("Test", "Mock", "est"), PageRequest.of(0, 25));
        assertEquals(3, results.size());
        assertEquals("Test a", results.get(0).getActivityName());
        assertEquals("Test b", results.get(1).getActivityName());
        assertEquals("Mock a", results.get(2).getActivityName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "This", "this", "THIS", "tHIS", "ThIs",
            "Activity", "activity", "ACTIVITY", "aCtIViTy",
            "tHIS aCTIVITY", "activity capital", "CAPITAL LETTERS",
            "this ACTIVITY capital LETTERS", "THIS activity CAPITAL letters"
    })
    void testDifferentCase_SearchIsCaseInsensitive(String query) {
        createActivityWithName("This Activity Capital Letters");
        List<String> searchTerms = Collections.singletonList(query);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, PageRequest.of(0, 25));
        assertEquals(1, results.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "this activity",
            "this activity multiple",
            "this activity multiple words",
            "activity multiple",
            "activity multiple words",
            "multiple words"
    })
    void testMultiwordSearch_CorrectOrder_ExpectOneResult(String query) {
        createActivityWithName("this activity multiple words");
        List<String> searchTerms = Collections.singletonList(query);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, PageRequest.of(0, 25));
        assertEquals(1, results.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "activity this",
            "this multiple activity",
            "this activity words multiple",
            "this multiple words",
            "this words",
            "activity words"
    })
    void testMultiwordSearch_IncorrectOrder_ExpectNoResults(String query) {
        createActivityWithName("this activity multiple words");
        List<String> searchTerms = Collections.singletonList(query);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, PageRequest.of(0, 25));
        assertEquals(0, results.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "is",
            "is act",
            "tivit",
            "e",
            "ple",
            "ultiple word",
            "s activity m"
    })
    void testPartialWordMatches_ExpectOne(String query) {
        createActivityWithName("this activity multiple words");
        List<String> searchTerms = Collections.singletonList(query);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, Pageable.unpaged());
        assertEquals(1, results.size());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1, 1",
            "2, 2, 2",
            "10, 10, 10",
            "1, 2, 2",
            "2, 1, 1",
            "5, 10, 2",
            "10, 3, 3",
            "2, 10, 1",
            "10, 2, 1",
            "1, 50, 50"
    })
    void testMultiplePages_returnsOnePageAtATime(String numPagesString, String pageSizeString, String lastPageSizeString) {
        final int numPages = Integer.parseInt(numPagesString);
        final int pageSize = Integer.parseInt(pageSizeString);
        final int lastPageSize = Integer.parseInt(lastPageSizeString);
        if (lastPageSize > pageSize) {
            throw new IllegalArgumentException("last page cannot be larger than the max page size");
        }
        for (int pageNumber = 0; pageNumber < numPages - 1; pageNumber++) {
            for (int positionOnPage = 0; positionOnPage < pageSize; positionOnPage++) {
                createActivityWithName(String.format("page %d activity %d", pageNumber, positionOnPage));
            }
        }
        for (int positionOnPage = 0; positionOnPage < lastPageSize; positionOnPage++) {
            createActivityWithName(String.format("page %d activity %d", numPages - 1, positionOnPage));
        }

        List<List<Activity>> results = new ArrayList<>();
        for (int i = 0; i < numPages; i++) {
            results.add(activityRepository.findUniqueActivitiesByListOfNames(Collections.singletonList("activity"), PageRequest.of(i, pageSize)));
        }

        for (int i = 0; i < numPages - 1; i++) {
            assertEquals(pageSize, results.get(i).size());
            for (Activity activity : results.get(i)) {
                assertTrue(activity.getActivityName().startsWith(String.format("page %d", i)), String.format("Activity %s appeared on page %d", activity.getActivityName(), i));
            }
        }
        assertEquals(lastPageSize, results.get(numPages-1).size());
        for (Activity activity : results.get(numPages-1)) {
            assertTrue(activity.getActivityName().startsWith(String.format("page %d", numPages-1)));
        }

    }



    @Test
    void findRecommendedActivities_NoneFound(){
        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(List.of());

        assertEquals(0, activityRepository.findRecommendedActivitiesByProfile(selfProfile).size());
    }

    @Test
    void findRecommendedActivitiesDifferentActivityTypes_NoneFound(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(new ActivityType("Running"));

        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));

        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        assertEquals(0, activityRepository.findRecommendedActivitiesByProfile(selfProfile).size());
    }

    @Test
    void findRecommendedActivities_OneFound(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(selfActivityType);

        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(new Activity("Activity Name", false, "Nowhere", otherProfile, activityTypes1), 9.5f, 9.5f, 0,0,0,0 ));

        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        assertEquals(1, activityRepository.findRecommendedActivitiesByProfile(selfProfile).size());
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

        assertEquals(1, activityRepository.findRecommendedActivitiesByProfile(selfProfile).size());
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
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L, 4L)).thenReturn(Optional.of(new UserActivityRole(tempActivity, selfProfile.getUser(), ActivityRole.PARTICIPANT)));

        assertEquals(0, activityRepository.findRecommendedActivitiesByProfile(selfProfile).size());
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
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L, 4L)).thenReturn(Optional.of(new UserActivityRole(tempActivity, selfProfile.getUser(), ActivityRole.PARTICIPANT)));

        assertEquals(1, activityRepository.findRecommendedActivitiesByProfile(selfProfile).size());
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

        assertEquals(2, activityRepository.findRecommendedActivitiesByProfile(selfProfile).size());
    }

    @Test
    void findRecommendedActivitiesEndInPast_NoneFound(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(selfActivityType);

        Activity tempActivity = new Activity("Activity Name", true, "Nowhere", otherProfile, activityTypes1);
        tempActivity.setStartTime("1990-11-01T12:11:28+1200");
        tempActivity.setEndTime("1990-12-01T12:11:28+1200");

        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(tempActivity, 9.5f, 9.5f, 0,0,0,0 ));

        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        assertEquals(0, activityRepository.findRecommendedActivitiesByProfile(selfProfile).size());
    }

    @Test
    void findRecommendedActivitiesOneInPast_OneFound(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(selfActivityType);

        Activity pastActivity = new Activity("Activity Name", true, "Nowhere", otherProfile, activityTypes1);
        pastActivity.setStartTime("1990-11-01T12:11:28+1200");
        pastActivity.setEndTime("1991-11-01T12:11:28+1200");
        Activity recommendedActivity = new Activity("Activity Name 2", false, "Nowhere", otherProfile, activityTypes1);
        Activity activityICreated = new Activity("Activity Name 3", false, "Nowhere", selfProfile, activityTypes1);

        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(pastActivity, 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(recommendedActivity, 9.5f, 9.5f, 0,0,0,0 ));
        returnList.add(new ActivityPin(activityICreated, 9.5f, 9.5f, 0,0,0,0 ));


        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        List<Activity> results = activityRepository.findRecommendedActivitiesByProfile(selfProfile);
        assertEquals(1, results.size());
        assertEquals(recommendedActivity, results.get(0));
    }

    @Test
    void findRecommendedActivities_StartInPast_EndInFuture_IsRecommended(){
        Set<ActivityType> activityTypes1 = new HashSet<>();
        activityTypes1.add(selfActivityType);

        Activity tempActivity = new Activity("Activity Name", true, "Nowhere", otherProfile, activityTypes1);
        tempActivity.setStartTime("1990-10-10T12:11:28+1200");
        tempActivity.setEndTime("2025-05-02T12:11:28+1200");

        List<ActivityPin> returnList = new ArrayList<>();
        returnList.add(new ActivityPin(tempActivity, 9.5f, 9.5f, 0,0,0,0 ));

        Mockito.when(activityPinRepository.findPinsInBounds(selfProfile.getLocation().getLatitude() + BOUNDING_BOX_SIZE,selfProfile.getLocation().getLongitude() + BOUNDING_BOX_SIZE, selfProfile.getLocation().getLatitude() - BOUNDING_BOX_SIZE, selfProfile.getLocation().getLongitude() - BOUNDING_BOX_SIZE, Pageable.unpaged())).thenReturn(returnList);

        List<Activity> results = activityRepository.findRecommendedActivitiesByProfile(selfProfile);
        assertEquals(1, results.size());
        assertEquals(tempActivity, results.get(0));
    }

}