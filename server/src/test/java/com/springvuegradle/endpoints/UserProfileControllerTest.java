package com.springvuegradle.endpoints;

import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserProfileController.class})
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private EmailRepository emailRepository;
    @MockBean
    private ProfileRepository profileRepository;
    @MockBean
    private CountryRepository countryRepository;
    @MockBean
    private ActivityRepository activityRepository;
    @MockBean
    private ActivityTypeRepository activityTypeRepository;
    @MockBean
    private LocationRepository locationRepository;
    
    /**
     * Creates the edit password controller and inserts the mocks we define in the place of the repositories
     */
    @InjectMocks
    UserProfileController userProfileController;
 
    ActivityType tempActivityType;
    
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void beforeEach(){
        //Create a new user and set a password
        this.tempActivityType = new ActivityType("Running");
    }


    @Test
    @Disabled
    public void testGetProfileById() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .get("/profiles/{id}", 5)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.employees").exists())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].employeeId").isNotEmpty());
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(5));
    }
 
    @Test
    public void testValidCreateUser() throws Exception {
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
                "  \"passports\": [\n" +
//                "    \"United States of America\",\n" +
//                "    \"Thailand\"\n" +
                "  ]\n" +
                "}";


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile_id").value(0));
    }

    @Test
    public void testMinimalCreateUser() throws Exception {
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"password\": \"password\"\n" +
                "}";


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile_id").value(0));
    }

    @Test
    public void testMissingFirstname() throws Exception {
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"password\": \"password\"\n" +
                "}";


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
                // TODO: our current MockMvc implementation does not use our ExceptionHandlerController, so
        //      // TODO:   these tests do not return any JSON data. would be nice to figure out how to fix that.
                //.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("No firstname field"));
    }

    @Test
    public void testMissingPassword() throws Exception {
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\"\n" +
                "}";


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
                //.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("No password field"));
    }

    @Test
    public void testInvalidJsonString() throws Exception {
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\":: \"jacky@google.com\",\n" + // double colon on this line
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEmptyLists() throws Exception {
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"JackyTest\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"additional_email\": [],\n" + // empty
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"password\": \"aaaaaaaa\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [],\n" + // empty
                "  \"activities\": []\n" + // empty
                "}";


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile_id").value(0));
    }
    
    // ----------------------- Admin promotion/demotion tests ------------------------------------
    
    @Test
    public void testPromoteOtherUserToAdmin() throws Exception {
    	String updateRoleJson = "{\"role\": \"admin\"}";
    	
    	Long adminId = 0l;
    	User adminUser = new User(adminId);
    	adminUser.setPermissionLevel(126);
    	
    	Long profileId = 1l;
    	User userToEdit = new User(profileId);
    	    	
    	Mockito.when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    	Mockito.when(userRepository.findById(profileId)).thenReturn(Optional.of(userToEdit));
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + profileId + "/role")
                .content(updateRoleJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", adminId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    public void testPromoteToNonExistentRole() throws Exception {
    	String updateRoleJson = "{\"role\": \"ultimateadmin\"}";
    	
    	Long adminId = 0l;
    	User adminUser = new User(adminId);
    	adminUser.setPermissionLevel(126);
    	
    	Long profileId = 1l;
    	User userToEdit = new User(profileId);
    	    	
    	Mockito.when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    	Mockito.when(userRepository.findById(profileId)).thenReturn(Optional.of(userToEdit));
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + profileId + "/role")
                .content(updateRoleJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", adminId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testNonAdminPromoting() throws Exception {
    	String updateRoleJson = "{\"role\": \"admin\"}";
    	
    	Long adminId = 0l;
    	User adminUser = new User(adminId);
    	
    	Long profileId = 1l;
    	User userToEdit = new User(profileId);
    	    	
    	Mockito.when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    	Mockito.when(userRepository.findById(profileId)).thenReturn(Optional.of(userToEdit));
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + profileId + "/role")
                .content(updateRoleJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", adminId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void testDemoteSelf() throws Exception {
    	String updateRoleJson = "{\"role\": \"user\"}";
    	
    	Long adminId = 0l;
    	User adminUser = new User(adminId);
    	adminUser.setPermissionLevel(126);
    	
    	Mockito.when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    	    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + adminId + "/role")
                .content(updateRoleJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", adminId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    public void testDemoteOtherUser() throws Exception {
    	String updateRoleJson = "{\"role\": \"user\"}";
    	
    	Long adminId = 0l;
    	User adminUser = new User(adminId);
    	adminUser.setPermissionLevel(126);
    	
    	Long profileId = 1l;
    	User userToEdit = new User(profileId);
    	userToEdit.setPermissionLevel(126);
    	
    	Mockito.when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    	Mockito.when(userRepository.findById(profileId)).thenReturn(Optional.of(userToEdit));
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + profileId + "/role")
                .content(updateRoleJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", adminId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    public void testPromoteToSuperAdmin() throws Exception {
    	String updateRoleJson = "{\"role\": \"superadmin\"}";
    	
    	Long adminId = 0l;
    	User adminUser = new User(adminId);
    	adminUser.setPermissionLevel(126);
    	
    	Long profileId = 1l;
    	User userToEdit = new User(profileId);
    	    	
    	Mockito.when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
    	Mockito.when(userRepository.findById(profileId)).thenReturn(Optional.of(userToEdit));
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + profileId + "/role")
                .content(updateRoleJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", adminId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}