package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ActivitySearchRepositoryImplTest {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ActivityTypeRepository activityTypeRepository;

    @Autowired
    ProfileRepository profileRepository;

    ActivityType activityType;

    @BeforeAll
    void beforeAll(){
        activityType = activityTypeRepository.save(new ActivityType("Walkin"));
    }

    Activity createActivityWithName(String name) {
        Profile creator = new Profile(new User(), "Fname", "Lname", LocalDate.EPOCH, Gender.MALE);
        profileRepository.save(creator);
        Activity activity = new Activity(name, false, "Location", creator, Set.of(activityType));
        return activityRepository.save(activity);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "This",
            "is",
            "a",
            "fun",
            "run"
    })
    void testSingleWord_singleResult(String searchTerm) {
        String activityName = "This is a fun run";
        createActivityWithName(activityName);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(Collections.singletonList(searchTerm), PageRequest.of(0, 25));

        assertEquals(1, results.size());
        assertEquals(activityName, results.get(0).getActivityName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "This is a fun run",
            "Test this activity",
            "Test Test Test",
            "oneword"
    })
    void testSearchByAllWordsInName_singleResult(String activityName) {
        List<String> searchTerms = Arrays.asList(activityName.split(" "));
        createActivityWithName(activityName);
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(searchTerms, PageRequest.of(0, 25));

        assertEquals(1, results.size());
        assertEquals(activityName, results.get(0).getActivityName());
    }

    @Test
    void testSingleWord_mulitpleResults(){
        createActivityWithName("Test activity");
        createActivityWithName("Test activity two");
        List<Activity> results = activityRepository.findUniqueActivitiesByListOfNames(Collections.singletonList("Test"), PageRequest.of(0, 25));

        assertEquals(2, results.size());
        //assertEquals("Test activity", results.get(0).getActivityName());
        //assertEquals("Test activity two", results.get(1).getActivityName());
    }

}