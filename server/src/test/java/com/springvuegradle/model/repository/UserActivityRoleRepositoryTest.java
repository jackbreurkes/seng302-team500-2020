package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @BeforeAll
    public void populateUsersAndActivities(){
        User user1 = new User(1L);
        User user2 = new User(2L);
        User user3 = new User(3L);

        //save users
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        ActivityType walking = new ActivityType("Walking");
        ActivityType running = new ActivityType("Running");
        ActivityType biking = new ActivityType("Biking");

        activityTypeRepository.save(walking);
        activityTypeRepository.save(running);
        activityTypeRepository.save(biking);

        Profile profile1 = new Profile(user1, "Bill", "Testman", LocalDate.EPOCH, Gender.NON_BINARY);
        Profile profile2 = new Profile(user2, "Andy", "Warshaw", LocalDate.EPOCH, Gender.NON_BINARY);
        Profile profile3 = new Profile(user3, "Brie", "Calaris", LocalDate.EPOCH, Gender.NON_BINARY);

        profileRepository.save(profile1);
        profileRepository.save(profile2);
        profileRepository.save(profile3);

        Activity activity1 = new Activity("Test Activity", false, "New Zealand", profileRepository.getOne(1L), Collections.singleton(walking));
        Activity activity2 = new Activity("Test Activity2", false, "New Zealand", profileRepository.getOne(2L), Collections.singleton(running));
        Activity activity3 = new Activity("Test Activity3", false, "New Zealand", profileRepository.getOne(3L), Collections.singleton(biking));

        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);

        //Adding associations
        UserActivityRole userActivityRole1 = new UserActivityRole(activity1, user1, ActivityRole.ORGANISER);

        UserActivityRole userActivityRole2 = new UserActivityRole(activity2, user2, ActivityRole.PARTICIPANT);
        UserActivityRole userActivityRole3 = new UserActivityRole(activity1, user2, ActivityRole.PARTICIPANT);

        UserActivityRole userActivityRole4 = new UserActivityRole(activity2, user3, ActivityRole.PARTICIPANT);

        userActivityRoleRepository.save(userActivityRole1);
        userActivityRoleRepository.save(userActivityRole2);
        userActivityRoleRepository.save(userActivityRole3);
        userActivityRoleRepository.save(userActivityRole4);
    }

    @Test
    public void testGetInvolvedActivitiesByUserId() {
        List<Activity> result = userActivityRoleRepository.getInvolvedActivitiesByUserId(2);
        assertEquals(2, result.size());
    }

}
