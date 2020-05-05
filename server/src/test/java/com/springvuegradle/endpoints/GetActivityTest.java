package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.responses.ActivityResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetActivityTest {

    @InjectMocks
    private ActivitiesController activitiesController;

    @Mock
    private ActivityRepository activityRepository;

    private Profile profile;

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
        profile = new Profile(new User(1L), "David", "Clarke", LocalDate.now(), Gender.FEMALE);
    }

    @Test
    void testAuthorisedUser() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        Activity activity = new Activity("Test", false, "Dunedin", profile, new ArrayList<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.of(activity));
        ActivityResponse response = activitiesController.getActivity(1L,2L, request);
        assertEquals(response.getActivityName(), "Test");
    }


    @Test
    void testUnauthorisedUser(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(UserNotAuthenticatedException.class, () -> {
            activitiesController.getActivity(2L, 2L, request);
        });
    }

    @Test
    void testActivityDoesntExist(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);
        Mockito.when(activityRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
           activitiesController.getActivity(1L, 2L, request);
        });
    }

}
