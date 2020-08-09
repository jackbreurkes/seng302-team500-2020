package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdateUserActivityRoleRequest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PutUserActivityRoleControllerTest {
    @InjectMocks
    private UserActivityRoleController userActivityRoleController;

    @Mock
    private UserActivityRoleRepository userActivityRoleRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeAll
    void beforeAll(){
        userActivityRoleController = new UserActivityRoleController();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testUpdateUserActivityRoleAsAdmin() throws Exception {
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

        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        UserActivityRole userActivityRole = new UserActivityRole();
        userActivityRole.setUser(self);
        userActivityRole.setActivity(activity);
        userActivityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L,2L)).thenReturn(Optional.of(userActivityRole));

        // Mock update request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 100L);

        UpdateUserActivityRoleRequest updateUserActivityRoleRequest = new UpdateUserActivityRoleRequest();
        updateUserActivityRoleRequest.setRole(ActivityRole.ORGANISER);


        ResponseEntity<Object> response = userActivityRoleController.setUserActivityRole(2L, 1L,updateUserActivityRoleRequest, request);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testCreateUserActivityRoleAsAdmin() throws Exception {
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));
        //Mock admin
        User admin = new User(100L);
        admin.setPermissionLevel(126);
        Mockito.when(userRepository.findById(100L)).thenReturn(Optional.of(admin));

        //Mock activity
        Activity activity = new Activity();
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        // Mock create request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 100L);

        UpdateUserActivityRoleRequest updateUserActivityRoleRequest = new UpdateUserActivityRoleRequest();
        updateUserActivityRoleRequest.setRole(ActivityRole.ORGANISER);

        ResponseEntity<Object> response = userActivityRoleController.setUserActivityRole(2L, 1L,updateUserActivityRoleRequest, request);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testUpdateUserActivityRoleAsOrganiser() throws Exception {
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
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        // Set organiser
        UserActivityRole userActivityRoleOrg = new UserActivityRole();
        userActivityRoleOrg.setUser(organiser);
        userActivityRoleOrg.setActivity(activity);
        userActivityRoleOrg.setActivityRole(ActivityRole.ORGANISER);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(100L,2L)).thenReturn(Optional.of(userActivityRoleOrg));

        UserActivityRole userActivityRole = new UserActivityRole();
        userActivityRole.setUser(self);
        userActivityRole.setActivity(activity);
        userActivityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L,2L)).thenReturn(Optional.of(userActivityRole));

        // Mock update request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 100L);

        UpdateUserActivityRoleRequest updateUserActivityRoleRequest = new UpdateUserActivityRoleRequest();
        updateUserActivityRoleRequest.setRole(ActivityRole.ORGANISER);

        ResponseEntity<Object> response = userActivityRoleController.setUserActivityRole(2L, 1L,updateUserActivityRoleRequest, request);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testUpdateUserActivityRoleAsCreator() throws Exception {
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
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));


        UserActivityRole userActivityRole = new UserActivityRole();
        userActivityRole.setUser(self);
        userActivityRole.setActivity(activity);
        userActivityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L,2L)).thenReturn(Optional.of(userActivityRole));

        // Mock update request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 65L);

        UpdateUserActivityRoleRequest updateUserActivityRoleRequest = new UpdateUserActivityRoleRequest();
        updateUserActivityRoleRequest.setRole(ActivityRole.ORGANISER);

        ResponseEntity<Object> response = userActivityRoleController.setUserActivityRole(2L, 1L,updateUserActivityRoleRequest, request);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testUpdateUserActivityRoleAsUnauthorizedUser() throws Exception {
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
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        UserActivityRole userActivityRole = new UserActivityRole();
        userActivityRole.setUser(self);
        userActivityRole.setActivity(activity);
        userActivityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L,2L)).thenReturn(Optional.of(userActivityRole));

        // Mock update request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 6L);

        UpdateUserActivityRoleRequest updateUserActivityRoleRequest = new UpdateUserActivityRoleRequest();
        updateUserActivityRoleRequest.setRole(ActivityRole.ORGANISER);

        assertThrows(UserNotAuthorizedException.class, () -> {
            ResponseEntity<Object> response = userActivityRoleController.setUserActivityRole(2L, 1L,updateUserActivityRoleRequest, request);
        });
    }

    @Test
    void testCreateUserActivityRoleAsCreator() throws Exception {
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
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));


        // Mock create request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 65L);

        UpdateUserActivityRoleRequest updateUserActivityRoleRequest = new UpdateUserActivityRoleRequest();
        updateUserActivityRoleRequest.setRole(ActivityRole.ORGANISER);

        ResponseEntity<Object> response = userActivityRoleController.setUserActivityRole(2L, 1L,updateUserActivityRoleRequest, request);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    void testCreateUserActivityRoleAsOrganiser() throws Exception {
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
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        // Set organiser
        UserActivityRole userActivityRoleOrg = new UserActivityRole();
        userActivityRoleOrg.setUser(organiser);
        userActivityRoleOrg.setActivity(activity);
        userActivityRoleOrg.setActivityRole(ActivityRole.ORGANISER);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(100L,2L)).thenReturn(Optional.of(userActivityRoleOrg));

        // Mock create request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 100L);

        UpdateUserActivityRoleRequest updateUserActivityRoleRequest = new UpdateUserActivityRoleRequest();
        updateUserActivityRoleRequest.setRole(ActivityRole.ORGANISER);

        ResponseEntity<Object> response = userActivityRoleController.setUserActivityRole(2L, 1L,updateUserActivityRoleRequest, request);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    void testCreateUserActivityRoleAsParticipant() throws Exception {
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
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        // Set organiser
        UserActivityRole userActivityRoleOrg = new UserActivityRole();
        userActivityRoleOrg.setUser(participant);
        userActivityRoleOrg.setActivity(activity);
        userActivityRoleOrg.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(100L,2L)).thenReturn(Optional.of(userActivityRoleOrg));

        // Mock create request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 100L);

        UpdateUserActivityRoleRequest updateUserActivityRoleRequest = new UpdateUserActivityRoleRequest();
        updateUserActivityRoleRequest.setRole(ActivityRole.ORGANISER);

        assertThrows(UserNotAuthorizedException.class, () -> {
            ResponseEntity<Object> response = userActivityRoleController.setUserActivityRole(2L, 1L,updateUserActivityRoleRequest, request);
        });
    }
}
