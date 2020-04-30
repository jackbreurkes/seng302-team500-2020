package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidPasswordException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.requests.UpdatePasswordRequest;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Configuration
@ComponentScan("repository")
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
    private ActivityTypeRepository activityTypeRepository;
    

    private MockMvc mvc;

    
    public ActivityType tempActivityType;

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
        this.tempActivityType = new ActivityType("Running");
    }

    @BeforeEach
    public void beforeEach(){
        //Create a new activity type called "Running"
    	this.userProfileController = new UserProfileController();
    }

    @Test
    public void testCreateUserWithNonexistentActivities() throws Exception {
    	System.out.println("Printed");
    	System.out.println(this.tempActivityType);
    	System.out.println(Optional.of(tempActivityType));
    	System.out.println(this.activityTypeRepository);
    	//System.out.println(activityTypeRepository.getActivityTypeByActivityTypeName("Running"));
    	System.out.println(userRepository);
    	System.out.println("Printed");
    	
    	//mock the return of activityTypeRepository.getActivityTypeByActivityTypeName
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName("Walking")).thenReturn(Optional.of(null));
    	
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
                "  ],\n" +
                "  \"activities\": [\n" +
                "    \"Walking\"\n" +
                "  ]  \n" +
                "}";
        
        
        System.out.println(activityTypeRepository.getActivityTypeByActivityTypeName("Running"));


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile_id").value(0));
    }
   


}
