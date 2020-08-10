package com.springvuegradle.endpoints;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.springvuegradle.exceptions.ExceptionHandlerController;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.ChangeLogRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.SubscriptionRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.repository.UserRepository;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ActivitiesController.class})
@WebMvcTest
public class ActivitiesControllerTest {

	@InjectMocks
	ActivitiesController activitiesController;

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ActivityRepository activityRepo;

	@MockBean
	private ActivityTypeRepository activityTypeRepo;

	@MockBean
	private UserRepository userRepo;

	@MockBean
    private SubscriptionRepository subscriptionRepo;

	@MockBean
	private UserActivityRoleRepository userActivityRoleRepository;

	@MockBean
	private ProfileRepository profileRepo;

	@MockBean
	private ChangeLogRepository changeLogRepository;

	User user;
	Profile profile;
	Set<ActivityType> testActivityTypes;

	@BeforeEach
	void beforeEach() {
		activitiesController = new ActivitiesController();
		MockitoAnnotations.initMocks(this);

		user = new User(1);
		Mockito.when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));

		profile = new Profile(user, "Bob","Builder", LocalDate.of(2000, 10, 15), Gender.MALE);
		Mockito.when(profileRepo.findById(user.getUserId())).thenReturn(Optional.of(profile));

		testActivityTypes = new HashSet<>();
		for (String activityType : new String[] {"walking", "coding", "flying"}) {
			ActivityType testActivityType = new ActivityType(activityType);
			testActivityTypes.add(testActivityType);
			Mockito.when(activityTypeRepo.getActivityTypeByActivityTypeName(activityType)).thenReturn(Optional.of(testActivityType));
		}

		Mockito.when(activityRepo.save(Mockito.any())).thenAnswer(new Answer<Activity>() {
			@Override
			public Activity answer(InvocationOnMock invocation) throws Throwable {
				Activity saving = invocation.getArgument(0);
				saving.setId(1);
				return saving;
			}
		});

		Mockito.when(changeLogRepository.save(Mockito.any(ChangeLog.class))).thenReturn(null);

	}


	/**
	 * helper method to handle the basic post operation for activity create tests
	 * @param json the activity json
	 * @return a ResultActions object that can be used in further .andExpect() or other chaining
	 */
	ResultActions postActivityJson(String json) throws Exception {
		return mvc.perform(MockMvcRequestBuilders
				.post("/profiles/"+user.getUserId()+"/activities")
				.contentType(MediaType.APPLICATION_JSON).content(json)
				.requestAttr("authenticatedid", user.getUserId()))
				.andDo(print());
	}

	@Test
	public void testCreateValidActivity() throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \"I need more hours in SENG302 so lets do it together\",\n" +
				"  \"activity_type\":[ \n" +
				"    \"coding\"\n" +
				"  ],\r\n" +
				"  \"continuous\": false,\n" +
				"  \"start_time\": \"2020-02-20T08:00:00+1300\", \n" +
				"  \"end_time\": \"2020-02-20T08:00:00+1300\",\n" +
				"  \"location\": \"Christchurch, NZ\"\n" +
				"}";

		postActivityJson(json)
				.andExpect(status().isCreated());

		ArgumentCaptor<ChangeLog> changeLogCaptor = ArgumentCaptor.forClass(ChangeLog.class);
		Mockito.verify(changeLogRepository).save(changeLogCaptor.capture());
		assertEquals(1, changeLogCaptor.getAllValues().size());
		ChangeLog createLog = changeLogCaptor.getValue();
		assertEquals(ChangedAttribute.ACTIVITY_EXISTENCE, createLog.getChangedAttribute());
		assertEquals(user.getUserId(), createLog.getEditingUser().getUserId());
		assertEquals(ActionType.CREATED, createLog.getActionType());
	}

	@ParameterizedTest
	@CsvSource({
			"Cat", "Dog", "A", "AB", "Fourty characters is the maximum length of the name of an activity in this app"
	})
	public void testCreateActivityBadActivityNameLength(String name) throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \""+name+"\",\n" +
				"  \"description\": \"I need more hours in SENG302 so lets do it together\",\n" +
				"  \"activity_type\":[ \n" +
				"    \"coding\"\n" +
				"  ],\r\n" +
				"  \"continuous\": false,\n" +
				"  \"start_time\": \"2020-02-20T08:00:00+1300\", \n" +
				"  \"end_time\": \"2020-02-20T08:00:00+1300\",\n" +
				"  \"location\": \"Christchurch, NZ\"\n" +
				"}";

		postActivityJson(json)
				.andExpect(status().isBadRequest());

		Mockito.verifyNoInteractions(changeLogRepository);
	}

	@ParameterizedTest
	@CsvSource({
			"A", "BB", "CCC", "DDDD", "JRHJFG", "dfkriej", "jjfkwoi", ","
	})
	public void testCreateActivityBadActivityDescriptionLength(String name) throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \""+name+"\",\n" +
				"  \"activity_type\":[ \n" +
				"    \"coding\"\n" +
				"  ],\r\n" +
				"  \"continuous\": false,\n" +
				"  \"start_time\": \"2020-02-20T08:00:00+1300\", \n" +
				"  \"end_time\": \"2020-02-20T08:00:00+1300\",\n" +
				"  \"location\": \"Christchurch, NZ\"\n" +
				"}";

		postActivityJson(json)
				.andExpect(status().isBadRequest());

		Mockito.verifyNoInteractions(changeLogRepository);
	}

	@ParameterizedTest
	@CsvSource({
			"\"walking\"", "\"walking\", \"coding\"", "\"coding\"", "\"walking\", \"flying\", \"coding\"", "\"coding\", \"walking\", \"flying\""
	})
	public void testCreateActivityValidActivityTypeString(String activities) throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \"This is a description\",\n" +
				"  \"activity_type\":[ \n" +
				activities +
				"  ],\r\n" +
				"  \"continuous\": false,\n" +
				"  \"start_time\": \"2020-02-20T08:00:00+1300\", \n" +
				"  \"end_time\": \"2020-02-20T08:00:00+1300\",\n" +
				"  \"location\": \"Christchurch, NZ\"\n" +
				"}";

		postActivityJson(json)
				.andExpect(status().isCreated());

		ArgumentCaptor<ChangeLog> changeLogCaptor = ArgumentCaptor.forClass(ChangeLog.class);
		Mockito.verify(changeLogRepository).save(changeLogCaptor.capture());
		assertEquals(1, changeLogCaptor.getAllValues().size());
		ChangeLog createLog = changeLogCaptor.getValue();
		assertEquals(user.getUserId(), createLog.getEditingUser().getUserId());
		assertEquals(ChangedAttribute.ACTIVITY_EXISTENCE, createLog.getChangedAttribute());
		assertEquals(ActionType.CREATED, createLog.getActionType());
	}

	@ParameterizedTest
	@CsvSource({
			"\"boating\"", "\"cycling\"", "\"boating\", \"cycling\""
	})
	public void testCreateActivityInvalidActivityTypeDoesNotExist(String activities) throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \"This is a description\",\n" +
				"  \"activity_type\":[ \n" +
				activities +
				"  ],\r\n" +
				"  \"continuous\": false,\n" +
				"  \"start_time\": \"2020-02-20T08:00:00+1300\", \n" +
				"  \"end_time\": \"2020-02-20T08:00:00+1300\",\n" +
				"  \"location\": \"Christchurch, NZ\"\n" +
				"}";

		postActivityJson(json)
				.andExpect(status().isNotFound());

		Mockito.verifyNoInteractions(changeLogRepository);
	}

	@ParameterizedTest
	@CsvSource({
			"\"walking", "coding\""
	})
	public void testCreateActivityInvalidActivityTypeBrokenJson(String activities) throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \"This is a description\",\n" +
				"  \"activity_type\":[ \n" +
				activities +
				"  ],\r\n" +
				"  \"continuous\": false,\n" +
				"  \"start_time\": \"2020-02-20T08:00:00+1300\", \n" +
				"  \"end_time\": \"2020-02-20T08:00:00+1300\",\n" +
				"  \"location\": \"Christchurch, NZ\"\n" +
				"}";

		postActivityJson(json)
				.andExpect(status().isBadRequest());

		Mockito.verifyNoInteractions(changeLogRepository);
	}

	@Test
	public void testUpdateActivity_Success_LogsChanges() throws Exception {
		long activityId = 3L;
		Activity testActivity = new Activity("Old Name", false, "Test Location", profile, testActivityTypes);
		testActivity.setDescription("the old activity description");
		testActivity.setId(activityId);
		Mockito.when(activityRepo.findById(activityId)).thenReturn(Optional.of(testActivity));
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \"This is a description\",\n" +
				"  \"activity_type\":[\"walking\"],\r\n" +
				"  \"continuous\": false,\n" +
				"  \"start_time\": \"2020-02-20T08:00:00+1300\", \n" +
				"  \"end_time\": \"2020-02-20T08:00:00+1300\",\n" +
				"  \"location\": \"Christchurch, NZ\"\n" +
				"}";

		mvc.perform(MockMvcRequestBuilders
				.put("/profiles/"+user.getUserId()+"/activities/" + activityId)
				.content(json).contentType(MediaType.APPLICATION_JSON)
				.requestAttr("authenticatedid", user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		ArgumentCaptor<ChangeLog> changeLogCaptor = ArgumentCaptor.forClass(ChangeLog.class);
		Mockito.verify(changeLogRepository, Mockito.atLeastOnce()).save(changeLogCaptor.capture());
		List<ChangeLog> updateLogs = changeLogCaptor.getAllValues();
		assertTrue(updateLogs.size() > 0);
		assertTrue(updateLogs.stream().allMatch(changeLog -> changeLog.getEditingUser().getUserId() == profile.getUser().getUserId()));
		assertTrue(updateLogs.stream().allMatch(changeLog -> changeLog.getEntityId() == activityId));
	}

	@Test
	public void testDeleteActivity_Success_LogsChanges() throws Exception {
		long activityId = 3L;
		Activity testActivity = new Activity("Old Name", false, "Test Location", profile, testActivityTypes);
		testActivity.setId(activityId);
		Mockito.when(activityRepo.findById(activityId)).thenReturn(Optional.of(testActivity));

		mvc.perform(MockMvcRequestBuilders
				.delete("/profiles/"+user.getUserId()+"/activities/" + activityId)
				.requestAttr("authenticatedid", user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		ArgumentCaptor<ChangeLog> changeLogCaptor = ArgumentCaptor.forClass(ChangeLog.class);
		Mockito.verify(changeLogRepository).save(changeLogCaptor.capture());
		assertEquals(1, changeLogCaptor.getAllValues().size());
		ChangeLog deleteLog = changeLogCaptor.getValue();
		assertEquals(user.getUserId(), deleteLog.getEditingUser().getUserId());
		assertEquals(ChangedAttribute.ACTIVITY_EXISTENCE, deleteLog.getChangedAttribute());
		assertEquals(ActionType.DELETED, deleteLog.getActionType());
	}

	@ParameterizedTest
	@CsvSource({
			"test outcome, km/h, other outcome, strokes",
			"testing min vals, test, 123, 1",
			"testing max vals, test, this is thirty characters, ok?, this is 10",
	})
	public void testCreateActivityWithOutcomes_ValidOutcomes_OutcomesAreCreated(
			String outcome1Description, String outcome1Units, String outcome2Description, String outcome2Units) throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \"I really like testing\",\n" +
				"  \"activity_type\":[ \"coding\" ],\r\n" +
				"  \"continuous\": true,\n" +
				"  \"location\": \"Christchurch, NZ\",\n" +
				"  \"outcomes\": [" +
				"{\"description\": \"" + outcome1Description + "\", \"units\": \"" + outcome1Units + "\" }," +
				"{\"description\": \"" + outcome2Description + "\", \"units\": \"" + outcome2Units + "\" }" +
				"] " +
				"}";

		postActivityJson(json)
				.andExpect(status().isCreated());

		ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
		Mockito.verify(activityRepo).save(activityCaptor.capture());
		assertEquals(1, activityCaptor.getAllValues().size());
		Activity createdActivity = activityCaptor.getValue();
		assertEquals(2, createdActivity.getOutcomes().size());
	}

	@Test
	public void testCreateActivityWithOutcomes_MissingOutcomeDesription_400() throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \"I really like testing\",\n" +
				"  \"activity_type\":[ \"coding\" ],\r\n" +
				"  \"continuous\": true,\n" +
				"  \"location\": \"Christchurch, NZ\",\n" +
				"  \"outcomes\": [" +
				"{ \"units\": \"km/h\" }" +
				"] " +
				"}";

		postActivityJson(json)
				.andExpect(status().isBadRequest())
				.andDo(result -> {
					Exception thrown = result.getResolvedException();
					assertNotNull(thrown);
					assertTrue(thrown instanceof InvalidRequestFieldException);
					assertEquals("outcome missing description field", thrown.getMessage());
				});
	}


	@Test
	public void testCreateActivityWithOutcomes_MissingOutcomeUnits_400() throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \"I really like testing\",\n" +
				"  \"activity_type\":[ \"coding\" ],\r\n" +
				"  \"continuous\": true,\n" +
				"  \"location\": \"Christchurch, NZ\",\n" +
				"  \"outcomes\": [" +
				"{ \"description\": \"this is a description\" }" +
				"] " +
				"}";

		postActivityJson(json)
				.andExpect(status().isBadRequest())
				.andDo(result -> {
					Exception thrown = result.getResolvedException();
					assertNotNull(thrown);
					assertTrue(thrown instanceof InvalidRequestFieldException);
					assertEquals("outcome missing units field", thrown.getMessage());
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1",
			"12",
			"this is thirty one characs, ok?",
	})
	public void testCreateActivityWithOutcomes_InvalidDescriptionLength_400(String outcomeDescription) throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \"I really like testing\",\n" +
				"  \"activity_type\":[ \"coding\" ],\r\n" +
				"  \"continuous\": true,\n" +
				"  \"location\": \"Christchurch, NZ\",\n" +
				"  \"outcomes\": [" +
				"{\"description\": \"" + outcomeDescription + "\", \"units\": \"km/h\" }" +
				"] " +
				"}";

		postActivityJson(json)
				.andExpect(status().isBadRequest())
				.andDo(result -> {
					Exception thrown = result.getResolvedException();
					assertNotNull(thrown);
					assertTrue(thrown instanceof InvalidRequestFieldException);
					assertEquals("outcome descriptions must be between 3 and 30 characters", thrown.getMessage());
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"",
			"this is 11!"
	})
	public void testCreateActivityWithOutcomes_InvalidUnitsLength_400(String outcomeUnits) throws Exception {
		String json = "{\n" +
				"  \"activity_name\": \"SENG302\",\n" +
				"  \"description\": \"I really like testing\",\n" +
				"  \"activity_type\":[ \"coding\" ],\r\n" +
				"  \"continuous\": true,\n" +
				"  \"location\": \"Christchurch, NZ\",\n" +
				"  \"outcomes\": [" +
				"{\"description\": \"this is a description\", \"units\": \"" + outcomeUnits + "\" }" +
				"] " +
				"}";

		postActivityJson(json)
				.andExpect(status().isBadRequest())
				.andDo(result -> {
					Exception thrown = result.getResolvedException();
					assertNotNull(thrown);
					assertTrue(thrown instanceof InvalidRequestFieldException);
					assertEquals("outcome units must be between 1 and 10 characters", thrown.getMessage());
				});
	}

	@Test
	public void testGetActivityWithOutcomes_Exists_200() throws Exception {
		long activityId = 10;
		Activity testActivity = new Activity("Test Activity", false, "Test Location", profile, testActivityTypes);
		testActivity.setId(activityId);
		ActivityOutcome outcome1 = new ActivityOutcome("first outcome", "km/h");
		ActivityOutcome outcome2 = new ActivityOutcome("second outcome", "m/s");
		testActivity.addOutcome(outcome1);
		testActivity.addOutcome(outcome2);
		Mockito.when(activityRepo.findById(activityId)).thenReturn(Optional.of(testActivity));

		mvc.perform(MockMvcRequestBuilders
				.get("/activities/" + activityId)
				.requestAttr("authenticatedid", user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.activity_id").value(activityId))
				.andExpect(jsonPath("$.outcomes", hasSize(2)))
				.andExpect(jsonPath("$.outcomes[0].description").value(outcome1.getDescription()))
				.andExpect(jsonPath("$.outcomes[0].units").value(outcome1.getUnits()))
				.andExpect(jsonPath("$.outcomes[1].description").value(outcome2.getDescription()))
				.andExpect(jsonPath("$.outcomes[1].units").value(outcome2.getUnits()));
	}

	@Test
	public void testGetActivity_DoesNotExist_RecordNotFound() throws Exception {
		long activityId = 10;
		Mockito.when(activityRepo.findById(activityId)).thenReturn(Optional.empty());

		mvc.perform(MockMvcRequestBuilders
				.get("/activities/" + activityId)
				.requestAttr("authenticatedid", user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andDo(result -> {
					Exception thrown = result.getResolvedException();
					assertTrue(thrown instanceof RecordNotFoundException);
					assertEquals("Activity doesn't exist", thrown.getMessage());
				});
	}
}


