package com.springvuegradle.endpoints;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityRole;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.data.UserActivityRole;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        //Mock admin
        User admin = new User(100L);
        self.setPermissionLevel(126);
        Mockito.when(userRepository.findById(100L)).thenReturn(Optional.of(self));

        //Mock activity
        Activity activity = new Activity();
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
        self.setPermissionLevel(126);
        Mockito.when(userRepository.findById(100L)).thenReturn(Optional.of(self));

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
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));
        //Mock admin
        User organiser = new User(100L);
        Mockito.when(userRepository.findById(100L)).thenReturn(Optional.of(self));


        //Mock activity
        Activity activity = new Activity();
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

}
