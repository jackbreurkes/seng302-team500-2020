package com.springvuegradle.endpoints;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.ExceptionHandlerController;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;

import org.apache.tomcat.util.json.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserProfileController.class})
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserProfileControllerTest {

    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private EmailRepository emailRepository;
    @MockBean
    private ProfileRepository profileRepository;
    @MockBean
    private CountryRepository countryRepository;
    @MockBean
    private ActivityRepository activityRepository;
    @MockBean
    private SessionRepository sessionRepository;
    @MockBean
    private ActivityTypeRepository activityTypeRepository;
    @MockBean
    private LocationRepository locationRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private ChangeLogRepository changeLogRepository;

    /**
     * Creates the user profile controller and inserts the mocks we define in the place of the repositories
     */
    @InjectMocks
    UserProfileController userProfileController;

    ActivityType tempActivityType;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(userProfileController)
                .setControllerAdvice(new ExceptionHandlerController()) // allows us to use our ExceptionHandlerController with MockMvc
                .build();
        this.tempActivityType = new ActivityType("Running");
    }
    
    /**
     * Get a list of two profiles to use when testing the GET /profiles endpoint
     * @return List containing two Profiles with names "Bobby B Brown" and "Bamboo Brown"
     */
    private List<Profile> getTestProfileList() {
    	// Fullname is "Bobby B Brown"
    	Profile profile1 = new Profile(new User(1), "Bobby", "Brown", LocalDate.now(), Gender.NON_BINARY);
    	profile1.setMiddleName("B");
    	
    	// Fullname is "Bamboo Brown"
    	Profile profile2 = new Profile(new User(2), "Bamboo", "Brown", LocalDate.now(), Gender.NON_BINARY);
    	
    	List<Profile> profileList = new ArrayList<Profile>();
    	profileList.add(profile1);
    	profileList.add(profile2);
    	
    	return profileList;
    }
    
    /**
     * Create a list of email objects to use when testing GET /profiles by email
     * @param user1 the first user to associate with an email
     * @param user2 the second user to associate with an email
     * @return List of Email objects containing the emails: "a@a.com", "b@b.com", "babble@b.com", "babble@b.co.nz", "babble@b.net"
     */
    private List<Email> getTestEmailList(User user1, User user2) {
    	Email email1 = new Email(user1, "a@a.com", false);
    	Email email2 = new Email(user2, "b@b.com", false);
    	Email email3 = new Email(user1, "babble@b.com", false);
    	Email email4 = new Email(user1, "babble@b.co.nz", false);
    	Email email5 = new Email(user2, "babble@b.co.au", false);
    	
    	List<Email> emailList = new ArrayList<Email>();
    	emailList.add(email1);
    	emailList.add(email2);
    	emailList.add(email3);
    	emailList.add(email4);
    	emailList.add(email5);
    	
    	return emailList;
    }


    @Test
    @Disabled
    public void testGetProfileById() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .get("/profiles/{id}", 5)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.employees").exists())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].employeeId").isNotEmpty());
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(5));
    }
 
    @Test
    public void testValidCreateUser() throws Exception {
    	String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"JackyTest\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"additional_email\": [\n" +
                "    \"jacky@xtra.co.nz\",\n" +
                "    \"jacky@msn.com\"\n" +
                "    ],\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"password\": \"aaaaaaaa\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
//                "    \"United States of America\",\n" +
//                "    \"Thailand\"\n" +
                "  ]\n" +
                "}";


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile_id").value(0));
    }

    @Test
    public void testMinimalCreateUser() throws Exception {
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"password\": \"password\"\n" +
                "}";


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile_id").value(0));
    }

    @Test
    public void testMissingFirstname() throws Exception {
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"password\": \"password\"\n" +
                "}";


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
                // TODO: our current MockMvc implementation does not use our ExceptionHandlerController, so
        //      // TODO:   these tests do not return any JSON data. would be nice to figure out how to fix that.
                //.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("No firstname field"));
    }

    @Test
    public void testMissingPassword() throws Exception {
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\"\n" +
                "}";


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
                //.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("No password field"));
    }

    @Test
    public void testInvalidJsonString() throws Exception {
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\":: \"jacky@google.com\",\n" + // double colon on this line
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEmptyLists() throws Exception {
        String createUserJson = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"JackyTest\",\n" +
                "  \"primary_email\": \"111@google.com\",\n" +
                "  \"additional_email\": [],\n" + // empty
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"password\": \"aaaaaaaa\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [],\n" + // empty
                "  \"activities\": []\n" + // empty
                "}";


        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(createUserJson).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile_id").value(0));
    }

    @Test
    public void testGetProfile_Authorized_ReturnsProfile() throws Exception {
        long profileId = 1;
        long authId = 2;

        Profile profile = new Profile(new User(profileId), "First", "Last", LocalDate.EPOCH, Gender.NON_BINARY);
        Mockito.when(profileRepository.existsById(profileId)).thenReturn(true);
        Mockito.when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        User authUser = Mockito.mock(User.class);
        Mockito.when(authUser.getPermissionLevel()).thenReturn(0);
        Mockito.when(userRepository.findById(authId)).thenReturn(Optional.of(authUser));

        mvc.perform(MockMvcRequestBuilders
                .get("/profiles/" + profileId)
                .requestAttr("authenticatedid", authId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile_id").value(profileId));
    }

    @Test
    public void testGetProfile_NoToken_Unauthorised() throws Exception {
        long profileId = 1;
        mvc.perform(MockMvcRequestBuilders
                .get("/profiles/" + profileId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.error")
                        .value("User not authenticated"));
    }
    
    // ==================== Test GET /profiles with parameters ==========================
    
    // ---------------------- Test GET /profiles by nickname ----------------------------
    @Test
    public void testGetUserByFullNickname() throws Exception {
    	String nickname = "mika";

    	Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.now(), Gender.NON_BINARY);
    	profile1.setNickName(nickname);
    	
    	List<Profile> profileList = new ArrayList<Profile>();
    	profileList.add(profile1);
    	
        Mockito.when(profileRepository.findByNickNameStartingWith(nickname)).thenReturn(profileList);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("nickname", nickname)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);

        assertEquals(BigInteger.valueOf(1l), profileFound.get("profile_id"));
    }
    
    @Test
    public void testGetUserByFirstCharsOfNickname() throws Exception {
    	String nickname = "mika";
    	String partialNickname = "mi";

    	Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.now(), Gender.NON_BINARY);
    	profile1.setNickName(nickname);
    	
    	List<Profile> profileList = new ArrayList<Profile>();
    	profileList.add(profile1);
    	
        Mockito.when(profileRepository.findByNickNameStartingWith(partialNickname)).thenReturn(profileList);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("nickname", partialNickname)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);

        assertEquals(BigInteger.valueOf(1L), profileFound.get("profile_id"));
    }
    
    @Test
    public void testGetUserByNicknameDifferentCase() throws Exception {
    	String nickname = "Mika";
    	String partialNickname = "mi";

    	Profile profile1 = new Profile(new User(1), "Fake", "User", LocalDate.now(), Gender.NON_BINARY);
    	profile1.setNickName(nickname);
    	
    	List<Profile> profileList = new ArrayList<Profile>();
    	profileList.add(profile1);
    	
        Mockito.when(profileRepository.findByNickNameStartingWith(partialNickname)).thenReturn(profileList);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("nickname", partialNickname)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);

        assertEquals(BigInteger.valueOf(1L), profileFound.get("profile_id"));
    }
    
    @Test
    public void testGetMultipleUsersByNickname() throws Exception {
    	String nickname1 = "mika";
    	String nickname2 = "mickeymouse";
    	String partialNickname = "mi";

    	Profile profile1 = new Profile(new User(1), "Fake", "Number one", LocalDate.now(), Gender.NON_BINARY);
    	profile1.setNickName(nickname1);
    	Profile profile2 = new Profile(new User(2), "Fake", "Number two", LocalDate.now(), Gender.NON_BINARY);
    	profile1.setNickName(nickname2);
    	
    	List<Profile> profileList = new ArrayList<Profile>();
    	profileList.add(profile1);
    	profileList.add(profile2);
    	
        Mockito.when(profileRepository.findByNickNameStartingWith(partialNickname)).thenReturn(profileList);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("nickname", partialNickname)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> firstProfileFound = (LinkedHashMap<String, Object>) body.get(0);
        LinkedHashMap<String, Object> secondProfileFound = (LinkedHashMap<String, Object>) body.get(1);
    	
    	assertEquals(2, body.size());
        assertEquals(BigInteger.valueOf(1L), firstProfileFound.get("profile_id"));
        assertEquals(BigInteger.valueOf(2L), secondProfileFound.get("profile_id"));
    }
    
    @Test
    public void testGetUserByNonExistentNickname() throws Exception {
    	String nickname = "mika";
    	
        Mockito.when(profileRepository.findByNickNameStartingWith(nickname)).thenReturn(new ArrayList<Profile>());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("nickname", nickname)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());

    	assertEquals(0, body.size());
    }
    
    // ---------------------- Test GET /profiles by full name ----------------------------
    @Test
    public void testGetUserByFullFullname() throws Exception {
    	
    	String fullname = "Bobby Brown";
    	Profile profile1 = new Profile(new User(1), "Bobby", "Brown", LocalDate.now(), Gender.NON_BINARY);
    	
    	List<Profile> profileList = new ArrayList<Profile>();
    	profileList.add(profile1);
    	
        Mockito.when(profileRepository.findByFirstNameStartingWithAndLastNameStartingWith("Bobby", "Brown")).thenReturn(profileList);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("fullname", fullname)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);

        assertEquals(BigInteger.valueOf(1L), profileFound.get("profile_id"));
    }
    
    @Test
    public void testGetUserByFullFullnameWithMiddleName() throws Exception {
    	
    	String fullname = "Bobby B Brown";
    	Profile profile1 = new Profile(new User(1), "Bobby", "Brown", LocalDate.now(), Gender.NON_BINARY);
    	profile1.setMiddleName("B");
    	
    	List<Profile> profileList = new ArrayList<Profile>();
    	profileList.add(profile1);
    	
        Mockito.when(profileRepository.findByFirstNameStartingWithAndMiddleNameStartingWithAndLastNameStartingWith("Bobby", "B", "Brown")).thenReturn(profileList);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("fullname", fullname)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);

        assertEquals(BigInteger.valueOf(1l), profileFound.get("profile_id"));
    }
    
    @Test
    public void testGetUserByPartialFullname() throws Exception {
    	
    	String partialName = "Bob Bro";
    	
    	String fullname = "Bobby B Brown";
    	Profile profile1 = new Profile(new User(1), "Bobby", "Brown", LocalDate.now(), Gender.NON_BINARY);
    	profile1.setMiddleName("B");
    	
    	List<Profile> profileList = new ArrayList<Profile>();
    	profileList.add(profile1);
    	
        Mockito.when(profileRepository.findByFirstNameStartingWithAndLastNameStartingWith("Bob", "Bro")).thenReturn(profileList);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("fullname", partialName)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);

        assertEquals(BigInteger.valueOf(1l), profileFound.get("profile_id"));
    }
    
    @Test
    public void testGetUserByNonExistentFullname() throws Exception {
    	    	
    	String fullname = "Bobby B Brown";
    	
        Mockito.when(profileRepository.findByFirstNameStartingWithAndLastNameStartingWith("Billy", "Bob")).thenReturn(new ArrayList<Profile>());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("fullname", fullname)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        assertEquals(0, body.size());
    }
    
    @Test
    public void testGetMultipleUsersByFullname() throws Exception {
    	
    	String partialName = "B Brown";
    	
    	// Fullname is "Bobby B Brown";
    	Profile profile1 = new Profile(new User(1), "Bobby", "Brown", LocalDate.now(), Gender.NON_BINARY);
    	profile1.setMiddleName("B");
    	
    	// Fullname is "Bamboo Brown";
    	Profile profile2 = new Profile(new User(2), "Bamboo", "Brown", LocalDate.now(), Gender.NON_BINARY);
    	
    	List<Profile> profileList = new ArrayList<Profile>();
    	profileList.add(profile1);
    	profileList.add(profile2);
    	
        Mockito.when(profileRepository.findByFirstNameStartingWithAndLastNameStartingWith("B", "Brown")).thenReturn(profileList);

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("fullname", partialName)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    	
    	ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());

    	assertEquals(2, body.size());
        assertEquals(BigInteger.valueOf(1L), body.get(0).get("profile_id"));
        assertEquals(BigInteger.valueOf(2L), body.get(1).get("profile_id"));
    }
    
    // ------------------------ Test GET /profiles by email -----------------------------
    @Test
    public void testGetUserByFullEmail() throws Exception {
    	
    	String email = "a@a.com";
    	List<Profile> profileList = getTestProfileList();
    	User user1 = profileList.get(0).getUser();
    	List<Email> emailList = new ArrayList<Email>();
    	emailList.add(getTestEmailList(user1, profileList.get(1).getUser()).get(0));
    	
        Mockito.when(emailRepository.findByEmailStartingWith(email)).thenReturn(emailList);
        Mockito.when(profileRepository.getOne(user1.getUserId())).thenReturn(profileList.get(0));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("email", email)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);

    	assertEquals(1, body.size());
        assertEquals(BigInteger.valueOf(1L), profileFound.get("profile_id"));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
            "test"
    })
    public void testGetUserByFirstPartOfEmail(String partialEmail) throws Exception {
    	
    	List<Profile> profileList = getTestProfileList();
    	User user1 = profileList.get(0).getUser();
    	List<Email> emailList = new ArrayList<Email>();
    	emailList.add(getTestEmailList(user1, profileList.get(1).getUser()).get(0));
    	
        Mockito.when(emailRepository.findByEmailStartingWith(partialEmail+"@")).thenReturn(emailList);
        Mockito.when(profileRepository.getOne(user1.getUserId())).thenReturn(profileList.get(0));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("email", partialEmail)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
    	assertEquals(1, body.size());

        assertEquals(BigInteger.valueOf(1L), body.get(0).get("profile_id"));
    }
    
    @Test
    public void testGetUserByPartOfFirstEmailSegment() throws Exception {
    	
    	String partialEmail = "bab";
    	
        Mockito.when(emailRepository.findByEmailStartingWith(partialEmail+"@")).thenReturn(new ArrayList<Email>());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("email", partialEmail)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
    	
    	assertEquals(0, body.size());
    }
    
    @Test
    public void testGetUserByPartialEmailUpToAtSymbol() throws Exception {
    	
    	String partialEmail = "a@";
    	List<Profile> profileList = getTestProfileList();
    	User user1 = profileList.get(0).getUser();
    	List<Email> emailList = new ArrayList<Email>();
    	emailList.add(getTestEmailList(user1, profileList.get(1).getUser()).get(0));
    	
        Mockito.when(emailRepository.findByEmailStartingWith(partialEmail)).thenReturn(emailList);
        Mockito.when(profileRepository.getOne(user1.getUserId())).thenReturn(profileList.get(0));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("email", partialEmail)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);

    	assertEquals(1, body.size());
    	assertEquals(BigInteger.valueOf(1l), profileFound.get("profile_id"));
    }
    
    @Test
    public void testGetUserByPartialEmailPastAtSymbol() throws Exception {
    	
    	String partialEmail = "a@a.c";
    	List<Profile> profileList = getTestProfileList();
    	User user1 = profileList.get(0).getUser();
    	List<Email> emailList = new ArrayList<Email>();
    	emailList.add(getTestEmailList(user1, profileList.get(1).getUser()).get(0));
    	
        Mockito.when(emailRepository.findByEmailStartingWith(partialEmail)).thenReturn(emailList);
        Mockito.when(profileRepository.getOne(user1.getUserId())).thenReturn(profileList.get(0));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("email", partialEmail)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);

    	assertEquals(1, body.size());
        assertEquals(BigInteger.valueOf(1l), profileFound.get("profile_id"));
    }
    
    @Test
    public void testGetUserByNonExistentEmail() throws Exception {
    	    	
    	String email = "pizza@pies.com";
    	
        Mockito.when(emailRepository.findByEmailStartingWith(email)).thenReturn(new ArrayList<Email>());

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("email", email)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
    	
    	assertEquals(0, body.size());
    }
    
    @Test
    public void testGetSingleUserWithMultipleMatchingEmails() throws Exception {
    	
    	String partialEmail = "babble@b.c";
    	List<Profile> profileList = getTestProfileList();
    	User user1 = profileList.get(0).getUser();
    	User user2 = profileList.get(1).getUser();
    	List<Email> emailList = new ArrayList<Email>();
    	emailList.add(getTestEmailList(user1, user2).get(2));	// babble@b.com   - belongs to user1
    	emailList.add(getTestEmailList(user1, user2).get(3));	// babble@b.co.nz - belongs to user1
    	
        Mockito.when(emailRepository.findByEmailStartingWith(partialEmail)).thenReturn(emailList);
        Mockito.when(profileRepository.getOne(user1.getUserId())).thenReturn(profileList.get(0));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("email", partialEmail)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());
        LinkedHashMap<String, Object> profileFound = body.get(0);
    	
    	assertEquals(1, body.size());
        assertEquals(BigInteger.valueOf(1l), profileFound.get("profile_id"));
    } 
    
    @Test
    public void testGetMultipleUsersByEmail() throws Exception {
    	
    	String partialEmail = "babble@";
    	List<Profile> profileList = getTestProfileList();
    	User user1 = profileList.get(0).getUser();
    	User user2 = profileList.get(1).getUser();
    	List<Email> emailList = new ArrayList<Email>();
    	emailList.add(getTestEmailList(user1, user2).get(2));	// babble@b.com   - belongs to user1
    	emailList.add(getTestEmailList(user1, user2).get(3));	// babble@b.co.nz - belongs to user1
    	emailList.add(getTestEmailList(user1, user2).get(4));	// babble@b.net   - belongs to user2
    	
        Mockito.when(emailRepository.findByEmailStartingWith(partialEmail)).thenReturn(emailList);
        Mockito.when(profileRepository.getOne(user1.getUserId())).thenReturn(profileList.get(0));
        Mockito.when(profileRepository.getOne(user2.getUserId())).thenReturn(profileList.get(1));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/profiles")
                .queryParam("email", partialEmail)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        
        ArrayList<LinkedHashMap<String, Object>> body = getResultListJson(result.getResponse().getContentAsString());

    	assertEquals(2, body.size());
    	List<BigInteger> ids = body.stream().map(profile -> (BigInteger) profile.get("profile_id")).collect(Collectors.toList());
        assertTrue(ids.containsAll(Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2))));
    } 
    
    // Helper function to get the list of users from the JSON returned when searching for them using GET /profiles with query parameters
    private ArrayList<LinkedHashMap<String, Object>> getResultListJson(String raw) throws org.apache.tomcat.util.json.ParseException {
		JSONParser parser = new JSONParser(raw);
		ArrayList<LinkedHashMap<String, Object>> json = null;
		json = (ArrayList<LinkedHashMap<String, Object>>) parser.parse();
		return json;
	}

	//----------------------------Testing GET Profile Location----------------------------//
    @Test
    public void testGetProfileLocation_Authorized_ReturnsProfile() throws Exception {
        long profileId = 1;
        long authId = 2;

        Profile profile = new Profile(new User(profileId), "First", "Last", LocalDate.EPOCH, Gender.NON_BINARY);
        Location location = new Location("Christchurch", "New Zealand");
        profile.setLocation(location);
        Mockito.when(profileRepository.existsById(profileId)).thenReturn(true);
        Mockito.when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        User authUser = Mockito.mock(User.class);
        Mockito.when(authUser.getPermissionLevel()).thenReturn(0);
        Mockito.when(userRepository.findById(authId)).thenReturn(Optional.of(authUser));

        mvc.perform(MockMvcRequestBuilders
                .get("/profiles/" + profileId + "/latlon")
                .requestAttr("authenticatedid", authId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile_id").value(profileId));
    }

    @Test
    public void testGetProfileLocation_Authorized_ReturnsProfileWithLocation() throws Exception {
        long profileId = 1;
        long authId = 2;

        Profile profile = new Profile(new User(profileId), "First", "Last", LocalDate.EPOCH, Gender.NON_BINARY);
        Location location = new Location("Christchurch", "New Zealand");
        profile.setLocation(location);
        Mockito.when(profileRepository.existsById(profileId)).thenReturn(true);
        Mockito.when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        User authUser = Mockito.mock(User.class);
        Mockito.when(authUser.getPermissionLevel()).thenReturn(0);
        Mockito.when(userRepository.findById(authId)).thenReturn(Optional.of(authUser));

        mvc.perform(MockMvcRequestBuilders
                .get("/profiles/" + profileId + "/latlon")
                .requestAttr("authenticatedid", authId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile_id").value(profileId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lon").value(172.63664))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lat").value(-43.530956));
    }

}