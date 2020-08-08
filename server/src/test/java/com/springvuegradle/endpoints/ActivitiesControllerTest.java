package com.springvuegradle.endpoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.springvuegradle.model.data.ActionType;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.ChangeLog;
import com.springvuegradle.model.data.ChangedAttribute;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.ChangeLogRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.SubscriptionRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.repository.UserRepository;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ActivitiesController.class})
@WebMvcTest
public class ActivitiesControllerTest {

	@InjectMocks
	private ActivitiesController activitiesController;

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

		mvc.perform(MockMvcRequestBuilders
				.post("/profiles/"+user.getUserId()+"/activities")
				.content(json).contentType(MediaType.APPLICATION_JSON)
				.requestAttr("authenticatedid", user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
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

		mvc.perform(MockMvcRequestBuilders
				.post("/profiles/"+user.getUserId()+"/activities")
				.content(json).contentType(MediaType.APPLICATION_JSON)
				.requestAttr("authenticatedid", user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
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

		mvc.perform(MockMvcRequestBuilders
				.post("/profiles/"+user.getUserId()+"/activities")
				.content(json).contentType(MediaType.APPLICATION_JSON)
				.requestAttr("authenticatedid", user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
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

		mvc.perform(MockMvcRequestBuilders
				.post("/profiles/"+user.getUserId()+"/activities")
				.content(json).contentType(MediaType.APPLICATION_JSON)
				.requestAttr("authenticatedid", user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
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

		mvc.perform(MockMvcRequestBuilders
				.post("/profiles/"+user.getUserId()+"/activities")
				.content(json).contentType(MediaType.APPLICATION_JSON)
				.requestAttr("authenticatedid", user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
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

		mvc.perform(MockMvcRequestBuilders
				.post("/profiles/"+user.getUserId()+"/activities")
				.content(json).contentType(MediaType.APPLICATION_JSON)
				.requestAttr("authenticatedid", user.getUserId())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
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
}
