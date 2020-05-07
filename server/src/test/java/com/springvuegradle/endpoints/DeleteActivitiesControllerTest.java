package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
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
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeleteActivitiesControllerTest {

    @InjectMocks
    private ActivitiesController activitiesController;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeAll
    void beforeAll(){
        activitiesController = new ActivitiesController();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testDeleteActivity() throws Exception {
        //Mock user
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));

        //Mock activity
        Activity activity = new Activity();
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1l);

        ResponseEntity<Object> response = activitiesController.deleteActivity(1L, 2L, request);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testDeleteActivityAsAdmin() throws Exception{
        //Mock user
        User self = new User(1L);
        self.setPermissionLevel(126);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));

        //Mock activity
        Activity activity = new Activity();
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        ResponseEntity<Object> response = activitiesController.deleteActivity(3L, 2L, request);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testDeleteActivityFromOtherUser() throws Exception{
        User self = new User(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(self));

        //Mock activity
        Activity activity = new Activity();
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));

        //mock request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        assertThrows(UserNotAuthenticatedException.class,() -> {
            activitiesController.deleteActivity(3L, 2L, request);
        });
    }
}