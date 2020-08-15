package com.springvuegradle.endpoints;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.*;

import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.rest.core.ValidationErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.requests.CreateActivityRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.Errors;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ActivitiesController.class})
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PutActivityTest {

    @InjectMocks
    private ActivitiesController activitiesController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ActivityRepository activityRepository;
    @MockBean
    private ActivityTypeRepository activityTypeRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SubscriptionRepository subscriptionRepository;
    @MockBean
    private UserActivityRoleRepository userActivityRoleRepository;
    @MockBean
    private ProfileRepository profileRepository;
    @MockBean
    private ChangeLogRepository changeLogRepository;
    @MockBean
    private ActivityOutcomeRepository activityOutcomeRepository;


    @MockBean
	private ActivityParticipantResultRepository activityOutcomeRepo;

    private User user;
    private Profile profile;
    private Set<ActivityType> testActivityTypes;

    @BeforeAll
    void beforeAll(){
        activitiesController = new ActivitiesController();
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void beforeEach(){
        user = new User(1L);
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        profile = new Profile(user, "Bob","Builder", LocalDate.of(2000, 10, 15), Gender.MALE);
        Mockito.when(profileRepository.findById(user.getUserId())).thenReturn(Optional.of(profile));

        testActivityTypes = new HashSet<>();
        for (String activityType : new String[] {"Running", "Swimming"}) {
            ActivityType testActivityType = new ActivityType(activityType);
            testActivityTypes.add(testActivityType);
            Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(activityType)).thenReturn(Optional.of(testActivityType));
        }
    }

    /**
     * helper method to handle the basic put operation for activity put tests
     * @param json the activity json
     * @return a ResultActions object that can be used in further .andExpect() or other chaining
     */
    ResultActions putActivityJson(String json, long userId, long activityId) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + userId + "/activities/" + activityId)
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1", "12", "this one is over thirty characs"
    })
    void testInvalidActivityName_BadLength_400(String name) throws Exception {
        long activityId = 10;
        Mockito.when(activityRepository.findById(activityId)).thenReturn(Optional.of(Mockito.mock(Activity.class)));

        String json = "{\n" +
                "  \"activity_name\": \"" + name + "\",\n" +
                "  \"description\": \"I really like testing\",\n" +
                "  \"activity_type\":[ \"Running\" ],\r\n" +
                "  \"continuous\": true,\n" +
                "  \"location\": \"Christchurch, NZ\",\n" +
                "  \"outcomes\": [" +
                "        {\"description\": \"test description\", \"units\": \"test\" }" +
                "    ] " +
                "}";

        putActivityJson(json, user.getUserId(), activityId)
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    Exception thrown = result.getResolvedException();
                    assertTrue(thrown instanceof InvalidRequestFieldException);
                    assertEquals("activity_name must be between 4 and 30 characters inclusive", thrown.getMessage());
                });

        Mockito.verify(activityRepository, never()).save(Mockito.any());
    }

    @Test
    void testActivityMissingLocation_400() throws Exception {
        long activityId = 10;
        Mockito.when(activityRepository.findById(activityId)).thenReturn(Optional.of(Mockito.mock(Activity.class)));

        String json = "{\n" +
                "  \"activity_name\": \"test activity\",\n" +
                "  \"description\": \"this is an activity\",\n" +
                "  \"activity_type\":[ \"Swimming\" ],\r\n" +
                "  \"continuous\": true,\n" +
                "  \"outcomes\": [" +
                "        {\"description\": \"test description\", \"units\": \"test\" }" +
                "    ] " +
                "}";

        putActivityJson(json, user.getUserId(), activityId)
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    Exception thrown = result.getResolvedException();
                    assertTrue(thrown instanceof InvalidRequestFieldException);
                    assertEquals("missing location field", thrown.getMessage());
                });

        Mockito.verify(activityRepository, never()).save(Mockito.any());
    }

    @Test
    void testActivityDoesntExist_404() throws Exception {
        long activityId = 10;
        Mockito.when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

        String json = "{\n" +
                "  \"activity_name\": \"test activity\",\n" +
                "  \"activity_type\":[ \"Swimming\" ],\r\n" +
                "  \"continuous\": true,\n" +
                "  \"location\": \"test location\",\n" +
                "  \"outcomes\": [" +
                "        {\"description\": \"test description\", \"units\": \"test\" }" +
                "    ] " +
                "}";

        putActivityJson(json, user.getUserId(), activityId)
                .andExpect(status().isNotFound())
                .andDo(result -> {
                    Exception thrown = result.getResolvedException();
                    assertTrue(thrown instanceof RecordNotFoundException);
                    assertEquals("Activity does not exist", thrown.getMessage());
                });

        Mockito.verify(activityRepository, never()).save(Mockito.any());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "non-existent", "a", "", "shouldn't matter if the activity type name is valid!"
    })
    void testActivityTypeDoesntExist_404(String activityType) throws Exception {
        long activityId = 10;
        Mockito.when(activityRepository.findById(activityId)).thenReturn(Optional.of(Mockito.mock(Activity.class)));

        String json = "{\n" +
                "  \"activity_name\": \"test activity\",\n" +
                "  \"activity_type\":[ \"" + activityType + "\" ],\r\n" +
                "  \"continuous\": true,\n" +
                "  \"location\": \"test location\",\n" +
                "  \"outcomes\": [" +
                "        {\"description\": \"test description\", \"units\": \"test\" }" +
                "    ] " +
                "}";

        putActivityJson(json, user.getUserId(), activityId)
                .andExpect(status().isNotFound())
                .andDo(result -> {
                    Exception thrown = result.getResolvedException();
                    assertTrue(thrown instanceof RecordNotFoundException);
                    assertEquals("Activity type " + activityType + " does not exist", thrown.getMessage());
                });

        Mockito.verify(activityRepository, never()).save(Mockito.any());
    }

    @Test
    void testUpdatingOtherUsersActivity_403NotAuthorized(){
        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        Errors validationErrors = Mockito.mock(Errors.class);
        Mockito.when(validationErrors.getAllErrors()).thenReturn(new ArrayList<>());

        CreateActivityRequest CreateActivityRequest = createValidUpdateRequest();
        Activity activity = new Activity();
        when(activityRepository.findById(3L)).thenReturn(Optional.of(activity));
        assertThrows(UserNotAuthorizedException.class, () -> {
            activitiesController.putActivity(2L, 3L, CreateActivityRequest, validationErrors, request);
        });
    }

    @Test
    @Disabled
    void testUpdateActivityAsAdmin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        Errors validationErrors = Mockito.mock(Errors.class);
        Mockito.when(validationErrors.getAllErrors()).thenReturn(new ArrayList<>());
        //need to set as admin
        user.setPermissionLevel(126);

        CreateActivityRequest CreateActivityRequest = createValidUpdateRequest();
        Activity activity = new Activity();
        activity.setActivityTypes(new HashSet<>());

        when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        assertEquals(activitiesController.putActivity(3L, 2L, CreateActivityRequest, validationErrors, request), HttpStatus.OK);
    }

    @Test
    @Disabled
    void testUpdateActivity() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        Errors validationErrors = Mockito.mock(Errors.class);
        Mockito.when(validationErrors.getAllErrors()).thenReturn(new ArrayList<>());

        CreateActivityRequest CreateActivityRequest = createValidUpdateRequest();
        Activity activity = mock(Activity.class);
        when(activity.getId()).thenReturn(1L);
        activity.setActivityTypes(new HashSet<>());

        when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        assertEquals(activitiesController.putActivity(1L, 2L, CreateActivityRequest, validationErrors, request), HttpStatus.OK);
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




    @Test
    public void testPutActivityWithOutcomes_DeleteOutcomes_200() throws Exception {
        long activityId = 10;
        Activity testActivity = new Activity("Test Activity", false, "Test Location", profile, testActivityTypes);
        testActivity.setId(activityId);
        ActivityOutcome outcome1 = new ActivityOutcome("first outcome", "km/h");
        ActivityOutcome outcome2 = new ActivityOutcome("second outcome", "m/s");
        testActivity.addOutcome(outcome1);
        testActivity.addOutcome(outcome2);
        Mockito.when(activityRepository.findById(activityId)).thenReturn(Optional.of(testActivity));
        Mockito.when(activityRepository.save(Mockito.any(Activity.class))).thenAnswer(
                invocation -> invocation.getArguments()[0] // returns the object that was saved
        );

        String json = "{\n" +
                "  \"activity_name\": \"SENG302\",\n" +
                "  \"description\": \"I really like testing\",\n" +
                "  \"activity_type\":[ \"Swimming\" ],\r\n" +
                "  \"continuous\": true,\n" +
                "  \"location\": \"Christchurch, NZ\",\n" +
                "  \"outcomes\": [ ] " + // delete the outcomes
                "}";

        putActivityJson(json, user.getUserId(), activityId)
                .andExpect(status().isOk());

        ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
        Mockito.verify(activityRepository).save(activityCaptor.capture());
        assertEquals(1, activityCaptor.getAllValues().size());
        Activity createdActivity = activityCaptor.getValue();
        assertEquals(0, createdActivity.getOutcomes().size());
    }

    @Test
    public void testPutActivityWithOutcomes_AlterOutcomes_200() throws Exception {
        long activityId = 10;
        Activity testActivity = new Activity("Test Activity", false, "Test Location", profile, testActivityTypes);
        testActivity.setId(activityId);
        ActivityOutcome outcome1 = new ActivityOutcome("first outcome", "km/h");
        ActivityOutcome outcome2 = new ActivityOutcome("second outcome", "m/s");
        testActivity.addOutcome(outcome1);
        testActivity.addOutcome(outcome2);
        Mockito.when(activityRepository.findById(activityId)).thenReturn(Optional.of(testActivity));
        Mockito.when(activityRepository.save(Mockito.any())).thenReturn(testActivity);

        String newOutcomeDescription = "a new one!";
        String newOutcomeUnits = "miles";
        String json = "{\n" +
                "  \"activity_name\": \"SENG302\",\n" +
                "  \"description\": \"I really like testing\",\n" +
                "  \"activity_type\":[ \"Swimming\" ],\r\n" +
                "  \"continuous\": true,\n" +
                "  \"location\": \"Christchurch, NZ\",\n" +
                "  \"outcomes\": [" +
                "{\"description\": \"" + newOutcomeDescription + "\", \"units\": \"" + newOutcomeUnits + "\" }," + // new outcome
                "{\"description\": \"" + outcome1.getDescription() + "\", \"units\": \"" + outcome1.getUnits() + "\" }" +
                "] " +
                "}";

        putActivityJson(json, user.getUserId(), activityId)
                .andExpect(status().isOk());

        ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
        Mockito.verify(activityRepository).save(activityCaptor.capture());
        assertEquals(1, activityCaptor.getAllValues().size());
        Activity createdActivity = activityCaptor.getValue();
        assertEquals(2, createdActivity.getOutcomes().size());
        assertEquals(outcome1.getDescription(), createdActivity.getOutcomes().get(0).getDescription()); // already existing outcomes should come first
        assertEquals(outcome1.getUnits(), createdActivity.getOutcomes().get(0).getUnits());
        assertSame(outcome1, createdActivity.getOutcomes().get(0)); // should not create a new instance
        assertEquals(newOutcomeDescription, createdActivity.getOutcomes().get(1).getDescription()); // new outcome appended to the list
        assertEquals(newOutcomeUnits, createdActivity.getOutcomes().get(1).getUnits());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "new outcome description",
            "min",
            "this is the maximum length, ok"
    })
    public void testPutActivityWithOutcomes_OutcomeMissingUnits_400(String newOutcomeDescription) throws Exception {
        long activityId = 10;
        Mockito.when(activityRepository.findById(activityId)).thenReturn(Optional.of(Mockito.mock(Activity.class)));

        String json = "{\n" +
                "  \"activity_name\": \"SENG302\",\n" +
                "  \"description\": \"I really like testing\",\n" +
                "  \"activity_type\":[ \"Swimming\" ],\r\n" +
                "  \"continuous\": true,\n" +
                "  \"location\": \"Christchurch, NZ\",\n" +
                "  \"outcomes\": [" +
                "{\"description\": \"" + newOutcomeDescription + "\" }" +
                "] " +
                "}";

        putActivityJson(json, user.getUserId(), activityId)
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    Exception thrown = result.getResolvedException();
                    assertTrue(thrown instanceof InvalidRequestFieldException);
                    assertEquals("outcome missing units field", thrown.getMessage());
                });

        Mockito.verify(activityRepository, Mockito.never()).save(Mockito.any());
    }

}
