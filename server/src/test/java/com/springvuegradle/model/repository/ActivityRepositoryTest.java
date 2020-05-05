package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Disabled  // TODO enable this
class ActivityRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;

    private static Profile testCreator;

    @BeforeAll
    static void beforeAll() {
        User testCreatorUser = new User();
        testCreator = new Profile(testCreatorUser, "Testy", "McTestFace", LocalDate.EPOCH, Gender.NON_BINARY);
    }

    @Test
    public void testCreateActivity() {
        ActivityType activityType = new ActivityType("Walking");
        Activity activity = new Activity("Test Activity", false, "New Zealand", testCreator, Collections.singletonList(activityType));
        Activity savedActivity = activityRepository.save(activity);
        System.out.println(savedActivity.getId());
    }

}