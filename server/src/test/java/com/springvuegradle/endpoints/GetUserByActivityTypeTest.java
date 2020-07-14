package com.springvuegradle.endpoints;

import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import org.apache.tomcat.util.json.JSONParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserProfileController.class})
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetUserByActivityTypeTest {

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

    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
    }

    @BeforeAll
    public void beforeAll() {

    }

    /**
     * Get a list of two profiles to use when testing the GET /profiles endpoint
     * @return List containing two Profiles with names "Bobby B Brown" and "Bamboo Brown"
     */
    private List<Profile> getTestProfileList() {
    	// Fullname is "Bobby B Brown"
    	Profile profile1 = new Profile(new User(1), "Bobby", "Brown", LocalDate.now(), Gender.NON_BINARY);
    	profile1.setMiddleName("B");

    	// Fullname is "Bamboo Brown"
    	Profile profile2 = new Profile(new User(2), "Bamboo", "Brown", LocalDate.now(), Gender.NON_BINARY);

    	List<Profile> profileList = new ArrayList<Profile>();
    	profileList.add(profile1);
    	profileList.add(profile2);

    	return profileList;
    }

    /**
     * Create a list of email objects to use when testing GET /profiles by email
     * @param user1 the first user to associate with an email
     * @param user2 the second user to associate with an email
     * @return List of Email objects containing the emails: "a@a.com", "b@b.com", "babble@b.com", "babble@b.co.nz", "babble@b.net"
     */
    private List<Email> getTestEmailList(User user1, User user2) {
    	Email email1 = new Email(user1, "a@a.com", false);
    	Email email2 = new Email(user2, "b@b.com", false);
    	Email email3 = new Email(user1, "babble@b.com", false);
    	Email email4 = new Email(user1, "babble@b.co.nz", false);
    	Email email5 = new Email(user2, "babble@b.co.au", false);

    	List<Email> emailList = new ArrayList<Email>();
    	emailList.add(email1);
    	emailList.add(email2);
    	emailList.add(email3);
    	emailList.add(email4);
    	emailList.add(email5);

    	return emailList;
    }


    private String activityListToSearchString(List<String> activities) {
        StringBuilder searchString = new StringBuilder();
        for (String activity : activities) {
            searchString.append(activity).append(" ");
        }
        searchString.deleteCharAt(searchString.length() - 1);
        return searchString.toString();
    }

    // Helper function to get the list of users from the JSON returned when searching for them using GET /profiles with query parameters
    private ArrayList<LinkedHashMap<String, Object>> getResultListJson(String raw) throws org.apache.tomcat.util.json.ParseException {
        JSONParser parser = new JSONParser(raw);
        ArrayList<LinkedHashMap<String, Object>> json = null;
        json = (ArrayList<LinkedHashMap<String, Object>>) parser.parse();
        return json;
    }


    @Test
    public void testGetSingleUserByActivityType(String activityTypeName) throws Exception {
        Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.EPOCH, Gender.NON_BINARY);
        profile1.setActivityTypes(Collections.singletonList(new ActivityType(activityTypeName)));

        Mockito.when(profileRepository.findByActivityTypesContainsAnyOf(Collections.singleton(activityTypeName))).thenReturn(Arrays.asList(profile1));
        Mockito.when(profileRepository.findByActivityTypesContainsAllOf(Collections.singleton(activityTypeName))).thenReturn(Arrays.asList(profile1));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", activityTypeName)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());

        assertEquals(1, body.size());
        LinkedHashMap<String, Object> userFound = (LinkedHashMap<String, Object>) body.get(0).get("user");
        assertEquals(BigInteger.valueOf(profile1.getUser().getUserId()), userFound.get("user_id"));
        assertEquals("what should go here?", userFound.get("activities"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Walking Running",
            "Running Scootering",
            "Scootering Biking"
    })
    public void testSingleUserActivityTypeSearchOrMethod(String spaceSeparatedActivityTypes) throws Exception {
        List<String> profileInterests = Arrays.asList("Running", "Scootering");
        Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.EPOCH, Gender.NON_BINARY);
        List<ActivityType> activityTypes = profileInterests.stream().map(ActivityType::new).collect(Collectors.toList());
        profile1.setActivityTypes(activityTypes);

        List<String> activityTypeNames = Arrays.asList(spaceSeparatedActivityTypes.split(" "));
        Mockito.when(profileRepository.findByActivityTypesContainsAnyOf(activityTypeNames)).thenReturn(Arrays.asList(profile1));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", spaceSeparatedActivityTypes)
                .queryParam("method", "or")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        assertEquals(1, body.size());
        LinkedHashMap<String, Object> userFound = (LinkedHashMap<String, Object>) body.get(0).get("user");

        assertEquals(BigInteger.valueOf(profile1.getUser().getUserId()), userFound.get("user_id"));
        assertEquals(profileInterests, userFound.get("activities"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Walking Running",
            "Running Scootering",
            "Scootering Biking",
            "Walking Biking Running",
            "Walking Scootering Running Biking"
    })
    public void testSingleUserActivityTypeSearchAndMethod(String spaceSeparatedActivityTypes) throws Exception {
        List<String> profileInterests = Arrays.asList("Walking", "Running", "Scootering", "Biking");
        Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.EPOCH, Gender.NON_BINARY);
        List<ActivityType> activityTypes = profileInterests.stream().map(ActivityType::new).collect(Collectors.toList());
        profile1.setActivityTypes(activityTypes);

        List<String> activityTypeNames = Arrays.asList(spaceSeparatedActivityTypes.split(" "));
        Mockito.when(profileRepository.findByActivityTypesContainsAllOf(profileInterests)).thenReturn(Arrays.asList(profile1));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", spaceSeparatedActivityTypes)
                .queryParam("method", "and")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        assertEquals(1, body.size());
        LinkedHashMap<String, Object> userFound = (LinkedHashMap<String, Object>) body.get(0).get("user");

        assertEquals(BigInteger.valueOf(profile1.getUser().getUserId()), userFound.get("user_id"));
        assertEquals(profileInterests, userFound.get("activities"));
    }

}