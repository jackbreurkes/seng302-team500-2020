package com.springvuegradle.endpoints;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import com.springvuegradle.model.repository.*;
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
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;

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
    
    @MockBean
    private SessionRepository sessionRepository;

    @Autowired
    @MockBean
    private ActivityTypeRepository activityTypeRepository;

    @MockBean
    private LocationRepository locationRepository;


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
    
    // ====== Delete User =========================================
    
    @Test
    public void testDeleteUser() throws Exception {
    	long userId = 1;
    	User user = new User(userId);
    	
    	Profile profile = new Profile(user, "firstName", "lastName", LocalDate.now(), Gender.NON_BINARY);
    	
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(profileRepository.findById(userId)).thenReturn(Optional.of(profile));
        
        Mockito.doNothing().when(userRepository).delete(user);
        Mockito.doNothing().when(profileRepository).delete(profile);
        Mockito.doNothing().when(sessionRepository).deleteUserSession(user);
        Mockito.doNothing().when(emailRepository).deleteUserEmails(user);
        
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/{id}", userId)
        		.requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
        		.andDo(print())
                .andExpect(status().isOk());
        
        Mockito.verify(userRepository).delete(user);
        Mockito.verify(profileRepository).delete(profile);
        Mockito.verify(sessionRepository).deleteUserSession(user);
        Mockito.verify(emailRepository).deleteUserEmails(user);
    }
    
    @Test
    public void testDeleteUserNotAuthenticated() throws Exception {
    	long userId = 1;
    	User user = new User(userId);
    	
    	Profile profile = new Profile(user, "firstName", "lastName", LocalDate.now(), Gender.NON_BINARY);
    	
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(profileRepository.findById(userId)).thenReturn(Optional.of(profile));
        
        Mockito.doNothing().when(userRepository).delete(user);
        Mockito.doNothing().when(profileRepository).delete(profile);
        Mockito.doNothing().when(sessionRepository).deleteUserSession(user);
        Mockito.doNothing().when(emailRepository).deleteUserEmails(user);
        
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/{id}", userId)
                .accept(MediaType.APPLICATION_JSON))
        		.andDo(print())
                .andExpect(status().isUnauthorized());
        
    }
    
    @Test
    public void testDeleteUserDoesNotExist() throws Exception {
    	long userId = 1;
    	User user = new User(userId);
    	Profile profile = new Profile(user, "firstName", "lastName", LocalDate.now(), Gender.NON_BINARY);

    	long authId = 2;
    	User authUser = new User(authId);
    	authUser.setPermissionLevel(126);
    	
        Mockito.when(userRepository.findById(authId)).thenReturn(Optional.of(authUser));
        Mockito.when(profileRepository.findById(userId)).thenReturn(Optional.empty());
        
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/{id}", userId)
        		.requestAttr("authenticatedid", authUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
        		.andDo(print())
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testDeleteOtherUser() throws Exception {
    	long authId = 2;
    	User authUser = new User(authId);
    	
    	long userId = 1;
    	User user = new User(userId);
    	
    	Profile profile = new Profile(user, "firstName", "lastName", LocalDate.now(), Gender.NON_BINARY);
    	
        Mockito.when(userRepository.findById(authId)).thenReturn(Optional.of(authUser));
        Mockito.when(profileRepository.findById(userId)).thenReturn(Optional.of(profile));
        
        Mockito.doNothing().when(userRepository).delete(user);
        Mockito.doNothing().when(profileRepository).delete(profile);
        Mockito.doNothing().when(sessionRepository).deleteUserSession(user);
        Mockito.doNothing().when(emailRepository).deleteUserEmails(user);
        
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/{id}", userId)
        		.requestAttr("authenticatedid", authId)
                .accept(MediaType.APPLICATION_JSON))
        		.andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testDeleteUserAsAdmin() throws Exception {
    	long adminId = 1;
    	User admin = new User(adminId);
    	admin.setPermissionLevel(126);
    	
    	long userId = 2;
    	User user = new User(userId);
    	Profile profile = new Profile(user, "firstName", "lastName", LocalDate.now(), Gender.NON_BINARY);
    	
        Mockito.when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        Mockito.when(profileRepository.findById(userId)).thenReturn(Optional.of(profile));
        
        Mockito.doNothing().when(userRepository).delete(user);
        Mockito.doNothing().when(profileRepository).delete(profile);
        Mockito.doNothing().when(sessionRepository).deleteUserSession(user);
        Mockito.doNothing().when(emailRepository).deleteUserEmails(user);
        
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/{id}", userId)
        		.requestAttr("authenticatedid", admin.getUserId())
                .accept(MediaType.APPLICATION_JSON))
        		.andDo(print())
                .andExpect(status().isOk());
        
        Mockito.verify(userRepository).delete(user);
        Mockito.verify(profileRepository).delete(profile);
        Mockito.verify(sessionRepository).deleteUserSession(user);
        Mockito.verify(emailRepository).deleteUserEmails(user);
    }


}
