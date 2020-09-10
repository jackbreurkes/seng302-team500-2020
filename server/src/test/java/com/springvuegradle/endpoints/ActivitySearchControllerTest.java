package com.springvuegradle.endpoints;

import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserProfileController.class})
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivitySearchControllerTest {

    @InjectMocks
    ActivitySearchController activitySearchController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailRepository emailRepository;

    @MockBean
    private ProfileRepository profileRepository;

    @MockBean
    private CountryRepository countryRepository;

    @MockBean
    private SessionRepository sessionRepository;

    @Autowired
    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private ActivityTypeRepository activityTypeRepository;

    @MockBean
    private LocationRepository locationRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private ChangeLogRepository changeLogRepository;


    @Autowired
    private MockMvc mvc;

    private HashSet testActivityTypes;
    private Profile profile;
    private User user;

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        activitySearchController = new ActivitySearchController();
        MockitoAnnotations.initMocks(this);
        user = new User(1);
        Mockito.when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        profile = new Profile(user, "Bob","Builder", LocalDate.of(2000, 10, 15), Gender.MALE);
        Mockito.when(profileRepository.findById(user.getUserId())).thenReturn(Optional.of(profile));
        this.testActivityTypes = new HashSet<>();
        this.testActivityTypes.add(new ActivityType("Running"));
    }

    @Test
    @Ignore
    @Disabled
    public void testGetActivityByPartialSearch_200() throws Exception {
        User creator = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(creator));

        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        List<Activity> activityList = new ArrayList<>();

        Activity activity = new Activity("test search",false,"REe",new Profile(creator,"creator","man",null, Gender.FEMALE),activitySet);
        activityList.add(activity);
        Mockito.when(activityRepository.findActivitiesByActivityNameContaining("test")).thenReturn(activityList);
        String json = "{\n" +
                "   \"searchTerms\": [\"test\"]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .get("/activities/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .requestAttr("authenticatedid", creator.getUserId())
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @Ignore
    @Disabled
    public void testGetActivityByPartialSearch_404() throws Exception {
        User creator = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(creator));

        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        List<Activity> activityList = new ArrayList<>();

        Activity activity = new Activity("dontfindme",false,"REe",new Profile(creator,"creator","man",null, Gender.FEMALE),activitySet);
        activityList.add(activity);
        Mockito.when(activityRepository.findActivitiesByActivityNameContaining("test")).thenReturn(activityList);
        String json = "{\n" +
                "   \"searchTerms\": [\"test\"]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .get("/activities/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .requestAttr("authenticatedid", creator.getUserId())
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Ignore
    @Disabled
    public void testGetActivityByPartialSearch_400() throws Exception {
        User creator = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(creator));

        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        List<Activity> activityList = new ArrayList<>();

        Activity activity = new Activity("dontfindme",false,"REe",new Profile(creator,"creator","man",null, Gender.FEMALE),activitySet);
        activityList.add(activity);
        Mockito.when(activityRepository.findActivitiesByActivityNameContaining("test")).thenReturn(activityList);
        String json = "{\n" +
                "   \"searchTerms\": []\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .get("/activities/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .requestAttr("authenticatedid", creator.getUserId())
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    @Ignore
    @Disabled
    public void testGetMultipleActivitiesByPartialSearch_200() throws Exception {
        User creator = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(creator));

        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        List<Activity> activityList = new ArrayList<>();

        Activity activity = new Activity("testActivity 1",false,"REe",new Profile(creator,"creator","man",null, Gender.FEMALE),activitySet);
        Activity activity1 = new Activity("testActivity 2",false,"REe",new Profile(creator,"creator","man",null, Gender.FEMALE),activitySet);
        activityList.add(activity);
        activityList.add(activity1);
        Mockito.when(activityRepository.findActivitiesByActivityNameContaining("testActivity")).thenReturn(activityList);
        String json = "{\n" +
                "   \"searchTerms\": [\"testActivity\"]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .get("/activities/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .requestAttr("authenticatedid", creator.getUserId())
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}

