package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfileRepositoryTest {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ActivityTypeRepository activityTypeRepository;

    @BeforeAll
    public void createTestProfiles() {
        ActivityType walking = new ActivityType("Walking");
        ActivityType running = new ActivityType("Running");
        ActivityType biking = new ActivityType("Biking");
        ActivityType skiing = new ActivityType("Skiing");
        ActivityType tramping = new ActivityType("Tramping");
        ActivityType scootering = new ActivityType("Scootering");
        activityTypeRepository.save(walking);
        activityTypeRepository.save(running);
        activityTypeRepository.save(biking);
        activityTypeRepository.save(skiing);
        activityTypeRepository.save(tramping);
        activityTypeRepository.save(scootering);

        Profile profile1 = new Profile(new User(), "Bill", "Testman", LocalDate.EPOCH, Gender.NON_BINARY);
        Profile profile2 = new Profile(new User(), "Andy", "Warshaw", LocalDate.EPOCH, Gender.NON_BINARY);
        Profile profile3 = new Profile(new User(), "Brie", "Calaris", LocalDate.EPOCH, Gender.NON_BINARY);
        Profile profile4 = new Profile(new User(), "Jack", "Sandler", LocalDate.EPOCH, Gender.NON_BINARY);
        Profile profile5 = new Profile(new User(), "Jill", "Sandler", LocalDate.EPOCH, Gender.NON_BINARY);
        profile1.setActivityTypes(Arrays.asList(
                walking
        ));
        profile2.setActivityTypes(Arrays.asList(
                walking, running
        ));
        profile3.setActivityTypes(Arrays.asList(
                walking, running, biking
        ));
        profile4.setActivityTypes(Arrays.asList(
                tramping
        ));
        profile5.setActivityTypes(Arrays.asList(
                scootering
        ));
        profileRepository.save(profile1);
        profileRepository.save(profile2);
        profileRepository.save(profile3);
        profileRepository.save(profile4);
        profileRepository.save(profile5);
    }

    @ParameterizedTest
    @CsvSource({
            "Walking,3",
            "Running,2",
            "Biking,1",
            "Skiing,0",
            "Walking Skiing Biking Running,3",
            "Running Biking,2",
            "Tramping Walking,4",
            "Running Running,2" // duplicate terms
    })
    public void searchForActivityTypeContainingAnyOf(String spaceSeparatedActivityTypes, String expectedCount) {
        List<String> activityTypes = Arrays.asList(spaceSeparatedActivityTypes.split(" "));
        List<Profile> result = profileRepository.findByActivityTypesContainsAnyOf(activityTypes);
        assertEquals(Integer.parseInt(expectedCount), result.size(), "search for " + spaceSeparatedActivityTypes + " (OR method)");
    }

    @ParameterizedTest
    @CsvSource({
            "Walking,3",
            "Running,2",
            "Biking,1",
            "Skiing,0",
            "Walking Skiing Biking Running,0",
            "Running Biking,1",
            "Walking Running,2",
            "Tramping Walking,0",
            "Walking Walking,3" // duplicate terms
    })
    public void searchForActivityTypeContainingAllOf(String spaceSeparatedActivityTypes, String expectedCount) {
        List<String> activityTypes = Arrays.asList(spaceSeparatedActivityTypes.split(" "));
        List<Profile> result = profileRepository.findByActivityTypesContainsAllOf(activityTypes);
        assertEquals(Integer.parseInt(expectedCount), result.size());
    }

    @Test
    public void searchForActivityTypeContainingAnyOf_EmptySearchArray() {
        List<String> activityTypes = new ArrayList<>();
        List<Profile> result = profileRepository.findByActivityTypesContainsAnyOf(activityTypes);
        assertEquals(0, result.size());
    }

    @Test
    public void searchForActivityTypeContainingAllOf_EmptySearchArray() {
        List<String> activityTypes = new ArrayList<>();
        List<Profile> result = profileRepository.findByActivityTypesContainsAllOf(activityTypes);
        assertEquals(0, result.size());
    }

    @Test
    public void searchForActivityTypeContainingAnyOf_EmptyString() {
        List<String> activityTypes = Collections.singletonList("");
        List<Profile> result = profileRepository.findByActivityTypesContainsAnyOf(activityTypes);
        assertEquals(0, result.size());
    }

    @Test
    public void searchForActivityTypeContainingAllOf_EmptyString() {
        List<String> activityTypes = Collections.singletonList("");
        List<Profile> result = profileRepository.findByActivityTypesContainsAllOf(activityTypes);
        assertEquals(0, result.size());
    }

}
