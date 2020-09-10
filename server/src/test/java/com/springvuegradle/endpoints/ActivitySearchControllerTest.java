package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.requests.SearchActivityRequest;
import com.springvuegradle.model.responses.ActivityResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivitySearchControllerTest {
    @InjectMocks
    private ActivitySearchController activitySearchController;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ProfileRepository profileRepository;

    private Profile profile;

    @BeforeAll
    public void setUp(){
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
        profile = new Profile(new User(1L), "David", "Clarke", LocalDate.now(), Gender.FEMALE);
    }
    @Test
    void searchActivity_200() throws UserNotAuthenticatedException, RecordNotFoundException, InvalidRequestFieldException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        SearchActivityRequest s = new SearchActivityRequest();
        s.setSearchedTerms(new ArrayList<>(Arrays.asList("Test")));

        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        activity.setId(2L);

        Mockito.when(activityRepository.findActivitiesByActivityNameContaining("Test")).thenReturn(new ArrayList<Activity>(Arrays.asList(activity)));
        Mockito.when(profileRepository.getOne(3l)).thenReturn(profile);

        List<ActivityResponse> response = activitySearchController.searchActivities(s, request);

        assertEquals(1, response.size(), "There should be one search result");
    }


    @Test
    void searchActivityNotAuthenticated() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        SearchActivityRequest s = new SearchActivityRequest();
        s.setSearchedTerms(new ArrayList<>(Arrays.asList("Test")));

        Activity activity = new Activity("Test", false, "Dunedin", profile, new HashSet<ActivityType>(Arrays.asList(new ActivityType("Swimming"))));
        activity.setId(2L);

        Mockito.when(activityRepository.findActivitiesByActivityNameContaining("Test")).thenReturn(new ArrayList<Activity>(Arrays.asList(activity)));
        Mockito.when(profileRepository.getOne(3l)).thenReturn(profile);

        assertThrows(UserNotAuthenticatedException.class, () -> {
            activitySearchController.searchActivities(s, request);
        });
    }

    @Test
    void searchActivityNoResults_404() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        SearchActivityRequest s = new SearchActivityRequest();
        s.setSearchedTerms(new ArrayList<>(Arrays.asList("Test")));

        Mockito.when(activityRepository.findActivitiesByActivityNameContaining("Test")).thenReturn(new ArrayList<Activity>());
        Mockito.when(profileRepository.getOne(3l)).thenReturn(profile);

        assertThrows(RecordNotFoundException.class, () -> {
            activitySearchController.searchActivities(s, request);
        });
    }

    @Test
    void searchBadRequest_400() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("authenticatedid", 1L);

        SearchActivityRequest s = new SearchActivityRequest();
        s.setSearchedTerms(new ArrayList<>(Arrays.asList()));

        Mockito.when(activityRepository.findActivitiesByActivityNameContaining("Test")).thenReturn(new ArrayList<Activity>());
        Mockito.when(profileRepository.getOne(3l)).thenReturn(profile);

        assertThrows(InvalidRequestFieldException.class, () -> {
            activitySearchController.searchActivities(s, request);
        });
    }
}

