package cucumber.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.springvuegradle.AppConfiguration;
import com.springvuegradle.RESTApplication;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.springvuegradle.endpoints.UserProfileController;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.CountryRepository;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.LocationRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.SessionRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.ProfileObjectMapper;
import com.springvuegradle.model.responses.ProfileResponse;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = AppConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchTestSteps {

    private MockMvc mvc;

    @InjectMocks
    private UserProfileController userProfileController;

    @Autowired
    private UserRepository userRepository;

    /*@Mock
    private SessionRepository sessionRepository;

    @Mock
    private EmailRepository emailRepository;*/

    @Autowired
    private ProfileRepository profileRepository;

    /*@Mock
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class SearchTestSteps {

	@Autowired
    private MockMvc mvc;

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
    private LocationRepository locationRepository;*/

    private User testUser;
    private User testAdminUser;
    private Profile testProfile;
    private ProfileObjectMapper updateProfileRequest;
    private MockHttpServletRequest mockRequest;
    private MvcResult requestResult;
    private Exception errorResponse;

    private List<Profile> profiles;

	@Given("the following profiles have been added to the database")
	public void the_following_profiles_have_been_added_to_the_database(io.cucumber.datatable.DataTable dataTable) {
		// Write code here that turns the phrase above into concrete actions
	    // For automatic transformation, change DataTable to one of
	    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
	    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
	    // Double, Byte, Short, Long, BigInteger or BigDecimal.
	    //
	    // For other transformations you can register a DataTableType.

		List<Map<String, String>> profilesToAdd = dataTable.asMaps();
		profiles = new ArrayList<Profile>();

		for (Map<String, String> person: profilesToAdd) {
			System.out.println("========== The next one is ===========");
			/*for (String key: person.keySet()) {
				System.out.println(key + " is: " + person.get(key));
				//profiles.add(e)
			}*/
			System.out.println("Their profile id is: " + person.get("profile id"));
			System.out.println("Their first name is: " + person.get("first name"));
			System.out.println("Their last name is: " + person.get("last name"));

			//profile id | first name | middle name | last name   | email                 | nickname     | interests
//			User newUser = new User(Long.parseLong(person.get("profile id")));
			User newUser = new User(); // TODO get this to work with the desired ID, not just `new User();`
			System.out.println("user: " + newUser);
			System.out.println("userRepository: " + userRepository);
//			newUser = userRepository.save(newUser);
			Profile newProfile = new Profile(newUser, person.get("first name"), person.get("last name"), LocalDate.now(), Gender.NON_BINARY);
			profileRepository.save(newProfile);
		}
	}

	@When("I search for profiles by {string} with the search term {string}")
	public void i_search_for_profiles_by_with_the_search_term(String searchBy, String searchTerm) {
	    /*requestResult = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam(searchBy, searchTerm)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(requestResult.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);
        LinkedHashMap<String, Object> userFound = (LinkedHashMap<String, Object>) profileFound.get("user");

    	assertEquals(BigInteger.valueOf(1l), userFound.get("user_id"));*/
	}

	@Then("I should receive exactly {int} results")
	public void i_should_receive_exactly_results(Integer int1) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@When("I search for profiles interested in {string} that are {string} together")
	public void i_search_for_profiles_interested_in_activities_by_method(String spaceSeparatedActivityTypes, String method) {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
	
	// Helper function to get the list of users from the JSON returned when searching for them using GET /profiles with query parameters
    private ArrayList<LinkedHashMap<String, Object>> getResultListJson(String raw) throws org.apache.tomcat.util.json.ParseException {
		JSONParser parser = new JSONParser(raw);
		ArrayList<LinkedHashMap<String, Object>> json = null;
		json = (ArrayList<LinkedHashMap<String, Object>>) parser.parse();
		return json;
	}
}