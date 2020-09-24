package com.springvuegradle.endpoints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.springvuegradle.model.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityOutcome;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ActivitiesController.class})
@WebMvcTest
public class ActivitiesControllerOutcomesTest {

	@InjectMocks
	ActivitiesController activitiesController;

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ActivityRepository activityRepo;

	@MockBean
	private ActivityTypeRepository activityTypeRepo;

	@MockBean
	private ActivityPinRepository activityPinRepository;

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
	
	@MockBean
	private ActivityParticipantResultRepository activityOutcomeRepo;
	
	@MockBean
    private ActivityOutcomeRepository activityOutcomeRepository;

	@MockBean
	EmailRepository emailRepository;

	User user;
	Profile profile;
	Set<ActivityType> testActivityTypes;
	
	Activity activity;
	Activity activity2;
	
	ActivityOutcome outcome1_1;
	ActivityOutcome outcome1_2;
	ActivityOutcome outcome2_1;

	@BeforeEach
	void beforeEach() {
		activitiesController = new ActivitiesController();
		MockitoAnnotations.initMocks(this);

		user = new User(1);
		Mockito.when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));

		profile = new Profile(user, "Bob","Builder", LocalDate.of(2000, 10, 15), Gender.MALE);
		Mockito.when(profileRepo.findById(user.getUserId())).thenReturn(Optional.of(profile));

		testActivityTypes = new HashSet<>();
		for (String activityType : new String[] {"juggling", "fun"}) {
			ActivityType testActivityType = new ActivityType(activityType);
			testActivityTypes.add(testActivityType);
			Mockito.when(activityTypeRepo.getActivityTypeByActivityTypeName(activityType)).thenReturn(Optional.of(testActivityType));
		}
		
		activity = new Activity("Juggling contest", false, "The Circus", profile, testActivityTypes);
		activity.setDescription("How many balls can we can juggle at once?");
		activity.setId(1);
		Mockito.when(activityRepo.findById(1L)).thenReturn(Optional.of(activity));
		
		activity2 = new Activity("Stealing tennis balls", false, "The Circus", profile, testActivityTypes);
		activity2.setDescription("Ruin the juggling contest by stealing the balls as they're juggling them");
		activity2.setId(2);
		Mockito.when(activityRepo.findById(2L)).thenReturn(Optional.of(activity2));

		outcome1_1 = new ActivityOutcome("Most balls juggled", "balls");
		outcome1_1.setActivity(activity);
		outcome1_1.setOutcomeId(10);
		
		outcome1_2 = new ActivityOutcome("Longest time juggling", "seconds");
		outcome1_2.setActivity(activity);
		outcome1_2.setOutcomeId(11);
		
		outcome2_1 = new ActivityOutcome("Longest time juggling", "seconds");
		outcome2_1.setActivity(activity2);
		outcome2_1.setOutcomeId(11);
		
		Mockito.when(activityOutcomeRepository.getOutcomesById(Mockito.anyList())).thenAnswer(new Answer<List<ActivityOutcome>>() {
			public List<ActivityOutcome> answer(InvocationOnMock invocation) throws Throwable {
        		List<Long> id = invocation.getArgument(0);
        		List<ActivityOutcome> response = new ArrayList<ActivityOutcome>();
        		
        		for (long outcomeId : id) {
        			if (outcomeId == 10) response.add(outcome1_1);
        			if (outcomeId == 11) response.add(outcome1_2);
        			if (outcomeId == 12) response.add(outcome2_1);
        		}
        		
        		return response;
        	}
    	});
	}
	
	@Test
	public void testRecordingTwoResultsOnActivity() throws Exception {
		String json = "{\r\n" +
				"    \"outcomes\": [\r\n" +
				"        {\r\n" +
				"            \"outcome_id\": 10,\r\n" +
				"            \"result\": \"6\",\r\n" +
				"            \"completed_date\": \"2020-08-14T00:15:10+0000\"\r\n" +
				"        },\r\n" +
				"        {\r\n" +
				"            \"outcome_id\": 11,\r\n" +
				"            \"result\": \"last place\",\r\n" +
				"            \"completed_date\": \"2020-08-14T00:15:10+0000\"\r\n" +
				"        }\r\n" +
				"    ]\r\n" +
				"}\r\n" +
				"";

		mvc.perform(MockMvcRequestBuilders
				.post("/activities/1/results")
				.contentType(MediaType.APPLICATION_JSON).content(json)
				.requestAttr("authenticatedid", user.getUserId()))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void testRecordingSingleResultOnActivity() throws Exception {
		String json = "{ \"outcomes\": [\r\n" +
				"        {\r\n" + 
				"            \"outcome_id\": 10,\r\n" + 
				"            \"result\": \"6\",\r\n" + 
				"            \"completed_date\": \"2020-08-14T00:15:10+0000\"\r\n" +
				"        }\r\n" +
				"    ]}";

		mvc.perform(MockMvcRequestBuilders
				.post("/activities/1/results")
				.contentType(MediaType.APPLICATION_JSON).content(json)
				.requestAttr("authenticatedid", user.getUserId()))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void testRecordingSingleResultOnOtherActivity() throws Exception {
		String json = "{ \"outcomes\": [\r\n" +
				"        {\r\n" + 
				"            \"outcome_id\": 10,\r\n" + 
				"            \"result\": \"6\",\r\n" + 
				"            \"completed_date\": \"2020-08-14T00:15:10+0000\"\r\n" +
				"        }\r\n" +
				"    ]}";

		mvc.perform(MockMvcRequestBuilders
				.post("/activities/2/results")
				.contentType(MediaType.APPLICATION_JSON).content(json)
				.requestAttr("authenticatedid", user.getUserId()))
				.andExpect(status().isBadRequest());
	}
}


