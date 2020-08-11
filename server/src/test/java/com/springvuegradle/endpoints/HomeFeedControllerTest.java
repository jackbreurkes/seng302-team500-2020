package com.springvuegradle.endpoints;

import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {HomeFeedController.class})
@WebMvcTest
public class HomeFeedControllerTest {

    @InjectMocks
    private HomeFeedController homeFeedController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ChangeLogRepository changeLogRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ActivityRepository activityRepository;

    @MockBean
    ProfileRepository profileRepository;

    User user;
    Profile profile;
    List<ChangeLog> changeLogList;
    String homeFeedJson;

    @BeforeEach
    void beforeEach() {
        homeFeedController = new HomeFeedController();

        user = new User(1);
        Mockito.when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        profile = new Profile(user, "Bob","Builder", LocalDate.of(2000, 10, 15), Gender.MALE);
        Mockito.when(profileRepository.findById(user.getUserId())).thenReturn(Optional.of(profile));

        List<String[]> changeValues = new ArrayList<>();
        changeValues.add(new String[]{"Value 0", "Value 1"});
        changeValues.add(new String[]{"Value 1", "Value 2"});
        changeValues.add(new String[]{"Value 2", "Value 3"});

        Activity swimming = new Activity("Swimming", false, "Location", profile, new HashSet<>());
        swimming.setId(1l);
        Activity cycling = new Activity("Cycling", false, "Location", profile, new HashSet<>());
        cycling.setId(2l);
        Mockito.when(activityRepository.findById(swimming.getId())).thenReturn(Optional.of(swimming));
        Mockito.when(activityRepository.findById(cycling.getId())).thenReturn(Optional.of(cycling));

        changeLogList = new ArrayList<>();
        homeFeedJson = "[";
        for (String[] values : changeValues) {
            ChangeLog change = new ChangeLog(ChangeLogEntity.ACTIVITY, swimming.getId(), ChangedAttribute.ACTIVITY_DESCRIPTION,
                    profile.getUser(), ActionType.UPDATED, values[0], values[1]);
            change.setOffsetDateTime(OffsetDateTime.of(2000, 1, 1, 1, 1, 1, 0, ZoneOffset.UTC));
            changeLogList.add(change);
            homeFeedJson += getChangeLogResponseJson(change, swimming) + ",";
        }

        ChangeLog cyclingChangeLog = new ChangeLog(ChangeLogEntity.ACTIVITY, cycling.getId(), ChangedAttribute.ACTIVITY_DESCRIPTION,
                profile.getUser(), ActionType.UPDATED, "Old description", "New description");
        changeLogList.add(cyclingChangeLog);
        cyclingChangeLog.setOffsetDateTime(OffsetDateTime.of(2000, 1, 1, 1, 1, 1, 0, ZoneOffset.UTC));
        homeFeedJson += getChangeLogResponseJson(cyclingChangeLog, cycling) + "]";

        Mockito.when(changeLogRepository.retrieveUserHomeFeedUpdates(profile)).thenReturn(changeLogList);
    }

    private String getChangeLogResponseJson(ChangeLog changeLog, Activity activity) {
        return "{" +                  // Format taken from api spec for GET /homefeed/profileId on wiki
                "\"entity_type\":\"ACTIVITY\"," +
                "\"entity_id\":" + activity.getId() + "," +
                "\"entity_name\":\"" + activity.getActivityName() + "\"," +
                "\"creator_id\":" + activity.getCreator().getUser().getUserId() + "," +
                "\"creator_name\":\"" + activity.getCreator().getFullName(false) + "\"," +
                "\"edited_timestamp\":\"" + changeLog.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")) + "\"," +
                "\"editor_id\":" + activity.getCreator().getUser().getUserId() + "," +
                "\"editor_name\":\"" + activity.getCreator().getFullName(false) + "\"," +
                "\"changed_attribute\":\"ACTIVITY_DESCRIPTION\"," +
                "\"action_type\":\"UPDATED\"," +
                "\"old_value\":\"" + changeLog.getOldValue() + "\"," +
                "\"new_value\":\"" + changeLog.getNewValue() + "\"" +
                "}";
    }

    @Test
    public void testGetValidUserHomeFeed() throws Exception {

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId())
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(homeFeedJson, result.getResponse().getContentAsString());
    }

    @Test
    public void testGetEmptyUserHomeFeed() throws Exception {

        User freshUser = new User(2);       // Create new user not subscribed to anything
        Mockito.when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        Profile freshProfile = new Profile(freshUser, "Billy-Does-Nothing","Bill", LocalDate.of(2000, 10, 15), Gender.MALE);
        Mockito.when(profileRepository.findById(freshUser.getUserId())).thenReturn(Optional.of(freshProfile));

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+freshUser.getUserId())
                .requestAttr("authenticatedid", freshUser.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    public void testGetHomeFeedWithNoAuthentication() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetHomeFeedOfNonExistentUser() throws Exception {

        Long fakeUserId = 99l;
        Mockito.when(userRepository.findById(fakeUserId)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+fakeUserId)
                .requestAttr("authenticatedid", fakeUserId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetHomeFeedOfOtherUser() throws Exception {

        User otherUser = new User(3);
        Mockito.when(userRepository.findById(otherUser.getUserId())).thenReturn(Optional.of(otherUser));

        mvc.perform(MockMvcRequestBuilders
                .get("/homefeed/"+otherUser.getUserId())
                .requestAttr("authenticatedid", user.getUserId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
