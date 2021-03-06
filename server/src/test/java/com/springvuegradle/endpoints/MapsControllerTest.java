package com.springvuegradle.endpoints;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.exceptions.ExceptionHandlerController;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.responses.ActivityPinResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {MapsController.class})
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MapsControllerTest {

	private MockMvc mvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
    @InjectMocks
    private MapsController mapsController;
    
    @MockBean
    ActivityRepository activityRepository;

    @MockBean
    ActivityPinRepository activityPinRepository;

    @MockBean
    UserActivityRoleRepository userActivityRoleRepository;

    @MockBean
    SubscriptionRepository subscriptionRepository;

    @MockBean
    ProfileRepository profileRepository;
    
    @MockBean
    UserRepository userRepository;
    
    private Profile profile;

    @BeforeAll
    public void setUp() {
        //Initialize the mocks we create
        MockitoAnnotations.initMocks(this);
        
        mvc = MockMvcBuilders.standaloneSetup(mapsController)
                .setControllerAdvice(new ExceptionHandlerController()) // allows us to use our ExceptionHandlerController with MockMvc
                .build();
    }
    
    @BeforeEach
    public void beforeEach() {
    	 profile = new Profile(new User(1L), "David", "Clarke", LocalDate.now(), Gender.FEMALE);
         Mockito.when(profileRepository.findById(profile.getUser().getUserId())).thenReturn(Optional.of(profile));
    }
    
    public ActivityPin setupActivityPinMock(long id, float lat, float lon) {
    	Activity activity = Mockito.mock(Activity.class);
    	Mockito.when(activity.getId()).thenReturn(id);
        return new ActivityPin(activity, lat, lon, 0f, 0f, 0f, 0f);
    }

    public ActivityPin setupActivityPinWithDuration(long id, float lat, float lon, String endTime){
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.getId()).thenReturn(id);
        Mockito.when(activity.isDuration()).thenReturn(true);
        Mockito.when(activity.getEndTime()).thenReturn(endTime);
        return new ActivityPin(activity, lat, lon, 0f, 0f, 0f, 0f);
    }
    
    @Test
    void testGetActivitiesInBounds_ShouldReturnOne() throws Exception {
        List<ActivityPin> pins = new ArrayList<>();
        
        ActivityPin pin1 = this.setupActivityPinMock(1, 8, 8);
        Activity activity1 = pin1.getActivity();
        Mockito.when(activity1.getCreator()).thenReturn(profile);
        pins.add(pin1);
        
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), any()))
    		.thenReturn(pins);
        
        mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", profile.getUser().getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
        
        Mockito.verify(activityPinRepository, Mockito.times(1)).findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), any());
    }

    @Test
    void testGetActivitiesInBounds_BoundsCrossDateLine_BoundsAreSplit() throws Exception {
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.anyFloat(), Mockito.anyFloat(), Mockito.anyFloat(), Mockito.anyFloat(), any()))
    		.thenReturn(new ArrayList<>());
        
        mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "100.0")
                .param("ne_lon", "-170.0")
                .param("sw_lat", "80.0")
                .param("sw_lon", "170.0")
                .requestAttr("authenticatedid", profile.getUser().getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        
        Mockito.verify(activityPinRepository, Mockito.times(1)).findPinsInBounds(Mockito.eq(100f), Mockito.eq(180f), Mockito.eq(80f), Mockito.eq(170f), any());
        Mockito.verify(activityPinRepository, Mockito.times(1)).findPinsInBounds(Mockito.eq(100f), Mockito.eq(-170f), Mockito.eq(80f), Mockito.eq(-180f), any());
    }

    @Test
    void testGetActivitiesInBounds_NotAuthenticated_401() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "100.0")
                .param("ne_lon", "-170.0")
                .param("sw_lat", "80.0")
                .param("sw_lon", "170.0")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ne_lat", "ne_lon", "sw_lat", "sw_lon"})
    void testMissingRequestParam_400(String missingParam) throws Exception {
        MultiValueMap<String, String> params = new HttpHeaders();
        params.add("ne_lat", "10.0");
        params.add("ne_lon", "10.0");
        params.add("sw_lat", "0.0");
        params.add("sw_lon", "0.0");
        params.remove(missingParam);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/maps")
                .queryParams(params)
                .requestAttr("authenticatedid", profile.getUser().getUserId())
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    Exception thrown = result.getResolvedException();
                    assertTrue(thrown instanceof InvalidRequestFieldException);
                    assertEquals(String.format("the request parameter %s must be included", missingParam), thrown.getMessage());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"10L", "fifty", "10.0.0", "58*30"})
    void testRequestParamNotParseableAsFloat_400(String badParamValue) throws Exception {
        for (String badParam : new String[]{"ne_lat", "ne_lon", "sw_lat", "sw_lon"}) {
            MultiValueMap<String, String> params = new HttpHeaders();
            params.add("ne_lat", "10.0");
            params.add("ne_lon", "10.0");
            params.add("sw_lat", "0.0");
            params.add("sw_lon", "0.0");
            params.set(badParam, badParamValue);

            MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                    .get("/maps")
                    .queryParams(params)
                    .requestAttr("authenticatedid", profile.getUser().getUserId())
                    .accept(MediaType.APPLICATION_JSON);

            mvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andDo(result -> {
                        Exception thrown = result.getResolvedException();
                        assertTrue(thrown instanceof InvalidRequestFieldException);
                        assertEquals(String.format("the request parameter %s must be a valid number", badParam), thrown.getMessage());
                    });
        }
    }

    @Test
    void testGetActivitiesInBounds_AsCreator_ActivityRoleIsCreator() throws Exception {
        List<ActivityPin> pins = new ArrayList<>();
        
        ActivityPin pin1 = this.setupActivityPinMock(1, 8, 8);
        Activity activity1 = pin1.getActivity();
        Mockito.when(activity1.getCreator()).thenReturn(profile);
        pins.add(pin1);
        
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), any()))
    		.thenReturn(pins);
        
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", profile.getUser().getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        String responseJson = result.getResponse().getContentAsString();
		List<ActivityPinResponse> response = objectMapper.readValue(responseJson, new TypeReference<List<ActivityPinResponse>>() {});
		
		assertEquals(1, response.size());
		assertEquals("creator", response.get(0).getRole());
    }
    
    @Test
    void testGetActivitiesInBounds_AsOrganiser_ActivityRoleIsOrganiser() throws Exception {
    	final User organiser = new User(2L);
    	Profile organiserProfile = new Profile(organiser, "A", "Name", LocalDate.now(), Gender.NON_BINARY);
        Mockito.when(profileRepository.findById(organiserProfile.getUser().getUserId())).thenReturn(Optional.of(organiserProfile));
        List<ActivityPin> pins = new ArrayList<>();
        
        ActivityPin pin1 = this.setupActivityPinMock(1, 8, 8);
        Activity activity1 = pin1.getActivity();
        Mockito.when(activity1.getCreator()).thenReturn(profile);
        pins.add(pin1);
        
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), any()))
    		.thenReturn(pins);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(organiser.getUserId(), activity1.getId()))
        	.thenReturn(Optional.of(new UserActivityRole(activity1, organiser, ActivityRole.ORGANISER)));
        
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", organiser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        String responseJson = result.getResponse().getContentAsString();
		List<ActivityPinResponse> response = objectMapper.readValue(responseJson, new TypeReference<List<ActivityPinResponse>>() {});
		
		assertEquals(1, response.size());
		assertEquals("organiser", response.get(0).getRole());
    }
    
    
    @Test
    void testGetActivitiesInBounds_AsParticipant_ActivityRoleIsParticipant() throws Exception {
    	final User participant = new User(2L); 
        List<ActivityPin> pins = new ArrayList<>();
        Profile participantProfile = new Profile(participant, "A", "Name", LocalDate.now(), Gender.NON_BINARY);
        Mockito.when(profileRepository.findById(participantProfile.getUser().getUserId())).thenReturn(Optional.of(participantProfile));

        ActivityPin pin1 = this.setupActivityPinMock(1, 8, 8);
        Activity activity1 = pin1.getActivity();
        Mockito.when(activity1.getCreator()).thenReturn(profile);
        pins.add(pin1);
        
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), any()))
    		.thenReturn(pins);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(participant.getUserId(), activity1.getId()))
        	.thenReturn(Optional.of(new UserActivityRole(activity1, participant, ActivityRole.PARTICIPANT)));
        
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", participant.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        String responseJson = result.getResponse().getContentAsString();
		List<ActivityPinResponse> response = objectMapper.readValue(responseJson, new TypeReference<List<ActivityPinResponse>>() {});
		
		assertEquals(1, response.size());
		assertEquals("participant", response.get(0).getRole());
    }
    
    @Test
    void testGetActivitiesInBounds_AsFollower_ActivityRoleIsFollower() throws Exception {
    	final User follower = new User(2L);
    	Profile followerProfile = new Profile(follower, "It", "DoesNotMatter", LocalDate.now(), Gender.NON_BINARY);
        List<ActivityPin> pins = new ArrayList<>();
        
        ActivityPin pin1 = this.setupActivityPinMock(1, 8, 8);
        Activity activity1 = pin1.getActivity();
        Mockito.when(activity1.getCreator()).thenReturn(profile);
        pins.add(pin1);
        
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), any()))
    		.thenReturn(pins);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(follower.getUserId(), activity1.getId()))
        	.thenReturn(Optional.empty());
        Mockito.when(profileRepository.findById(follower.getUserId())).thenReturn(Optional.of(followerProfile));
        Mockito.when(subscriptionRepository.isSubscribedToActivity(activity1.getId(), followerProfile)).thenReturn(true);
        
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", follower.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        String responseJson = result.getResponse().getContentAsString();
		List<ActivityPinResponse> response = objectMapper.readValue(responseJson, new TypeReference<List<ActivityPinResponse>>() {});
		
		assertEquals(1, response.size());
		assertEquals("follower", response.get(0).getRole());
    }
    
    @Test
    void testGetActivitiesInBounds_NoRole_ActivityRoleIsNull() throws Exception {
    	final User follower = new User(2L);
    	Profile followerProfile = new Profile(follower, "It", "DoesNotMatter", LocalDate.now(), Gender.NON_BINARY);
        List<ActivityPin> pins = new ArrayList<>();
        
        ActivityPin pin1 = this.setupActivityPinMock(1, 8, 8);
        Activity activity1 = pin1.getActivity();
        Mockito.when(activity1.getCreator()).thenReturn(profile);
        pins.add(pin1);
        
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), any()))
    		.thenReturn(pins);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(follower.getUserId(), activity1.getId()))
        	.thenReturn(Optional.empty());
        Mockito.when(profileRepository.findById(follower.getUserId())).thenReturn(Optional.of(followerProfile));
        Mockito.when(subscriptionRepository.isSubscribedToActivity(activity1.getId(), followerProfile)).thenReturn(false);
        
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", follower.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        String responseJson = result.getResponse().getContentAsString();
		List<ActivityPinResponse> response = objectMapper.readValue(responseJson, new TypeReference<List<ActivityPinResponse>>() {});
		
		assertEquals(1, response.size());
		assertNull(response.get(0).getRole());
    }
    
    
    @Test
    void testGetActivitiesInBounds_AsSuperAdmin_Returns() throws Exception {
    	final User superAdmin = new User(2L);
    	superAdmin.setPermissionLevel(127);
        Profile superAdminProfile = new Profile(superAdmin, "A", "Name", LocalDate.now(), Gender.NON_BINARY);
        Mockito.when(profileRepository.findById(superAdminProfile.getUser().getUserId())).thenReturn(Optional.of(superAdminProfile));

        List<ActivityPin> pins = new ArrayList<>();
        ActivityPin pin1 = this.setupActivityPinMock(1, 8, 8);
        Activity activity1 = pin1.getActivity();
        Mockito.when(activity1.getCreator()).thenReturn(profile);
        pins.add(pin1);
        
        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), any()))
    		.thenReturn(pins);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(superAdmin.getUserId(), activity1.getId()))
        	.thenReturn(Optional.empty());
        Mockito.when(profileRepository.findById(superAdmin.getUserId())).thenReturn(Optional.of(superAdminProfile));
        
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", superAdmin.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        String responseJson = result.getResponse().getContentAsString();
		List<ActivityPinResponse> response = objectMapper.readValue(responseJson, new TypeReference<List<ActivityPinResponse>>() {});
		
		assertEquals(1, response.size());
		assertNull(response.get(0).getRole());
    }


    @Test
    void testGetActivitiesInBounds_MultipleRoles_DifferentRolesAreReturned() throws Exception {
        User requestSender = new User(2L);
        Profile senderProfile = new Profile(requestSender, "It", "DoesNotMatter", LocalDate.now(), Gender.NON_BINARY);
        Mockito.when(profileRepository.findById(requestSender.getUserId())).thenReturn(Optional.of(senderProfile));

        List<ActivityPin> pins = new ArrayList<>();

        ActivityPin creatorPin = this.setupActivityPinMock(1, 8, 8);
        Activity creatorActivity = creatorPin.getActivity();
        Mockito.when(creatorActivity.getCreator()).thenReturn(senderProfile);
        pins.add(creatorPin);

        ActivityPin organiserPin = this.setupActivityPinMock(2L, 8, 8);
        Activity organiserActivity = organiserPin.getActivity();
        Mockito.when(organiserActivity.getCreator()).thenReturn(profile);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(requestSender.getUserId(), organiserActivity.getId()))
                .thenReturn(Optional.of(new UserActivityRole(organiserActivity, requestSender, ActivityRole.ORGANISER)));
        pins.add(organiserPin);

        ActivityPin participantPin = this.setupActivityPinMock(3, 8, 8);
        Activity participantActivity = participantPin.getActivity();
        Mockito.when(participantActivity.getCreator()).thenReturn(profile);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(requestSender.getUserId(), participantActivity.getId()))
                .thenReturn(Optional.of(new UserActivityRole(participantActivity, requestSender, ActivityRole.PARTICIPANT)));
        pins.add(participantPin);

        ActivityPin followerPin = this.setupActivityPinMock(4, 8, 8);
        Activity followerActivity = followerPin.getActivity();
        Mockito.when(followerActivity.getCreator()).thenReturn(profile);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(requestSender.getUserId(), followerActivity.getId()))
                .thenReturn(Optional.empty());
        Mockito.when(subscriptionRepository.isSubscribedToActivity(followerActivity.getId(), senderProfile)).thenReturn(true);
        pins.add(followerPin);

        ActivityPin miscPin = this.setupActivityPinMock(5, 8, 8);
        Activity miscActivity = miscPin.getActivity();
        Mockito.when(miscActivity.getCreator()).thenReturn(profile);
        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(requestSender.getUserId(), miscActivity.getId()))
                .thenReturn(Optional.empty());
        Mockito.when(subscriptionRepository.isSubscribedToActivity(miscActivity.getId(), senderProfile)).thenReturn(false);
        pins.add(miscPin);

        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.anyFloat(), Mockito.anyFloat(), Mockito.anyFloat(), Mockito.anyFloat(), any()))
                .thenReturn(pins);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", requestSender.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<ActivityPinResponse> response = objectMapper.readValue(responseJson, new TypeReference<>() {});

        assertEquals(5, response.size());
        assertEquals(1L, response.get(0).getActivityId());
        assertEquals("creator", response.get(0).getRole());
        assertEquals(2L, response.get(1).getActivityId());
        assertEquals("organiser", response.get(1).getRole());
        assertEquals(3L, response.get(2).getActivityId());
        assertEquals("participant", response.get(2).getRole());
        assertEquals(4L, response.get(3).getActivityId());
        assertEquals("follower", response.get(3).getRole());
        assertEquals(5L, response.get(4).getActivityId());
        assertNull(response.get(4).getRole());
    }

    @Test
    void testGetActivitiesWithSomeRecommended() throws Exception {
        User requestSender = new User(6L);
        Profile senderProfile = new Profile(requestSender, "It", "DoesNotMatter", LocalDate.now(), Gender.NON_BINARY);
        HashSet<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(new ActivityType("Walking"));
        senderProfile.setActivityTypes(new ArrayList<>(activityTypes));
        Mockito.when(profileRepository.findById(requestSender.getUserId())).thenReturn(Optional.of(senderProfile));

        User activityCreator = new User(7L);
        Profile creatorProfile = new Profile(activityCreator, "ItAlso", "DoesNotMatter", LocalDate.now(), Gender.NON_BINARY);
        Mockito.when(profileRepository.findById(activityCreator.getUserId())).thenReturn(Optional.of(creatorProfile));

        Mockito.when(userActivityRoleRepository.getRoleEntryByUserId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.empty());

        List<ActivityPin> pins = new ArrayList<>();

        Activity activity1 = new Activity("Activity 1", false, "Location", creatorProfile, activityTypes);
        activity1.setId(1L);
        ActivityPin pin1 = new ActivityPin(activity1, 8, 8, 0f, 0f, 0f, 0f);
        pins.add(pin1);
        Mockito.when(subscriptionRepository.isSubscribedToActivity(activity1.getId(), senderProfile)).thenReturn(false);

        Activity activity2 = new Activity("Activity 2", false, "Location", creatorProfile, activityTypes);
        activity2.setId(2L);
        ActivityPin pin2 = new ActivityPin(activity2, 8, 8, 0f, 0f, 0f, 0f);
        pins.add(pin2);
        Mockito.when(subscriptionRepository.isSubscribedToActivity(activity2.getId(), senderProfile)).thenReturn(false);

        Activity activity3 = new Activity("Activity 3", false, "Location", creatorProfile, new HashSet<>());
        activity3.setId(3L);
        ActivityPin pin3 = new ActivityPin(activity3, 8, 8, 0f, 0f, 0f, 0f);
        pins.add(pin3);
        Mockito.when(subscriptionRepository.isSubscribedToActivity(activity3.getId(), senderProfile)).thenReturn(false);

        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.anyFloat(), Mockito.anyFloat(), Mockito.anyFloat(), Mockito.anyFloat(), any()))
                .thenReturn(pins);
        Mockito.when(activityRepository.findRecommendedActivitiesByProfile(Mockito.any(Profile.class)))
                .thenReturn(Arrays.asList(activity1, activity2));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", requestSender.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<ActivityPinResponse> response = objectMapper.readValue(responseJson, new TypeReference<>() {});

        ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);
        Mockito.verify(activityRepository).findRecommendedActivitiesByProfile(captor.capture());
        assertEquals(1, captor.getAllValues().size());
        assertEquals(senderProfile, captor.getValue());

        assertEquals(3, response.size());
        assertTrue(response.get(0).getIsRecommended());
        assertTrue(response.get(1).getIsRecommended());
        assertFalse(response.get(2).getIsRecommended());
    }

    @Test
    void testActivityHasAlreadyFinished_ReturnNone() throws Exception {
        List<ActivityPin> pins = new ArrayList<>();

        ActivityPin pin1 = this.setupActivityPinWithDuration(1, 8, 8, "2018-07-14T17:45:55+1200");
        Activity activity1 = pin1.getActivity();
        Mockito.when(activity1.getCreator()).thenReturn(profile);
        pins.add(pin1);

        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), Mockito.any()))
                .thenReturn(pins);

        mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", profile.getUser().getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    void testActivityHasAlreadyFinished_ReturnOne() throws Exception {
        List<ActivityPin> pins = new ArrayList<>();

        ActivityPin pin1 = this.setupActivityPinWithDuration(1, 8, 8, "2018-07-14T17:45:55+1200");
        Activity activity1 = pin1.getActivity();
        Mockito.when(activity1.getCreator()).thenReturn(profile);
        pins.add(pin1);

        ActivityPin pin2 = this.setupActivityPinWithDuration(2, 8, 8, "2030-07-14T17:45:55+1200");
        Activity activity2 = pin2.getActivity();
        Mockito.when(activity2.getCreator()).thenReturn(profile);
        pins.add(pin2);

        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), Mockito.any()))
                .thenReturn(pins);

        mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", profile.getUser().getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)));
    }

    @Test
    void testActivityMixDurationAndNot_Return2() throws Exception {
        List<ActivityPin> pins = new ArrayList<>();

        ActivityPin pin1 = this.setupActivityPinWithDuration(1, 8, 8, "2018-07-14T17:45:55+1200");
        Activity activity1 = pin1.getActivity();
        Mockito.when(activity1.getCreator()).thenReturn(profile);
        pins.add(pin1);

        ActivityPin pin2 = this.setupActivityPinWithDuration(2, 8, 8, "2030-07-14T17:45:55+1200");
        Activity activity2 = pin2.getActivity();
        Mockito.when(activity2.getCreator()).thenReturn(profile);
        pins.add(pin2);

        ActivityPin pin3 = this.setupActivityPinMock(3, 8, 8);
        Activity activity3 = pin3.getActivity();
        Mockito.when(activity3.getCreator()).thenReturn(profile);
        pins.add(pin3);

        Mockito.when(activityPinRepository.findPinsInBounds(Mockito.eq(10f), Mockito.eq(10f), Mockito.eq(0f), Mockito.eq(0f), Mockito.any()))
                .thenReturn(pins);

        mvc.perform(MockMvcRequestBuilders
                .get("/maps")
                .param("ne_lat", "10.0")
                .param("ne_lon", "10.0")
                .param("sw_lat", "0.0")
                .param("sw_lon", "0.0")
                .requestAttr("authenticatedid", profile.getUser().getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
    }
}