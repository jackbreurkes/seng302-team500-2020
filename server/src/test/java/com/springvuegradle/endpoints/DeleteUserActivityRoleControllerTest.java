package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.repository.UserRepository;
import org.junit.Before;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeleteUserActivityRoleControllerTest {
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
    void testDeleteUserActivityRoleAsCreator() throws Exception {
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));

        //Mock Creator
        User creator = new User(65L);
        Mockito.when(userRepository.findById(65L)).thenReturn(Optional.of(creator));

        //Mock activity
        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        Profile profile = new Profile(creator,"Misha","Josh", null, Gender.MALE);
        Activity activity = new Activity("testActivity", false, "testlocation", profile, activitySet);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        // Mock userActivityRole
        UserActivityRole userActivityRole = new UserActivityRole();
        userActivityRole.setUser(self);
        userActivityRole.setActivity(activity);
        userActivityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L,2L)).thenReturn(Optional.of(userActivityRole));

        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 65L);

        ResponseEntity<Object> response = userActivityRoleController.deleteUserActivityRole(2L, 1L, request);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testDeleteUserActivityRoleAsAdmin() throws Exception {
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));
        //Mock admin
        User admin = new User(100L);
        self.setPermissionLevel(126);
        Mockito.when(userRepository.findById(100L)).thenReturn(Optional.of(self));

        //Mock Creator
        User creator = new User(65L);
        Mockito.when(userRepository.findById(65L)).thenReturn(Optional.of(creator));

        //Mock activity
        ActivityType activityType = new ActivityType("Running");
        Set<ActivityType> activitySet = new HashSet<ActivityType>();
        activitySet.add(activityType);
        Profile profile = new Profile(creator,"Misha","Josh", null, Gender.MALE);
        Activity activity = new Activity("testActivity", false, "testlocation", profile, activitySet);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        // Mock userActivityRole
        UserActivityRole userActivityRole = new UserActivityRole();
        userActivityRole.setUser(self);
        userActivityRole.setActivity(activity);
        userActivityRole.setActivityRole(ActivityRole.PARTICIPANT);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(1L,2L)).thenReturn(Optional.of(userActivityRole));

        //mock request (as logged in as admin)
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 100L);

        ResponseEntity<Object> response = userActivityRoleController.deleteUserActivityRole(2L, 1L, request);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testDeleteUserActivityRoleAsOtherUser() throws Exception {
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));
        //Mock admin
        User user1 = new User(2L);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(self));

        //Mock Creator
        User creator = new User(65L);
        Mockito.when(userRepository.findById(65L)).thenReturn(Optional.of(creator));

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

        //mock request (as logged in as admin)
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 2L);

        assertThrows(UserNotAuthorizedException.class,() -> {
            userActivityRoleController.deleteUserActivityRole(2L, 1L, request);
        });
    }
}
