package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdateUserActivityRoleRequest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserActivityRoleController.class})
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PutUserActivityRoleControllerTest {

    @InjectMocks
    private UserActivityRoleController userActivityRoleController;

    @MockBean
    private UserActivityRoleRepository userActivityRoleRepository;

    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    void beforeAll(){
        userActivityRoleController = new UserActivityRoleController();
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void beforeEach(){
        this.userActivityRoleController = new UserActivityRoleController();
    }

    @Test
    void testUpdateUserActivityRoleAsAdmin_Success() throws Exception {
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));
        //Mock creator
        User creator = new User(2L);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(self));
        //Mock admin
        User admin = new User(100L);
        self.setPermissionLevel(126);
        Mockito.when(userRepository.findById(100L)).thenReturn(Optional.of(self));

        //Mock activity
        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        Activity activity = new Activity("hello",false,"REe",new Profile(creator,"creator","man",null, Gender.FEMALE),activitySet);
        activity.setId(2L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        UserActivityRole userActivityRole = new UserActivityRole();
        userActivityRole.setUser(self);
        userActivityRole.setActivity(activity);
        userActivityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L,2L)).thenReturn(Optional.of(userActivityRole));

        // Mock json string
        String json = "{\"role\": \"" + ActivityRole.ORGANISER + "\"}";
        mvc.perform(MockMvcRequestBuilders
                .put("/activities/" + activity.getId() + "/roles/" + self.getUserId())
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", 100L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateUserActivityRoleAsAdmin_Success() throws Exception {
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));
        //Mock admin
        User admin = new User(100L);
        admin.setPermissionLevel(126);
        Mockito.when(userRepository.findById(100L)).thenReturn(Optional.of(admin));

        //Mock activity
        Activity activity = new Activity();
        activity.setId(2L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));


        // Mock json string
        String json = "{\"role\": \"" + ActivityRole.ORGANISER + "\"}";
        mvc.perform(MockMvcRequestBuilders
                .put("/activities/" + activity.getId() + "/roles/" + self.getUserId())
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", 100L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserActivityRoleAsOrganiser_Success() throws Exception {
        //Mock Creator
        User creator = new User(65L);
        Mockito.when(userRepository.findById(65L)).thenReturn(Optional.of(creator));
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));
        //Mock organiser
        User organiser = new User(100L);
        Mockito.when(userRepository.findById(100L)).thenReturn(Optional.of(organiser));


        //Mock activity
        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        Profile profile = new Profile(creator,"Misha","Josh", null, Gender.MALE);
        Activity activity = new Activity("testActivity", false, "testlocation", profile, activitySet);
        activity.setId(2L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        // Set organiser
        UserActivityRole userActivityRoleOrg = new UserActivityRole();
        userActivityRoleOrg.setUser(organiser);
        userActivityRoleOrg.setActivity(activity);
        userActivityRoleOrg.setActivityRole(ActivityRole.ORGANISER);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(100L,2L)).thenReturn(Optional.of(userActivityRoleOrg));

        //set participant
        UserActivityRole userActivityRole = new UserActivityRole();
        userActivityRole.setUser(self);
        userActivityRole.setActivity(activity);
        userActivityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L,2L)).thenReturn(Optional.of(userActivityRole));

        // Mock json string
        String json = "{\"role\": \"" + ActivityRole.ORGANISER + "\"}";
        mvc.perform(MockMvcRequestBuilders
                .put("/activities/" + activity.getId() + "/roles/" + self.getUserId())
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", 100L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserActivityRoleAsCreator_Success() throws Exception {
        //Mock Creator
        User creator = new User(65L);
        Mockito.when(userRepository.findById(65L)).thenReturn(Optional.of(creator));
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));


        //Mock activity
        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        Profile profile = new Profile(creator,"Misha","Josh", null, Gender.MALE);
        Activity activity = new Activity("testActivity", false, "testlocation", profile, activitySet);
        activity.setId(2L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));


        UserActivityRole userActivityRole = new UserActivityRole();
        userActivityRole.setUser(self);
        userActivityRole.setActivity(activity);
        userActivityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L,2L)).thenReturn(Optional.of(userActivityRole));

        // Mock json string
        String json = "{\"role\": \"" + ActivityRole.ORGANISER + "\"}";
        mvc.perform(MockMvcRequestBuilders
                .put("/activities/" + activity.getId() + "/roles/" + self.getUserId())
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", 65L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserActivityRoleAsUnauthorizedUser_FailsForbidden() throws Exception {
        //Mock Creator
        User creator = new User(65L);
        Mockito.when(userRepository.findById(65L)).thenReturn(Optional.of(creator));
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));
        //Mock user
        User unathorizedUser = new User(6L);
        Mockito.when(userRepository.findById(6L)).thenReturn(Optional.of(unathorizedUser));

        //Mock activity
        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        Profile profile = new Profile(creator,"Misha","Josh", null, Gender.MALE);
        Activity activity = new Activity("testActivity", false, "testlocation", profile, activitySet);
        activity.setId(2L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        UserActivityRole userActivityRole = new UserActivityRole();
        userActivityRole.setUser(self);
        userActivityRole.setActivity(activity);
        userActivityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L,2L)).thenReturn(Optional.of(userActivityRole));

        // Mock json string
        String json = "{\"role\": \"" + ActivityRole.ORGANISER + "\"}";
        mvc.perform(MockMvcRequestBuilders
                .put("/activities/" + activity.getId() + "/roles/" + self.getUserId())
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", 6L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateUserActivityRoleAsCreator_Success() throws Exception {
        //Mock Creator
        User creator = new User(65L);
        Mockito.when(userRepository.findById(65L)).thenReturn(Optional.of(creator));
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));


        //Mock activity
        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        Profile profile = new Profile(creator,"Misha","Josh", null, Gender.MALE);
        Activity activity = new Activity("testActivity", false, "testlocation", profile, activitySet);
        activity.setId(2L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));



        // Mock json string
        String json = "{\"role\": \"" + ActivityRole.ORGANISER + "\"}";
        mvc.perform(MockMvcRequestBuilders
                .put("/activities/" + activity.getId() + "/roles/" + self.getUserId())
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", 65L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateUserActivityRoleAsOrganiser_Success() throws Exception {
        //Mock Creator
        User creator = new User(65L);
        Mockito.when(userRepository.findById(65L)).thenReturn(Optional.of(creator));
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));


        //Mock organiser
        User organiser = new User(100L);
        Mockito.when(userRepository.findById(100L)).thenReturn(Optional.of(organiser));


        //Mock activity
        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        Profile profile = new Profile(creator,"Misha","Josh", null, Gender.MALE);
        Activity activity = new Activity("testActivity", false, "testlocation", profile, activitySet);
        activity.setId(2L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        // Set organiser
        UserActivityRole userActivityRoleOrg = new UserActivityRole();
        userActivityRoleOrg.setUser(organiser);
        userActivityRoleOrg.setActivity(activity);
        userActivityRoleOrg.setActivityRole(ActivityRole.ORGANISER);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(100L,2L)).thenReturn(Optional.of(userActivityRoleOrg));

        // Mock json string
        String json = "{\"role\": \"" + ActivityRole.ORGANISER + "\"}";
        mvc.perform(MockMvcRequestBuilders
                .put("/activities/" + activity.getId() + "/roles/" + self.getUserId())
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", 100L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testCreateUserActivityRoleAsParticipant_FailsForbidden() throws Exception {
        //Mock Creator
        User creator = new User(65L);
        Mockito.when(userRepository.findById(65L)).thenReturn(Optional.of(creator));
        //Mock user
        User baseUser = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));

        //Mock organiser
        User participant = new User(100L);
        Mockito.when(userRepository.findById(100L)).thenReturn(Optional.of(participant));


        //Mock activity
        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        Profile profile = new Profile(creator,"Misha","Josh", null, Gender.MALE);
        Activity activity = new Activity("testActivity", false, "testlocation", profile, activitySet);
        activity.setId(2L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        // Set organiser
        UserActivityRole userActivityRoleOrg = new UserActivityRole();
        userActivityRoleOrg.setUser(participant);
        userActivityRoleOrg.setActivity(activity);
        userActivityRoleOrg.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(100L,2L)).thenReturn(Optional.of(userActivityRoleOrg));

        // Mock json string
        String json = "{\"role\": \"" + ActivityRole.ORGANISER + "\"}";
        mvc.perform(MockMvcRequestBuilders
                .put("/activities/" + activity.getId() + "/roles/" + baseUser.getUserId())
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .requestAttr("authenticatedid", 100L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }
}
