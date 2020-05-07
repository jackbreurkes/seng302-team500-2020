package com.springvuegradle.endpoints;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.CountryRepository;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;

//@Configuration
//@ComponentScan("repository")
@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserProfileController.class})
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserProfileControllerMockTest {
	
	@InjectMocks
    UserProfileController userProfileController;

    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private EmailRepository emailRepository;
    
    @MockBean
    private ProfileRepository profileRepository;
    
    @MockBean
    private CountryRepository countryRepository;

    @Autowired
    @MockBean
    private ActivityTypeRepository activityTypeRepository;
    
    @Autowired
    private MockMvc mvc;

    private ActivityType tempActivityType;

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
        this.tempActivityType = new ActivityType("Running");
    }

    @BeforeEach
    public void beforeEach(){
    	this.userProfileController = new UserProfileController();
    }

    @Test
    public void testCreateUserWithNonexistentActivities() throws Exception {
    	
    	//mock the return of activityTypeRepository.getActivityTypeByActivityTypeName
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Walking")).thenReturn(Optional.empty());
    	
        String createUserJson = "{\n" +
            "  \"lastname\": \"Benson\",\n" +
            "  \"firstname\": \"Maurice\",\n" +
            "  \"middlename\": \"Jack\",\n" +
            "  \"nickname\": \"JackyTest\",\n" +
            "  \"primary_email\": \"111@google.com\",\n" +
            "  \"additional_email\": [\n" +
            "    \"jacky@xtra.co.nz\",\n" +
            "    \"jacky@msn.com\"\n" +
            "    ],\n" +
            "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
            "  \"date_of_birth\": \"1985-12-20\",\n" +
            "  \"gender\": \"male\",\n" +
            "  \"password\": \"aaaaaaaa\",\n" +
            "  \"fitness\": 4,\n" +
            "  \"activities\": [\n" +
            "    \"Walking\"\n" +
            "  ]  \n" +
            "}";
        
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
   
    @Test
    public void testCreateUserWithOneActivity() throws Exception {	// Test works when adding activity type in database to user's profile

    	// Create an activity to be "stored in the database"
    	ActivityType running = new ActivityType("Running");
    	//mock the return of activityTypeRepository.getActivityTypeByActivityTypeName
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Running")).thenReturn(Optional.of(running));
    	
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"JackyTest\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"additional_email\": [\n" +
                "    \"jacky@xtra.co.nz\",\n" +
                "    \"jacky@msn.com\"\n" +
                "    ],\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"password\": \"aaaaaaaa\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"activities\": [\n" +
                "    \"Running\"\n" +
                "]  \n" +
                "}";
        
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    
    @Test
    public void testCreateUserWithMultipleActivities() throws Exception {	// Test works for multiple activities being adding, all of which exist in the database

    	ActivityType running = new ActivityType("Running");
    	ActivityType cycling = new ActivityType("Cycling");
    	ActivityType swimming = new ActivityType("Swimming");
    	//mock the return of activityTypeRepository.getActivityTypeByActivityTypeName
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Running")).thenReturn(Optional.of(running));
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Cycling")).thenReturn(Optional.of(cycling));
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Swimming")).thenReturn(Optional.of(swimming));
    	
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"JackyTest\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"additional_email\": [\n" +
                "    \"jacky@xtra.co.nz\",\n" +
                "    \"jacky@msn.com\"\n" +
                "    ],\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"password\": \"aaaaaaaa\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"activities\": [\n" +
                "    \"Running\",\n" +
                "    \"Cycling\",\n" +
                "    \"Swimming\"\n" +
                "  ]  \n" +
                "}";
        
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    
    @Test
    public void testCreateUserWithMultipleActivitiesAndOneNonExistent() throws Exception {	// Test with multiple activity types where only one does not exist in database

    	ActivityType running = new ActivityType("Running");
    	ActivityType cycling = new ActivityType("Cycling");
    	//mock the return of activityTypeRepository.getActivityTypeByActivityTypeName
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Running")).thenReturn(Optional.of(running));
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Cycling")).thenReturn(Optional.of(cycling));
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Swimming")).thenReturn(Optional.empty());
    	
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"JackyTest\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"additional_email\": [\n" +
                "    \"jacky@xtra.co.nz\",\n" +
                "    \"jacky@msn.com\"\n" +
                "    ],\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"password\": \"aaaaaaaa\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"activities\": [\n" +
                "    \"Running\",\n" +
                "    \"Cycling\",\n" +
                "    \"Swimming\"\n" +
                "  ]  \n" +
                "}";
        
        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}