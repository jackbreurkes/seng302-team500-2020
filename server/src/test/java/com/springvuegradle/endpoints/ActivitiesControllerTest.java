package com.springvuegradle.endpoints;

import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private ActivityRepository repo;
    
    @MockBean
    private ActivityTypeRepository activityTypeRepo;
    
    @MockBean
    private UserRepository userRepo;
    
    @MockBean
    private ProfileRepository profileRepo;

    @MockBean
	private ChangeLogRepository changeLogRepository;
    
    User user;
    Profile profile;
    
	@BeforeEach
    void beforeEach() {
        activitiesController = new ActivitiesController();
        
        user = new User(1);
        Mockito.when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
        
        profile = new Profile(user, "Bob","Builder", LocalDate.of(2000, 10, 15), Gender.MALE);
        Mockito.when(profileRepo.findById(user.getUserId())).thenReturn(Optional.of(profile));
        
        for (String activityType : new String[] {"walking", "coding", "flying"}) {
        	Mockito.when(activityTypeRepo.getActivityTypeByActivityTypeName(activityType)).thenReturn(Optional.of(new ActivityType(activityType)));
        }
        
        Mockito.when(repo.save(Mockito.any())).thenAnswer(new Answer<Activity>() {
            @Override
            public Activity answer(InvocationOnMock invocation) throws Throwable {
              Activity saving = invocation.getArgument(0);
              saving.setId(1);
              return saving;
            }
          });

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
    }
}
