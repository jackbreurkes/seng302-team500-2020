package cucumber.steps;

import com.springvuegradle.endpoints.UserProfileController;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.requests.ProfileObjectMapper;
import com.springvuegradle.model.responses.ProfileResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EditProfileStepsTest {

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ActivityTypeRepository activityTypeRepository;

    @Mock
    private LocationRepository locationRepository;

    private User testUser;
    private Profile testProfile;
    private ProfileObjectMapper updateProfileRequest;
    private MockHttpServletRequest mockRequest;
    private ProfileResponse successReponse;
    private Exception errorResponse;


    @Before
    public void beforeEachScenario() {
        userProfileController = new UserProfileController();
        MockitoAnnotations.initMocks(this);
        mockRequest = new MockHttpServletRequest();
    }

    @Given("there is a profile in the database with profile id {int}")
    public void there_is_a_user_in_the_database_with_profile_id(Integer id) {
        testUser = new User((long)id);
        testProfile = new Profile(testUser, "Tessa", "Testman", LocalDate.of(1990, 05, 23), Gender.FEMALE);
        when(userRepository.findById((long)id)).thenReturn(Optional.of(testUser));
        when(profileRepository.existsById((long)id)).thenReturn(true);
        when(profileRepository.findById((long)id)).thenReturn(Optional.of(testProfile));
        when(emailRepository.getPrimaryEmail(testUser)).thenReturn("test@gmail.com");
        when(emailRepository.getNonPrimaryEmails(testUser)).thenReturn(new ArrayList<>());
    }


    @Given("I am not not logged in")
    public void i_am_not_not_logged_in() {
        mockRequest.clearAttributes(); // removes authenticatedid if exists
    }

    @When("I try to view the profile with id {int}")
    public void i_try_to_view_the_profile_with_id(Integer id) {
        try {
            successReponse = userProfileController.viewProfile((long) id, mockRequest);
        } catch (UserNotAuthenticatedException | RecordNotFoundException e) {
            errorResponse = e;
        }
    }

    @Then("I will receive a message that I am not logged in")
    public void i_will_receive_a_message_that_i_am_not_logged_in() {
        assertTrue(errorResponse instanceof UserNotAuthenticatedException);
        assertFalse(errorResponse.getMessage().isBlank());
    }

    @Given("I am logged in as the profile with id {int}")
    public void i_am_logged_in_as_the_profile_with_id(Integer id) {
        mockRequest.setAttribute("authenticatedid", id);
    }

    @Then("I will view the profile with id {int}")
    public void i_will_view_the_profile_with_id(Integer id) {
        assertNotNull(successReponse);
        assertEquals((long)id, successReponse.getProfile_id());
    }

    @Given("I am updating the profile with id {int}'s {string} information to {string}")
    public void i_am_updating_the_profile_with_id_s_information_to(Integer id, String field, String newValue) {
        updateProfileRequest = new ProfileObjectMapper();
        updateProfileRequest.setFirstname(testProfile.getFirstName());
        updateProfileRequest.setLastname(testProfile.getLastName());
//        updateProfileRequest.setMiddlename(testProfile.getMiddleName());
//        updateProfileRequest.setNickname(testProfile.getNickName());
//        updateProfileRequest.setBio(testProfile.getBio());
        updateProfileRequest.setDateOfBirth(testProfile.getDob().format(DateTimeFormatter.ISO_LOCAL_DATE));
//        updateProfileRequest.setFitness(testProfile.getFitness());
//        updateProfileRequest.setGender(testProfile.getGender().getJsonName());
//        updateProfileRequest.setPassports(testProfile.getCountries().stream().map(Country::getName).toArray(String[]::new));
//        updateProfileRequest.setActivities(testProfile.getActivityTypes().stream().map(ActivityType::getActivityTypeName).collect(Collectors.toList()));
//        updateProfileRequest.setLocation(testProfile.getLocation());

        if (field.equals("bio")) {
            updateProfileRequest.setBio(newValue);
        }
    }

    @When("I try to update the profile information of the profile with id {int}")
    public void i_update_the_profile_information_of_the_profile_with_id(Integer id) {
        throw new io.cucumber.java.PendingException();
//        try {
//            successReponse = userProfileController.updateProfile(updateProfileRequest, (long) id, mockRequest);
//        } catch (InvalidRequestFieldException | ParseException | UserNotAuthenticatedException | RecordNotFoundException e) {
//            errorResponse = e;
//        }
    }

    @Then("the profile with id {int}'s {string} will have been updated to {string}")
    public void theProfileWithIdSWillHaveBeenUpdatedTo(int id, String field, String newValue) throws Throwable {
        assertNotNull(successReponse);
        assertEquals(id, successReponse.getProfile_id());
        if (field.equals("bio")) {
            assertEquals(newValue, successReponse.getBio());
        }
    }

    @When("I try to edit the profile with id {int}")
    public void i_try_to_edit_the_profile_with_id(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I will receive a message that I am not authenticated as the target user")
    public void the_profile_with_id_will_not_be_updated(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I am logged in as an admin user")
    public void i_am_logged_in_as_an_admin_user() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
