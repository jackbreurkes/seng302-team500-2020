package com.springvuegradle.endpoints;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.repository.*;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
        this.testActivityTypes = new HashSet<>();
        this.testActivityTypes.add(new ActivityType("Running"));
    }

    @Test
    @Ignore
    public void testGetUserByPartOfFirstEmailSegment() throws Exception {

        String json = "{\n" +
                "  \"searchTerms\": [\"bab\"]\n" +
                "}";

        Activity activity = new Activity("bab", false, "simp city", null, testActivityTypes);
        List<Activity> activities = new ArrayList<>();
        activities.add(activity);
        Mockito.when(activityRepository.findActivitiesByActivityNameContaining("bab")).thenReturn(activities);

        mvc.perform(MockMvcRequestBuilders
                .get("/activities")
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
