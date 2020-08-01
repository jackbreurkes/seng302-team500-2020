package com.springvuegradle.model.repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ActivityRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityTypeRepository activityTypeRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private Profile testCreator;
    private ActivityType testActivityType;

    @BeforeAll
    void beforeAll() {
        User testCreatorUser = new User();
        testCreator = new Profile(testCreatorUser, "Testy", "McTestFace", LocalDate.EPOCH, Gender.NON_BINARY);
        testCreator = profileRepository.save(testCreator);
        testActivityType = new ActivityType("Walking");
        testActivityType = activityTypeRepository.save(testActivityType);
    }

    @Test
    public void deleteActivity_DoesNotCascade() {
        Activity activity = new Activity("Test Activity", false, "New Zealand", testCreator, Collections.singleton(testActivityType));
        Activity savedActivity = activityRepository.save(activity);
        activityRepository.delete(savedActivity);
        Optional<Profile> savedCreator = profileRepository.findById(testCreator.getUser().getUserId());
        Optional<ActivityType> savedActivityType = activityTypeRepository.findById(testActivityType.getActivityTypeId());
        assertTrue(savedCreator.isPresent());
        assertTrue(savedActivityType.isPresent());
    }

}