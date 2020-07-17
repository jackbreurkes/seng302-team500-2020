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
    UserRepository userRepository;

    @Autowired
    ActivityTypeRepository activityTypeRepository;

    @Autowired
    EmailRepository emailRepository;

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
        User user1 = new User(1);
        user1 = userRepository.save(user1);
        User user2 = new User(2);
        user2 = userRepository.save(user2);
        User user3 = new User(3);
        user3 = userRepository.save(user3);
        User user4 = new User(4);
        user4 = userRepository.save(user4);
        User user5 = new User(5);
        user5 = userRepository.save(user5);
//        //userRepository.save(user1);
//        //userRepository.save(user2);
//        userRepository.save(user3);
//        userRepository.save(user4);
//        userRepository.save(user5);
        Profile profile1 = new Profile(user1, "Bill", "Testman", LocalDate.EPOCH, Gender.NON_BINARY);
        Profile profile2 = new Profile(user2, "Andy", "Warshaw", LocalDate.EPOCH, Gender.NON_BINARY);
        Profile profile3 = new Profile(user3, "Brie", "Calaris", LocalDate.EPOCH, Gender.NON_BINARY);
        Profile profile4 = new Profile(user4, "Jack", "Sandler", LocalDate.EPOCH, Gender.NON_BINARY);
        Profile profile5 = new Profile(user5, "Jill", "Sandler", LocalDate.EPOCH, Gender.NON_BINARY);
        Email email1 = new Email(user1, "primary@test.com", true);
        Email email2 = new Email(user2, "primary@1234.com", true);
        Email email3 = new Email(user1, "secondary@test.com", false);
        Email email4 = new Email(user3, "josh@josh.com", true);
        Email email5 = new Email(user1, "olivia@olivia.com", false);
        Email email6 = new Email(user1, "olivia@double.com", false);



        emailRepository.save(email1);
        emailRepository.save(email2);
        emailRepository.save(email3);
        emailRepository.save(email4);
        emailRepository.save(email5);
        emailRepository.save(email6);

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


    //EMAIL STUFF
    @ParameterizedTest
    @CsvSource({
            "primary@test.com,1",
            "primary@1234.com,2",
            "secondary@test.com,3",
            "josh@josh.com,4",
            "olivia@olivia.com,1",
            "olivia@double.com,1" // duplicate terms
    })
    public void retrieveSingleUserWithValidEmail(String input) {
        String[] splitString = input.split(",");
        String email = splitString[0];
        int userId = Integer.parseInt(splitString[1]);
        List<Email> result = emailRepository.findByEmailStartingWith(email);
        assertEquals(userId, result.get(0).getUser().getUserId());
    }
}
