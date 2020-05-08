package com.springvuegradle.endpoints;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PostMapping;

import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityRepository;
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
public class NewEmailControllerTest {
	
	@InjectMocks
    NewEmailController newEmailController;

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
    
    @Autowired
    private MockMvc mvc;
    
    private User mockUser;
    private User editedUser;
    

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
        
        mockUser = new User();
        editedUser = new User();
    }

    @BeforeEach
    public void beforeEach(){
    	this.newEmailController = new NewEmailController();
    }
    
    @Ignore
    @Test
    public void updateOwnValidEmailsWithNewEmails() throws Exception {
    	// POST /profiles/{profileId}/emails
    	
    	/*
    	   {
			  "additional_email": [
			    "triplej@xtra.co.nz",
			    "triplej@msn.com"
			    ]
			}
    	 */
    	
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	//Mockito.when(emailRepository.save(new Email(editedUser, newEmail1, false))).;
    	//Mockito.when(emailRepository.delete(oldEmail)).thenReturn(editedUser);
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    			"			  \"additional_email\": [\r\n" + 
    			"			    \"triplej@xtra.co.nz\",\r\n" + 
    			"			    \"triplej@msn.com\"\r\n" + 
    			"			    ]\r\n" + 
    			"			}";
    	
    	
    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

    }
    
    

}
