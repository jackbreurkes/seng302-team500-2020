package com.springvuegradle.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.responses.HomeFeedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {HomeFeedController.class})
@WebMvcTest
public class HomeFeedControllerTest {

    @InjectMocks
    private HomeFeedController homeFeedController;

    @MockBean
    private ActivityPinRepository activityPinRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    ChangeLogRepository changeLogRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ActivityRepository activityRepository;

    @MockBean
    ProfileRepository profileRepository;

    @MockBean
    SubscriptionRepository subscriptionRepository;

    @MockBean
    UserActivityRoleRepository userActivityRoleRepository;

    User user;
    Profile profile;
    List<ChangeLog> changeLogList;
    String homeFeedJson;

    @BeforeEach
    void beforeEach() {
        homeFeedController = new HomeFeedController();

        user = new User(1);
        Mockito.when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        profile = new Profile(user, "Bob","Builder", LocalDate.of(2000, 10, 15), Gender.MALE);
        Mockito.when(profileRepository.findById(user.getUserId())).thenReturn(Optional.of(profile));

        List<String[]> changeValues = new ArrayList<>();
        changeValues.add(new String[]{"Value 0", "Value 1"});
        changeValues.add(new String[]{"Value 1", "Value 2"});
        changeValues.add(new String[]{"Value 2", "Value 3"});

        Activity swimming = new Activity("Swimming", false, "Location", profile, new HashSet<>());
        swimming.setId(1l);
        Activity cycling = new Activity("Cycling", false, "Location", profile, new HashSet<>());
        cycling.setId(2l);
        Mockito.when(activityRepository.findById(swimming.getId())).thenReturn(Optional.of(swimming));
        Mockito.when(activityRepository.findById(cycling.getId())).thenReturn(Optional.of(cycling));

        changeLogList = new ArrayList<>();
        homeFeedJson = "[";
        long currentChangeLogId = 1L;
        for (String[] values : changeValues) {
            ChangeLog change = new ChangeLog(ChangeLogEntity.ACTIVITY, swimming.getId(), ChangedAttribute.ACTIVITY_DESCRIPTION,
                    profile.getUser(), ActionType.UPDATED, values[0], values[1]);
            change.setTimestamp(OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC")));
            change.setChangeId(currentChangeLogId);
            changeLogList.add(change);
            homeFeedJson += getChangeLogResponseJson(change, swimming) + ",";
            currentChangeLogId += 1;
        }
        // Add change of timeframe
        ChangeLog cyclingChangeLog1 = new ChangeLog(ChangeLogEntity.ACTIVITY, cycling.getId(), ChangedAttribute.ACTIVITY_TIME_FRAME,
                profile.getUser(), ActionType.CREATED,
                null,
                "{\"start_time\":\"2021-08-02T10:06:00+1200\",\"end_time\":\"2021-08-03T10:06:00+1200\"}");
        cyclingChangeLog1.setChangeId(currentChangeLogId);
        currentChangeLogId += 1;
        changeLogList.add(cyclingChangeLog1);
        cyclingChangeLog1.setTimestamp(OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC")));
        homeFeedJson += getChangeLogResponseJson(cyclingChangeLog1, cycling) + ",";
        // Add change of activity types
        ChangeLog cyclingChangeLog2 = new ChangeLog(ChangeLogEntity.ACTIVITY, cycling.getId(), ChangedAttribute.ACTIVITY_ACTIVITY_TYPES,
                profile.getUser(), ActionType.UPDATED,
                "[\"Swimming\"]",
                "[\"Swimming\",\"Cycling\"]");
        cyclingChangeLog2.setChangeId(currentChangeLogId);
        currentChangeLogId += 1;
        changeLogList.add(cyclingChangeLog2);
        cyclingChangeLog2.setTimestamp(OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC")));
        homeFeedJson += getChangeLogResponseJson(cyclingChangeLog2, cycling) + "]";

        Mockito.when(changeLogRepository.retrieveUserHomeFeedUpdates(Mockito.eq(profile), Mockito.any(Pageable.class))).thenReturn(changeLogList);
    }

    private String getChangeLogResponseJson(ChangeLog changeLog, Activity activity) {
        String oldValue = changeLog.getOldValue();
        String newValue = changeLog.getNewValue();
        if (changeLog.getChangedAttribute() != ChangedAttribute.ACTIVITY_ACTIVITY_TYPES && changeLog.getChangedAttribute() != ChangedAttribute.ACTIVITY_TIME_FRAME) {
            oldValue = "\"" + oldValue + "\"";
            newValue = "\"" + newValue + "\"";
        }
        return "{" +
                "\"change_id\":" + changeLog.getChangeId() + "," +                 // Format taken from api spec for GET /homefeed/profileId on wiki
                "\"entity_type\":\"ACTIVITY\"," +
                "\"entity_id\":" + activity.getId() + "," +
                "\"entity_name\":\"" + activity.getActivityName() + "\"," +
                "\"creator_id\":" + activity.getCreator().getUser().getUserId() + "," +
                "\"creator_name\":\"" + activity.getCreator().getFullName(false) + "\"," +
                "\"editor_id\":" + activity.getCreator().getUser().getUserId() + "," +
                "\"editor_name\":\"" + activity.getCreator().getFullName(false) + "\"," +
                "\"edited_timestamp\":\"" + changeLog.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")) + "\"," +
                "\"changed_attribute\":\"" + changeLog.getChangedAttribute().toString().toUpperCase() + "\"," +
                "\"action_type\":\"" + changeLog.getActionType().toString().toUpperCase() + "\"," +
                "\"old_value\":" + oldValue + "," +
                "\"new_value\":" + newValue + "" +
                "}";
    }

    @Test
    public void testGetValidUserHomeFeed() throws Exception {

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId())
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(homeFeedJson, result.getResponse().getContentAsString());
        String responseJson = result.getResponse().getContentAsString();
        List<HomeFeedResponse> responses = objectMapper.readValue(responseJson, new TypeReference<List<HomeFeedResponse>>() {});
        assertEquals(changeLogList.size(), responses.size());
        for (HomeFeedResponse response : responses) {
            assertEquals(profile.getUser().getUserId(), response.getEditorId());
        }
    }

    @Test
    public void testGetEmptyUserHomeFeed() throws Exception {

        User freshUser = new User(2);       // Create new user not subscribed to anything
        Mockito.when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        Profile freshProfile = new Profile(freshUser, "Billy-Does-Nothing","Bill", LocalDate.of(2000, 10, 15), Gender.MALE);
        Mockito.when(profileRepository.findById(freshUser.getUserId())).thenReturn(Optional.of(freshProfile));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+freshUser.getUserId())
                .requestAttr("authenticatedid", freshUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    public void testGetHomeFeedWithNoAuthentication() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetHomeFeedOfNonExistentUser() throws Exception {

        Long fakeUserId = 99l;
        Mockito.when(userRepository.findById(fakeUserId)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+fakeUserId)
                .requestAttr("authenticatedid", fakeUserId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetHomeFeedOfOtherUser() throws Exception {

        User otherUser = new User(3);
        Mockito.when(userRepository.findById(otherUser.getUserId())).thenReturn(Optional.of(otherUser));

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+otherUser.getUserId())
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetChangeLog_DeletedActivity_FiltersAllExceptDelete() throws Exception {
        List<ChangeLog> testChanges = new ArrayList<>();
        long deletedActivityId = 5L;
        Activity deletedActivity = new Activity("Test Activity", false, "Test location", profile, Set.of(Mockito.mock(ActivityType.class)));
        deletedActivity.setId(deletedActivityId);
        ChangeLog deleteLog = ActivityChangeLog.getLogForDeleteActivity(deletedActivity, user);
        deleteLog.setTimestamp(OffsetDateTime.now());
        testChanges.add(deleteLog);
        for (int i = 0; i < 5; i++) {
            ChangeLog mockChange = Mockito.mock(ChangeLog.class);
            Mockito.when(mockChange.getEntity()).thenReturn(ChangeLogEntity.ACTIVITY);
            Mockito.when(mockChange.getEntityId()).thenReturn(deletedActivityId);
            Mockito.when(mockChange.getEditingUser()).thenReturn(user);
            testChanges.add(mockChange);
        }
        Mockito.when(changeLogRepository.retrieveUserHomeFeedUpdates(Mockito.eq(profile), Mockito.any(Pageable.class))).thenReturn(testChanges);

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId())
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entity_type").value("ACTIVITY"))
                .andExpect(jsonPath("$[0].entity_id").value("5"))
                .andExpect(jsonPath("$[0].action_type").value("DELETED"))
                .andExpect(jsonPath("$[0].entity_name").value(deletedActivity.getActivityName()))
                .andExpect(jsonPath("$[0].creator_id").doesNotExist())
                .andExpect(jsonPath("$[0].creator_name").doesNotExist());
    }

    @Test
    public void testGetChangeLog_DeletedEditor_Succeeds() throws Exception {
        List<ChangeLog> testChanges = new ArrayList<>();

        long deletedUserId = 5L;
        Profile deletedProfile = Mockito.mock(Profile.class);
        Mockito.when(deletedProfile.getUser()).thenReturn(new User(deletedUserId));
        Activity activity = new Activity("Test Activity", false, "Test location", profile, Set.of(Mockito.mock(ActivityType.class)));
        activity.setId(1L);
        ChangeLog deleteLog = ActivityChangeLog.getLogForDeleteActivity(activity, deletedProfile.getUser());
        deleteLog.setEditingUser(null);
        deleteLog.setTimestamp(OffsetDateTime.now());
        testChanges.add(deleteLog);

        Mockito.when(changeLogRepository.retrieveUserHomeFeedUpdates(Mockito.eq(profile), Mockito.any(Pageable.class))).thenReturn(testChanges);
        Mockito.when(userRepository.findById(deletedProfile.getUser().getUserId())).thenReturn(Optional.empty());
        Mockito.when(profileRepository.findById(deletedProfile.getUser().getUserId())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId())
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].editor_id").doesNotExist())
                .andExpect(jsonPath("$[0].editor_name").value("<deleted user>"));
    }


    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    public void testGetHomeFeedAfterId(int changeLogListIndex) throws Exception {
        long testChangeId = changeLogList.get(changeLogListIndex).getChangeId();
        Mockito.when(changeLogRepository.findById(testChangeId)).thenReturn(Optional.of(changeLogList.get(changeLogListIndex)));
        Mockito.when(changeLogRepository.retrieveUserHomeFeedUpdatesUpToTime(Mockito.eq(profile),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(changeLogList);

        int expectedSize = changeLogList.subList(changeLogListIndex + 1, changeLogList.size()).size();
        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId())
                .param("lastId", Long.toString(testChangeId))
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedSize)));
    }

    @Test
    public void testGetPaginatedHomeFeed_BadChangeId_400() throws Exception {
        String badExampleChangeId = "not a number";

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId())
                .param("lastId", badExampleChangeId)
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    Exception thrown = result.getResolvedException();
                    assertTrue(thrown instanceof InvalidRequestFieldException);
                    assertEquals(badExampleChangeId + " is not a valid changelog id", thrown.getMessage());
                });
    }

    @Test
    public void testGetHomeFeed_ActivityWithOutcomes_Succeeds() throws Exception {
        ActivityOutcome newOutcome = new ActivityOutcome("test description", "km/h");
        ChangeLog createOutcomeChangeLog = ActivityChangeLog.getLogForCreateOutcome(1L, newOutcome, user);
        createOutcomeChangeLog.setTimestamp(OffsetDateTime.MIN);
        Mockito.when(changeLogRepository.retrieveUserHomeFeedUpdates(Mockito.eq(profile), Mockito.any(Pageable.class)))
                .thenReturn(Collections.singletonList(createOutcomeChangeLog));

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId())
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].changed_attribute").value("ACTIVITY_OUTCOME"))
                .andExpect(jsonPath("$[0].old_value").doesNotExist())
                .andExpect(jsonPath("$[0].new_value").exists());

    }

    // ======================== GET Recommendations tests ==============================

    private String createActivityResponseString(Activity activity) {
        // All 0 and null values in this string are the default for recommended activity home feed responses
        return "{" +
                "\"change_id\":0," +
                "\"entity_type\":null," +
                "\"entity_id\":" + activity.getId() + "," +
                "\"entity_name\":\"" + activity.getActivityName() + "\"," +
                "\"creator_id\":" + activity.getCreator().getUser().getUserId() + "," +
                "\"creator_name\":\"" + activity.getCreator().getFullName(false) + "\"," +
                "\"editor_id\":null," +
                "\"editor_name\":null," +
                "\"edited_timestamp\":null," +
                "\"changed_attribute\":\"RECOMMENDED_ACTIVITY\"," +
                "\"action_type\":null," +
                "\"old_value\":null," +
                "\"new_value\":null" +
                "},";
    }

    @Test
    public void testGetValidUserActivityRecommendations() throws Exception {

        User creatorUser = new User();
        Profile creatorProfile = new Profile(user, "Sally","Smith", LocalDate.of(1980, 10, 15), Gender.FEMALE);;

        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Running"));
        profile.setActivityTypes(new ArrayList<ActivityType>(activityTypes));

        Location location = new Location("Kaikoura", "Canterbury", "New Zealand", -42.24f,173.61f);
        profile.setLocation(location);

        List<Activity> recommendedActivities = new ArrayList<>();
        List<ActivityPin> activityPins = new ArrayList<>();
        String expectedRecommendationsString = "[";

        for (int i = 0; i < 5; i++) {   // Make 5 activities in the same location
            Activity activity = new Activity("Activity "+i, false, location.getCity(), creatorProfile, activityTypes);
            // Make the activity's location near where the user is (the filter requires the activity to be within 0.2 degrees in both lat and lng)
            ActivityPin activityPin = new ActivityPin(activity, location.getLatitude()-0.1f, location.getLongitude()-0.1f,
                    location.getLatitude()-10, location.getLatitude()+10,
                    location.getLongitude()-10, location.getLongitude()+10);
            activity.setActivityPin(activityPin);

            recommendedActivities.add(activity);
            activityPins.add(activityPin);

            // Mock the repository queries for this activity
            Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(user.getUserId(), activity.getId())).thenReturn(Optional.empty());
            Mockito.when(subscriptionRepository.isSubscribedToActivity(activity.getId(), profile)).thenReturn(false);
            Mockito.when(subscriptionRepository.getFollowerCount(activity.getId())).thenReturn(20l);

            String activityResponseString = createActivityResponseString(activity);
            expectedRecommendationsString += activityResponseString;
        }
        expectedRecommendationsString = expectedRecommendationsString.substring(0, expectedRecommendationsString.length()-1) + "]";

        Mockito.when(userRepository.getOne(creatorUser.getUserId())).thenReturn(creatorUser);
        Mockito.when(userRepository.getOne(user.getUserId())).thenReturn(user);
        Mockito.when(profileRepository.getOne(creatorProfile.getUser().getUserId())).thenReturn(creatorProfile);
        Mockito.when(profileRepository.getOne(profile.getUser().getUserId())).thenReturn(profile);
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.any(Float.class), Mockito.any(Float.class),
                Mockito.any(Float.class), Mockito.any(Float.class), Mockito.any(Pageable.class))).thenReturn(activityPins);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId()+"/recommendations")
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expectedRecommendationsString, result.getResponse().getContentAsString());
        String responseJson = result.getResponse().getContentAsString();
        List<HomeFeedResponse> recommendationResponses = objectMapper.readValue(responseJson, new TypeReference<List<HomeFeedResponse>>() {});
        assertEquals(recommendedActivities.size(), recommendationResponses.size());
    }

    @Test
    public void testGetNoOwnActivityRecommendations() throws Exception {

        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Running"));
        profile.setActivityTypes(new ArrayList<ActivityType>(activityTypes));

        Location location = new Location("Kaikoura", "Canterbury", "New Zealand", -42.24f,173.61f);
        profile.setLocation(location);

        List<Activity> recommendedActivities = new ArrayList<>();
        List<ActivityPin> activityPins = new ArrayList<>();

        for (int i = 0; i < 5; i++) {   // Make 5 activities in the same location
            Activity activity = new Activity("Activity "+i, false, location.getCity(), profile, activityTypes);
            // Make the activity's location near where the user is (the filter requires the activity to be within 0.2 degrees in both lat and lng)
            ActivityPin activityPin = new ActivityPin(activity, location.getLatitude()-0.1f, location.getLongitude()-0.1f,
                    location.getLatitude()-10, location.getLatitude()+10,
                    location.getLongitude()-10, location.getLongitude()+10);
            activity.setActivityPin(activityPin);

            recommendedActivities.add(activity);
            activityPins.add(activityPin);

            // Mock the repository queries for this activity
            Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(user.getUserId(), activity.getId())).thenReturn(Optional.empty());
            Mockito.when(subscriptionRepository.isSubscribedToActivity(activity.getId(), profile)).thenReturn(false);
            Mockito.when(subscriptionRepository.getFollowerCount(activity.getId())).thenReturn(20l);
        }

        Mockito.when(userRepository.getOne(user.getUserId())).thenReturn(user);
        Mockito.when(profileRepository.getOne(profile.getUser().getUserId())).thenReturn(profile);
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.any(Float.class), Mockito.any(Float.class),
                Mockito.any(Float.class), Mockito.any(Float.class), Mockito.any(Pageable.class))).thenReturn(activityPins);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId()+"/recommendations")
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    public void testGetActivityRecommendationsWithNoInterestsInCommon() throws Exception {

        profile.setActivityTypes(new ArrayList<ActivityType>());    // Make user have no interests

        Location location = new Location("Kaikoura", "Canterbury", "New Zealand", -42.24f,173.61f);
        profile.setLocation(location);

        List<Activity> activities = new ArrayList<>();
        List<ActivityPin> activityPins = new ArrayList<>();
        String expectedRecommendationsString = "[]";

        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Running"));

        for (int i = 0; i < 5; i++) {   // Make 5 activities in the same location
            Activity activity = new Activity("Activity "+i, false, location.getCity(), profile, activityTypes);
            // Make the activity's location near where the user is (the filter requires the activity to be within 0.2 degrees in both lat and lng)
            ActivityPin activityPin = new ActivityPin(activity, location.getLatitude()-0.1f, location.getLongitude()-0.1f,
                    location.getLatitude()-10, location.getLatitude()+10,
                    location.getLongitude()-10, location.getLongitude()+10);
            activity.setActivityPin(activityPin);

            activities.add(activity);
            activityPins.add(activityPin);

            // Mock the repository queries for this activity
            Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(user.getUserId(), activity.getId())).thenReturn(Optional.empty());
            Mockito.when(subscriptionRepository.isSubscribedToActivity(activity.getId(), profile)).thenReturn(false);
            Mockito.when(subscriptionRepository.getFollowerCount(activity.getId())).thenReturn(20l);
        }

        Mockito.when(userRepository.getOne(user.getUserId())).thenReturn(user);
        Mockito.when(profileRepository.getOne(profile.getUser().getUserId())).thenReturn(profile);
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.any(Float.class), Mockito.any(Float.class),
                Mockito.any(Float.class), Mockito.any(Float.class), Mockito.any(Pageable.class))).thenReturn(activityPins);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId()+"/recommendations")
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expectedRecommendationsString, result.getResponse().getContentAsString());
        String responseJson = result.getResponse().getContentAsString();
        List<HomeFeedResponse> recommendationResponses = objectMapper.readValue(responseJson, new TypeReference<List<HomeFeedResponse>>() {});
        assertEquals(0, recommendationResponses.size());
    }

    @Test
    public void testGetActivityRecommendationsWithNoActivitiesWithinBounds() throws Exception {

        Location location = new Location("Kaikoura", "Canterbury", "New Zealand", -42.24f,173.61f);
        profile.setLocation(location);

        String expectedRecommendationsString = "[]";

        Mockito.when(userRepository.getOne(user.getUserId())).thenReturn(user);
        Mockito.when(profileRepository.getOne(profile.getUser().getUserId())).thenReturn(profile);
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.any(Float.class), Mockito.any(Float.class),
                Mockito.any(Float.class), Mockito.any(Float.class), Mockito.any(Pageable.class))).thenReturn(new ArrayList<>());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId()+"/recommendations")
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expectedRecommendationsString, result.getResponse().getContentAsString());
        String responseJson = result.getResponse().getContentAsString();
        List<HomeFeedResponse> recommendationResponses = objectMapper.readValue(responseJson, new TypeReference<>() {});
        assertEquals(0, recommendationResponses.size());
    }

    @Test
    public void testGetActivityRecommendationsWithNoAuthentication() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId()+"/recommendations")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetActivityRecommendationsOfNonExistentUser() throws Exception {

        Long fakeUserId = 99l;
        Mockito.when(userRepository.findById(fakeUserId)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+fakeUserId+"/recommendations")
                .requestAttr("authenticatedid", fakeUserId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetActivityRecommendationsOfOtherUser() throws Exception {

        User otherUser = new User(3);
        Mockito.when(userRepository.findById(otherUser.getUserId())).thenReturn(Optional.of(otherUser));

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+otherUser.getUserId()+"/recommendations")
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
