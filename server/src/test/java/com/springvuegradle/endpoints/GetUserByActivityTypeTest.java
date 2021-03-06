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

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserProfileController.class})
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetUserByActivityTypeTest {

    private MockMvc mvc;

    // all mocks below are required for injection into userProfileController
    // ################ every single repository used by userProfileController is required #############
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
    @MockBean
    private SessionRepository sessionRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private ChangeLogRepository changeLogRepository;

    /**
     * Creates the edit password controller and inserts the mocks we define in the place of the repositories
     */
    @InjectMocks
    UserProfileController userProfileController;

    Optional<ActivityType> mockOptionalActivityType;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(userProfileController)
                .setControllerAdvice(new ExceptionHandlerController()) // allows us to use our ExceptionHandlerController with MockMvc
                .build();
        mockOptionalActivityType = Optional.of(Mockito.mock(ActivityType.class));
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

        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(Mockito.anyString())).thenReturn(mockOptionalActivityType);
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

        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(Mockito.anyString())).thenReturn(mockOptionalActivityType);
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

        LinkedHashMap<String, Object> userFound = body.get(0);
        assertEquals(BigInteger.valueOf(profile1.getUser().getUserId()), userFound.get("profile_id"));
    }

    @Test
    public void testActivityTypeSearchOrMethod_NoResults() throws Exception {
        List<String> activityTypeNames = Arrays.asList("Running", "Scootering");

        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(Mockito.anyString())).thenReturn(mockOptionalActivityType);
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
    public void testActivityTypeSearch_AndMethod_NoResults() throws Exception {
        List<String> activityTypeNames = Arrays.asList("Running", "Scootering");

        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(Mockito.anyString())).thenReturn(mockOptionalActivityType);
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

        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(Mockito.anyString())).thenReturn(mockOptionalActivityType);
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

        LinkedHashMap<String, Object> userFound = body.get(0);
        assertEquals(BigInteger.valueOf(profile1.getUser().getUserId()), userFound.get("profile_id"));
    }

    @Test
    public void testActivityTypeSearch_AndMethod_WithResults() throws Exception {
        List<String> activityTypeNames = Arrays.asList("Running", "Scootering");
        Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.EPOCH, Gender.NON_BINARY);
        List<ActivityType> activityTypes = activityTypeNames.stream().map(ActivityType::new).collect(Collectors.toList());
        profile1.setActivityTypes(activityTypes);

        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(Mockito.anyString())).thenReturn(mockOptionalActivityType);
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
        assertEquals(1, body.size());

        LinkedHashMap<String, Object> userFound = body.get(0);
        assertEquals(BigInteger.valueOf(profile1.getUser().getUserId()), userFound.get("profile_id"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"or", "and"})
    public void testActivityTypeSearch_EmptySearchString_Returns400(String method) throws Exception {
        String activityTypeName = "";

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", activityTypeName)
                .queryParam("method", method)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error").value("activity search string cannot be empty"))
                .andReturn();
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
                .queryParam("activity", "any multi word search string")
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
            "and",
            "or",
            "",
            "nand",
            "xor",
            "garbage",
            "o r",
            "and;",
            "or; and"
    })
    public void testActivityTypeSearch_SingleTermWithMethodParam_IgnoresMethodAndSucceeds(String method) throws Exception {
        String activityTypeName = "Walking";

        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(Mockito.anyString())).thenReturn(mockOptionalActivityType);
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
        assertEquals("a 'method' param must be supplied when searching by multiple activity types", body.get("error"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Fake",
            "Walking Fake",
            "Walking Fake Nonexistent"
    })
    public void testActivityTypeSearch_SingleNonexistentActivityNoMethod_Returns404() throws Exception {
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(("Fake"))).thenReturn(Optional.empty());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", "Fake")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.error").value("an activity type named 'Fake' does not exist"))
                .andReturn();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Walking Fake",
            "Walking Fake Nonexistent"
    })
    public void testActivityTypeSearch_NonexistentActivityTypes_Returns404(String activityTypeNames) throws Exception {
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(("Walking"))).thenReturn(
                Optional.of(new ActivityType("Walking"))
        );
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(("Fake"))).thenReturn(Optional.empty());
        Mockito.when(activityTypeRepository.getActivityTypeByActivityTypeName(("Nonexistent"))).thenReturn(Optional.empty());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("activity", activityTypeNames)
                .queryParam("method", "or")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.error").value("an activity type named 'Fake' does not exist"))
                .andReturn();
    }

}