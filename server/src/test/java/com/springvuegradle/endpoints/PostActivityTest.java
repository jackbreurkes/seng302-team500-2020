package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.repository.ActivityOutcomeRepository;
import com.springvuegradle.model.repository.ActivityParticipantResultRepository;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.CreateActivityRequest;
import com.springvuegradle.model.responses.ProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebMvcTest(ActivitiesController.class)
@Disabled
class PostActivityTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProfileRepository profileRepository;

    @MockBean
    private ActivityTypeRepository activityTypeRepository;
    
    @MockBean
	private ActivityParticipantResultRepository activityOutcomeRepo;
    
    @MockBean
    private ActivityOutcomeRepository activityOutcomeRepository;


    private ActivitiesController activitiesController;
    private CreateActivityRequest createActivityRequest;

    @BeforeEach
    public void beforeEach() {
        activitiesController = new ActivitiesController();

        createActivityRequest = new CreateActivityRequest();
        createActivityRequest.setActivityName("Kaikoura Coast Track race");
        createActivityRequest.setDescription("A big and nice race on a lovely peninsula");
        createActivityRequest.setActivityTypes(Arrays.asList("Walking", "Running"));
        createActivityRequest.setContinuous(false);
        createActivityRequest.setStartTime("2020-02-20T08:00:00+1300");
        createActivityRequest.setEndTime("2020-02-20T08:00:00+1300");
        createActivityRequest.setLocation("Kaikoura, NZ");
    }

    @Test
    public void testExample() throws Exception {
        this.mvc.perform(post("/profiles/1/activities", createActivityRequest).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//                .andExpect(content().)
    }


}