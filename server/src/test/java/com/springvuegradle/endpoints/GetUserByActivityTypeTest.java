package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.ExceptionHandlerController;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import org.apache.tomcat.util.json.JSONParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

    private MockMvc mvc;

    // all mocks below are required for injection into userProfileController
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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(userProfileController)
                .setControllerAdvice(new ExceptionHandlerController()) // allows us to use our ExceptionHandlerController with MockMvc
                .build();
    }

    // Helper function to get the list of users from the JSON returned when searching for them using GET /profiles with query parameters
    private ArrayList<LinkedHashMap<String, Object>> getResultListJson(String raw) throws org.apache.tomcat.util.json.ParseException {
        JSONParser parser = new JSONParser(raw);
        ArrayList<LinkedHashMap<String, Object>> json = (ArrayList<LinkedHashMap<String, Object>>) parser.parse();
        return json;
    }

    @Test
    public void testSingleActivityTypeSearch_NoResults() throws Exception {
        String activityTypeName = "Walking";
        Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.EPOCH, Gender.NON_BINARY);
        profile1.setActivityTypes(Collections.singletonList(new ActivityType(activityTypeName)));

        Mockito.when(profileRepository.findByActivityTypesContainsAnyOf(Collections.singleton(activityTypeName))).thenReturn(new ArrayList<>());
        Mockito.when(profileRepository.findByActivityTypesContainsAllOf(Collections.singleton(activityTypeName))).thenReturn((new ArrayList<>()));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", activityTypeName)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());

        assertEquals(0, body.size());
    }

    @Test
    public void testSingleActivityTypeSearch_WithResults() throws Exception {
        String activityTypeName = "Walking";
        Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.EPOCH, Gender.NON_BINARY);
        profile1.setActivityTypes(Collections.singletonList(new ActivityType(activityTypeName)));

        Mockito.when(profileRepository.findByActivityTypesContainsAnyOf(Arrays.asList(activityTypeName))).thenReturn(Arrays.asList(profile1));
        Mockito.when(profileRepository.findByActivityTypesContainsAllOf(Arrays.asList(activityTypeName))).thenReturn(Arrays.asList(profile1));

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
    }

    @Test
    public void testActivityTypeSearchOrMethod_NoResults() throws Exception {
        List<String> activityTypeNames = Arrays.asList("Running", "Scootering");

        Mockito.when(profileRepository.findByActivityTypesContainsAnyOf(activityTypeNames)).thenReturn(new ArrayList<>());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", String.join(" ", activityTypeNames))
                .queryParam("method", "or")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());

        assertEquals(0, body.size());
    }

    @Test
    public void testActivityTypeSearchAndMethodNoResults() throws Exception {
        List<String> activityTypeNames = Arrays.asList("Running", "Scootering");

        Mockito.when(profileRepository.findByActivityTypesContainsAllOf(activityTypeNames)).thenReturn(new ArrayList<>());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", String.join(" ", activityTypeNames))
                .queryParam("method", "and")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());

        assertEquals(0, body.size());
    }

    @Test
    public void testActivityTypeSearch_OrMethod_WithResults() throws Exception {
        List<String> activityTypeNames = Arrays.asList("Running", "Scootering");
        Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.EPOCH, Gender.NON_BINARY);
        List<ActivityType> activityTypes = activityTypeNames.stream().map(ActivityType::new).collect(Collectors.toList());
        profile1.setActivityTypes(activityTypes);

        Mockito.when(profileRepository.findByActivityTypesContainsAnyOf(activityTypeNames)).thenReturn(Arrays.asList(profile1));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", String.join(" ", activityTypeNames))
                .queryParam("method", "or")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        assertEquals(1, body.size());

        LinkedHashMap<String, Object> userFound = (LinkedHashMap<String, Object>) body.get(0).get("user");
        assertEquals(BigInteger.valueOf(profile1.getUser().getUserId()), userFound.get("user_id"));
    }

    @Test
    public void testActivityTypeSearch_AndMethod_WithResults() throws Exception {
        List<String> activityTypeNames = Arrays.asList("Running", "Scootering");
        Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.EPOCH, Gender.NON_BINARY);
        List<ActivityType> activityTypes = activityTypeNames.stream().map(ActivityType::new).collect(Collectors.toList());
        profile1.setActivityTypes(activityTypes);

        Mockito.when(profileRepository.findByActivityTypesContainsAllOf(activityTypeNames)).thenReturn(Arrays.asList(profile1));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", String.join(" ", activityTypeNames))
                .queryParam("method", "and")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> userFound = (LinkedHashMap<String, Object>) body.get(0).get("user");

        assertEquals(1, body.size());
        assertEquals(BigInteger.valueOf(profile1.getUser().getUserId()), userFound.get("user_id"));
    }

    @Test
    public void testActivityTypeSearch_EmptySearchString_Returns400() throws Exception {
        String activityTypeName = "";

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", activityTypeName)
                .queryParam("method", "or")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn();

        JSONParser parser = new JSONParser(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) parser.parse();
        assertEquals("activity search string cannot be empty", body.get("error"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "nand",
            "xor",
            "garbage",
            "o r",
            "and;",
            "or; and"
    })
    public void testActivityTypeSearch_InvalidMethod_Returns400(String method) throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", "Walking Running")
                .queryParam("method", method)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn();

        JSONParser parser = new JSONParser(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) parser.parse();
        assertEquals("the method provided for activity type search must be either 'and' or 'or'", body.get("error"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "nand",
            "xor",
            "garbage",
            "o r",
            "and;",
            "or; and"
    })
    public void testActivityTypeSearch_SingleTermInvalidMethod_IgnoresMethodAndSucceeds(String method) throws Exception {
        String activityTypeName = "Walking";
        Mockito.when(profileRepository.findByActivityTypesContainsAnyOf(Collections.singleton(activityTypeName))).thenReturn(new ArrayList<>());
        Mockito.when(profileRepository.findByActivityTypesContainsAllOf(Collections.singleton(activityTypeName))).thenReturn((new ArrayList<>()));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", activityTypeName)
                .queryParam("method", method) // should be ignored
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());

        assertEquals(0, body.size());
    }

    @Test
    public void testActivityTypeSearch_MultipleTermsNoMethod_Returns400() throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", "Walking Running")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn();

        JSONParser parser = new JSONParser(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) parser.parse();
        assertEquals("a method param must be supplied when searching by multiple activity types", body.get("error"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Fake",
            "Walking Fake",
            "Walking Fake Nonexistent"
    })
    public void testActivityTypeSearch_NonexistentActivityTypes_Returns400(String activityTypeNames) throws Exception {

        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(("Walking"))).thenReturn(
                Optional.of(new ActivityType("Walking"))
        );
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(("Fake"))).thenReturn(Optional.empty());
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(("Nonexistent"))).thenReturn(Optional.empty());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", activityTypeNames)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn();

        JSONParser parser = new JSONParser(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) parser.parse();
        assertEquals("Fake is not a valid activity type", body.get("error"));
    }

}