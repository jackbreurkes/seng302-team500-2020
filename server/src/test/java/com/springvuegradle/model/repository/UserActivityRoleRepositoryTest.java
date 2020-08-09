package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.apache.catalina.util.ToStringUtil;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserActivityRoleRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    ActivityTypeRepository activityTypeRepository;

    Profile profile1, profile2, profile3, profile6;
    Activity activity1, activity2, activity3;

    @BeforeAll
    public void populateUsersAndActivities(){
        ActivityType walking = new ActivityType("A");
        ActivityType running = new ActivityType("B");
        ActivityType biking = new ActivityType("D");

        activityTypeRepository.save(walking);
        activityTypeRepository.save(running);
        activityTypeRepository.save(biking);

        profile1 = new Profile(new User(), "Bill", "Testman", LocalDate.EPOCH, Gender.NON_BINARY);
        profile2 = new Profile(new User(), "Andy", "Warshaw", LocalDate.EPOCH, Gender.NON_BINARY);
        profile3 = new Profile(new User(), "Brie", "Calaris", LocalDate.EPOCH, Gender.NON_BINARY);
        profile6 = new Profile(new User(), "New", "Fella", LocalDate.EPOCH, Gender.NON_BINARY);

        profileRepository.save(profile1);
        profileRepository.save(profile2);
        profileRepository.save(profile3);
        profileRepository.save(profile6);

        activity1 = new Activity("Test Activity", false, "New Zealand", profile1, Collections.singleton(walking));
        activity2 = new Activity("Test Activity2", false, "New Zealand", profile2, Collections.singleton(running));
        activity3 = new Activity("Test Activity3", false, "New Zealand", profile3, Collections.singleton(biking));

        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);

        UserActivityRole userActivityRole1 = new UserActivityRole(activity1, profile1.getUser(), ActivityRole.ORGANISER);
        UserActivityRole userActivityRole2 = new UserActivityRole(activity2, profile2.getUser(), ActivityRole.PARTICIPANT);
        UserActivityRole userActivityRole3 = new UserActivityRole(activity1, profile2.getUser(), ActivityRole.PARTICIPANT);
        UserActivityRole userActivityRole4 = new UserActivityRole(activity3, profile3.getUser(), ActivityRole.PARTICIPANT);

        userActivityRoleRepository.save(userActivityRole1);
        userActivityRoleRepository.save(userActivityRole2);
        userActivityRoleRepository.save(userActivityRole3);
        userActivityRoleRepository.save(userActivityRole4);
    }

    @Test
    public void testGetInvolvedActivitiesByUserId() {
        List<Activity> result = userActivityRoleRepository.getInvolvedActivitiesByUserId(profile2.getUser().getUserId());
        assertEquals(2, result.size());
    }

    @Test
    public void testGetSingleInvolvedActivityByUserId() {
        List<Activity> result = userActivityRoleRepository.getInvolvedActivitiesByUserId(profile3.getUser().getUserId());
        assertEquals(1, result.size());
    }

    @Test
    public void testGetInvolvedActivitiesByInvalidUserId() {
        List<Activity> result = userActivityRoleRepository.getInvolvedActivitiesByUserId(700);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetInvolvedUsersByActivityId(){
        List<User> result = userActivityRoleRepository.getInvolvedUsersByActivityId(activity1.getId());
        assertEquals(2, result.size());
    }

    @Test
    public void testGetSingleInvolvedUserByActivityId(){
        List<User> result = userActivityRoleRepository.getInvolvedUsersByActivityId(activity3.getId());
        assertEquals(1, result.size());
    }

    @Test
    public void testGetInvolvedUsersByInvalidActivityId(){
        List<User> result = userActivityRoleRepository.getInvolvedUsersByActivityId(1000);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetUserActivityRoleByUserId(){
        Optional <UserActivityRole> result = userActivityRoleRepository.getRoleEntryByUserId(profile3.getUser().getUserId(), activity3.getId());
        UserActivityRole object = result.get();
        assertEquals(profile3.getUser().getUserId(), object.getUser().getUserId());
    }

    @Test
    public void testGetNoUserActivityRoleByUserId(){
        Optional <UserActivityRole> result = userActivityRoleRepository.getRoleEntryByUserId(profile2.getUser().getUserId(), activity3.getId());
        assertTrue(result.isEmpty());
    }


    @Test
    public void testUpdateUserActivityRole(){
        userActivityRoleRepository.updateUserActivityRole(ActivityRole.ORGANISER,profile2.getUser().getUserId(), activity2.getId());
        Optional <UserActivityRole> result = userActivityRoleRepository.getRoleEntryByUserId(profile2.getUser().getUserId(), activity2.getId());
        UserActivityRole object = result.get();
        assertEquals(ActivityRole.ORGANISER, object.getActivityRole());
    }

}
