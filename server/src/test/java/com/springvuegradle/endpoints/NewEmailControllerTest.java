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

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {NewEmailController.class})
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
    }

    @BeforeEach
    public void beforeEach(){
    	this.newEmailController = new NewEmailController();
    
        mockUser = new User(1);
        editedUser = new User(2);
    
    }
    
    /* Tests for endpoint:
     * POST /profiles/{profileId}/emails
	   {
		  "additional_email": [
		    "triplej@xtra.co.nz",
		    "triplej@msn.com"
		    ]
		}
	 */
    
    @Test
    public void updateOwnValidEmailsWithNewEmails() throws Exception {    	
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

    }
    
    @Test
    public void updateEmailsAsAdmin() throws Exception {  
    	mockUser.setPermissionLevel(126);
    	
    	String newEmail1 = "abc@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(null);
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";    	
    	
    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", mockUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

    }
    
    @Test
    public void updateEmailWithEmailAlreadyRegistered() throws Exception {    	
    	String newEmail1 = "abc@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(new User(editedUser.getUserId()+1), newEmail1, true));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";    	
    	    	
    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

    }
    
    @Test
    public void updateOwnEmailsWithInvalidEmail() throws Exception {    	
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "invalid.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    
    @Test
    public void updateOwnEmailsWithSomeOldEmails() throws Exception {    	
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(editedUser, newEmail1, false));
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";

    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

    }
    
    @Test
    public void updateOwnEmailsToEmpty() throws Exception {    	    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\": []\r\n" + 
    	    	"		}";

    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

    }
    
    @Test
    public void updateEmailsOfNonExistentUser() throws Exception {    	
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(null);
    	Mockito.when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(editedUser, newEmail1, false));
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";

    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", mockUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }
    
    @Test
    public void updateSomeoneElsesEmails() throws Exception {    	
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	
    	mockUser.setPermissionLevel(0);
    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

    }
    
    @Test
    public void updateOwnValidEmailsWithTooManyEmails() throws Exception {    	
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	String newEmail3 = "ghi@google.com";
    	String newEmail4 = "jkl@google.com";
    	String newEmail5 = "mno@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail3)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail4)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail5)).thenReturn(null);
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\",\r\n" + 
    	    	"		    \"" + newEmail3 + "\",\r\n" + 
    	    	"		    \"" + newEmail4 + "\",\r\n" + 
    	    	"		    \"" + newEmail5 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

    }
    
    @Test
    public void emailRequestMissingAdditionalEmailList() throws Exception {    	    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	String emailRequestJson = "{}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    
    @Test
    public void updateEmailsWithIllformatedJson() throws Exception {    	    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\", [\"email@email.com\"]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    
    @Test
    public void updateEmailsWithBadAdditionalEmailList() throws Exception {    	    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email:\", {1: \"email@email.com\"}]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .post("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    
    /*
     * # PUT /profiles/{profileId}/emails
		{
		  "primary_email": "triplej@google.com",
		  "additional_email": [
		    "triplej@xtra.co.nz",
		    "triplej@msn.com"
		  ]
		}
     */
    
    @Test
    public void updateOwnPrimaryEmail() throws Exception {
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	String newEmail3 = "ghi@google.com";
    	String newEmail4 = "jkl@google.com";
    	String newEmail5 = "mno@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail3)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail4)).thenReturn(new Email(editedUser, newEmail4, true));
    	Mockito.when(emailRepository.findByEmail(newEmail5)).thenReturn(new Email(editedUser, newEmail5, false));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": \"" + newEmail5 + "\",\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\",\r\n" + 
    	    	"		    \"" + newEmail3 + "\",\r\n" + 
    	    	"		    \"" + newEmail4 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    
    @Test
    public void updateSomeoneElsesPrimaryEmail() throws Exception {
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	String newEmail3 = "ghi@google.com";
    	String newEmail4 = "jkl@google.com";
    	String newEmail5 = "mno@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail3)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail4)).thenReturn(new Email(editedUser, newEmail4, true));
    	Mockito.when(emailRepository.findByEmail(newEmail5)).thenReturn(new Email(editedUser, newEmail5, false));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": \"" + newEmail5 + "\",\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\",\r\n" + 
    	    	"		    \"" + newEmail3 + "\",\r\n" + 
    	    	"		    \"" + newEmail4 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", mockUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void updateSomeoneElsesPrimaryEmailAsAdmin() throws Exception {
    	mockUser.setPermissionLevel(126);
    	
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	String newEmail3 = "ghi@google.com";
    	String newEmail4 = "jkl@google.com";
    	String newEmail5 = "mno@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail3)).thenReturn(null);
    	Mockito.when(emailRepository.findByEmail(newEmail4)).thenReturn(new Email(editedUser, newEmail4, true));
    	Mockito.when(emailRepository.findByEmail(newEmail5)).thenReturn(new Email(editedUser, newEmail5, false));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": \"" + newEmail5 + "\",\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\",\r\n" + 
    	    	"		    \"" + newEmail3 + "\",\r\n" + 
    	    	"		    \"" + newEmail4 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", mockUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    
    @Test
    public void updateOwnPrimaryEmailButNoChanges() throws Exception {
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	String newEmail3 = "ghi@google.com";
    	String newEmail4 = "jkl@google.com";
    	String newEmail5 = "mno@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(editedUser, newEmail1, false));
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(new Email(editedUser, newEmail2, false));
    	Mockito.when(emailRepository.findByEmail(newEmail3)).thenReturn(new Email(editedUser, newEmail3, false));
    	Mockito.when(emailRepository.findByEmail(newEmail4)).thenReturn(new Email(editedUser, newEmail4, false));
    	Mockito.when(emailRepository.findByEmail(newEmail5)).thenReturn(new Email(editedUser, newEmail5, true));	// Is the primary email
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": \"" + newEmail5 + "\",\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\",\r\n" + 
    	    	"		    \"" + newEmail3 + "\",\r\n" + 
    	    	"		    \"" + newEmail4 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    
    @Test
    public void updateOwnPrimaryEmailToOtheerUsersEmail() throws Exception {
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	String newEmail3 = "ghi@google.com";
    	String newEmail4 = "jkl@google.com";
    	String newEmail5 = "mno@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(editedUser, newEmail1, false));
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(new Email(editedUser, newEmail2, false));
    	Mockito.when(emailRepository.findByEmail(newEmail3)).thenReturn(new Email(editedUser, newEmail3, false));
    	Mockito.when(emailRepository.findByEmail(newEmail4)).thenReturn(new Email(editedUser, newEmail4, false));
    	Mockito.when(emailRepository.findByEmail(newEmail5)).thenReturn(new Email(mockUser, newEmail5, true));	// Is the primary email
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": \"" + newEmail5 + "\",\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\",\r\n" + 
    	    	"		    \"" + newEmail3 + "\",\r\n" + 
    	    	"		    \"" + newEmail4 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void updateEmailsBadJsonFormat() throws Exception {
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	String newEmail3 = "ghi@google.com";
    	String newEmail4 = "jkl@google.com";
    	String newEmail5 = "mno@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(editedUser, newEmail1, false));
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(new Email(editedUser, newEmail2, false));
    	Mockito.when(emailRepository.findByEmail(newEmail3)).thenReturn(new Email(editedUser, newEmail3, false));
    	Mockito.when(emailRepository.findByEmail(newEmail4)).thenReturn(new Email(editedUser, newEmail4, true));
    	Mockito.when(emailRepository.findByEmail(newEmail5)).thenReturn(new Email(editedUser, newEmail5, false));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": \"" + newEmail5 + "\",\r\n" + 
    	    	"		  \"additional_email\", [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\",\r\n" + 
    	    	"		    \"" + newEmail3 + "\",\r\n" + 
    	    	"		    \"" + newEmail4 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void putEmailsWithMissingPrimaryEmail() throws Exception {
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "def@google.com";
    	String newEmail3 = "ghi@google.com";
    	String newEmail4 = "jkl@google.com";
    	String newEmail5 = "mno@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(editedUser, newEmail1, false));
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(new Email(editedUser, newEmail2, false));
    	Mockito.when(emailRepository.findByEmail(newEmail3)).thenReturn(new Email(editedUser, newEmail3, false));
    	Mockito.when(emailRepository.findByEmail(newEmail4)).thenReturn(new Email(editedUser, newEmail4, false));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail1 + "\",\r\n" + 
    	    	"		    \"" + newEmail2 + "\",\r\n" + 
    	    	"		    \"" + newEmail3 + "\",\r\n" + 
    	    	"		    \"" + newEmail4 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void updatePrimaryEmailMissingAdditionalEmailsField() throws Exception {
    	String newEmail1 = "abc@google.com";

    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(editedUser, newEmail1, false));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": \"" + newEmail1 + "\"}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void updatePrimaryEmailToNull() throws Exception {
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": null}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void updateAdditionalEmailsToNull() throws Exception {
    	    	
    	String newEmail1 = "abc@google.com";

    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(editedUser, newEmail1, false));
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": \"" + newEmail1 + "\"," +
    	    	"		  \"additional_email\": null}";
    	
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    
    @Test
    public void updateToInvalidPrimaryEmail() throws Exception {
    	String newEmail1 = "abc.com";
    	String newEmail2 = "def@google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(editedUser, newEmail1, false));
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": \"" + newEmail1 + "\",\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail2 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void updateWithInvalidAdditionalEmail() throws Exception {
    	String newEmail1 = "abc@google.com";
    	String newEmail2 = "google.com";
    	    	
    	Mockito.when(userRepository.getOne(editedUser.getUserId())).thenReturn(editedUser);
    	Mockito.when(userRepository.findById(editedUser.getUserId())).thenReturn(Optional.of(editedUser));
    	Mockito.when(emailRepository.findByEmail(newEmail1)).thenReturn(new Email(editedUser, newEmail1, false));
    	Mockito.when(emailRepository.findByEmail(newEmail2)).thenReturn(null);
    	Mockito.when(emailRepository.getNonPrimaryEmails(editedUser)).thenReturn(new ArrayList<Email>());
    	
    	
    	String emailRequestJson = "{\r\n" + 
    	    	"		  \"primary_email\": \"" + newEmail1 + "\",\r\n" + 
    	    	"		  \"additional_email\": [\r\n" + 
    	    	"		    \"" + newEmail2 + "\"\r\n" + 
    	    	"		    ]\r\n" + 
    	    	"		}";
    	
    	mvc.perform(MockMvcRequestBuilders
                .put("/profiles/" + editedUser.getUserId() + "/emails")
                .content(emailRequestJson).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", editedUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
}
