package com.springvuegradle.endpoints;

import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.responses.ErrorResponse;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {SubscriptionController.class})
@WebMvcTest
public class SubscriptionControllerTest {

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ActivityRepository repo;

    @MockBean
    private ActivityTypeRepository activityTypeRepo;

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private ProfileRepository profileRepo;

    User user;
    User user2;
    Profile profile;
    Profile profile2;
    Activity activity;
    Map<String, ActivityType> activityTypes;
    boolean isSubscribed;


    @BeforeEach
    void beforeEach() {
        subscriptionController = new SubscriptionController();
        user = new User(1);
        Mockito.when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
        user2 = new User(2);
        Mockito.when(userRepo.findById(user2.getUserId())).thenReturn(Optional.of(user2));

        profile = new Profile(user, "Bob","Builder", LocalDate.of(2000, 10, 15), Gender.MALE);
        Mockito.when(profileRepo.findById(user.getUserId())).thenReturn(Optional.of(profile));

        profile2 = new Profile(user2, "Bob2","Builder2", LocalDate.of(2000, 10, 15), Gender.MALE);
        Mockito.when(profileRepo.findById(user2.getUserId())).thenReturn(Optional.of(profile2));

        for (String activityType : new String[] {"walking", "coding", "flying"}) {
            Mockito.when(activityTypeRepo.getActivityTypeByActivityTypeName(activityType)).thenReturn(Optional.of(new ActivityType(activityType)));
        }

        activityTypes = new HashMap<String, ActivityType>();
        for (String activityTypeName : new String[] {"kayaking", "walking", "coding", "building"}) {
            ActivityType activityType = new ActivityType(activityTypeName);
            activityTypes.put(activityTypeName, activityType);
            Mockito.when(activityTypeRepo.getActivityTypeByActivityTypeName(activityTypeName)).thenReturn(Optional.of(new ActivityType(activityTypeName)));
        }

        Set<ActivityType> activityTypeSet = new HashSet<ActivityType>();
        activityTypeSet.add(activityTypes.get("walking"));

        activity =new Activity("Running around", false, "Hagley Park", profile, activityTypeSet);
        activity.setId(1);
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(activity));
    }

    @Test
    public void testPostValidSubscription() throws Exception {
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(activity));

        this.mvc.perform(MockMvcRequestBuilders.post("/profiles/1/subscriptions/activities/1")
                .requestAttr("authenticatedid", user.getUserId())
                .header("authenticatedid", user.getUserId()))
                .andExpect(status().isCreated());
    }

    @Test
    public void testPostUnauthorizedSubscription() throws Exception {
        this.mvc.perform(post("/profiles/1/subscriptions/activities/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testSubscribeActivityDoesntExist() throws Exception{
        this.mvc.perform(post("/profiles/1/subscriptions/activities/100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetSubscriptionWhenSubscribed() throws Exception {
        isSubscribed = true;
        Mockito.when(subscriptionRepository.isSubscribedToActivity(1L, profile)).thenReturn(isSubscribed);
        mvc.perform(MockMvcRequestBuilders
                .get("/profiles/1/subscriptions/activities/1")
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("subscribed").value(true));
    }

    @Test
    public void testGetSubscriptionWhenNotSubscribed() throws Exception {
        isSubscribed = false;
        Mockito.when(subscriptionRepository.isSubscribedToActivity(1L, profile)).thenReturn(isSubscribed);
        mvc.perform(MockMvcRequestBuilders
                .get("/profiles/1/subscriptions/activities/1")
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("subscribed").value(false));
    }

    @Test
    public void testGetSubscriptionWhenOtherUserSubscribed() throws Exception {
        isSubscribed = true;
        Mockito.when(subscriptionRepository.isSubscribedToActivity(1L, profile2)).thenReturn(isSubscribed);
        mvc.perform(MockMvcRequestBuilders
                .get("/profiles/1/subscriptions/activities/1")
                .requestAttr("authenticatedid", user2.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("subscribed").value(true));
    }

    @Test
    public void testGetSubscriptionWhenOtherUserNotSubscribed() throws Exception {
        isSubscribed = false;
        Mockito.when(subscriptionRepository.isSubscribedToActivity(1L, profile2)).thenReturn(isSubscribed);
        mvc.perform(MockMvcRequestBuilders
                .get("/profiles/1/subscriptions/activities/1")
                .requestAttr("authenticatedid", user2.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("subscribed").value(false));
    }

    @Ignore
    @Test
    public void testDeleteSubscriptionWhenNotSubscirbed() throws Exception {

    }

    @Ignore
    @Test
    public void testDeleteSubscriptionNotAuthneticated() throws Exception {

    }

    @Ignore
    @Test
    public void testDeleteSubscription(){

    }
}
