package cucumber.steps;

import com.springvuegradle.endpoints.UserProfileController;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Location;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.requests.ProfileObjectMapper;
import com.springvuegradle.model.responses.ProfileResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import java.text.ParseException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MyLocationTestSteps {
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
        this.successReponse = null;
        this.errorResponse = null;
    }

    @Given(" there is a user with the email {string} in the database")
    public void there_is_a_user_with_the_email_in_the_database(String string) {
        this.testUser = new User(1L);
        this.testProfile = new Profile(this.testUser, "David", "Dad", LocalDate.now(), Gender.MALE);
        this.userRepository.save(this.testUser);
        this.profileRepository.save(this.testProfile);
    }

    @Given("I am logged in as the user with the email {string}")
    public void i_am_logged_in_as_the_user_with_the_email(String string) {
        this.mockRequest.setAttribute("authenticatedid", 1L);
    }

    @Given("I have not set my location")
    public void i_have_not_set_my_location(){
        this.testProfile.setLocation(null);
        this.profileRepository.save(this.testProfile);
    }

    @Given("I have set my location where I am normally based as {string} {string} {string}")
    public void i_set_my_location_given(String city, String state, String country) throws UserNotAuthenticatedException, InvalidRequestFieldException, ParseException, RecordNotFoundException, UserNotAuthorizedException {
        if(state.equalsIgnoreCase("null")){
            state = null;
        }
        Location l = new Location(city, state, country);
        updateProfileRequest.setLocation(l);
        userProfileController.updateProfile(this.updateProfileRequest, 1L, mockRequest);
    }

    @When("I view my profile")
    public void i_view_my_profile() throws UserNotAuthenticatedException, RecordNotFoundException {
        this.successReponse = userProfileController.viewProfile(1L, mockRequest);
    }

    @When("I set my location where I am normally based as {string} {string} {string}")
    public void i_set_my_location_where_I_am_normally_based_as(String city, String state, String country) {
        Location l = new Location(city, state, country);
        try{
            updateProfileRequest.setLocation(l);
            userProfileController.updateProfile(this.updateProfileRequest, 1L, mockRequest);
        }catch (Exception e){
            //error
            this.errorResponse = e;
        }
    }


    @Then("my location info should be empty")
    public void my_location_info_should_be_empty() {
        assertNull(this.successReponse.getLocation());
    }

    @Then("I should receive an error message informing me that a field that I entered was invalid")
    public void i_should_receive_an_error_message() {
        assertNotNull(this.errorResponse);
    }

    @Then("my profile should show my location info as {string} {string} {string}")
    public void my_profile_should_show_my_location_info_as(String city, String state, String country) {
        Location returnLoc = this.profileRepository.getOne(1L).getLocation();
        assertEquals(returnLoc.getCity(), city);
        assertEquals(returnLoc.getState(), state);
        assertEquals(returnLoc.getCountry(), country);
    }

}
