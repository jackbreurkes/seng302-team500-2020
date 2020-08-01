package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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
        profile1.setMiddleName("James");
        profile1.setNickName("Brillybill");
        profile2.setActivityTypes(Arrays.asList(
                walking, running
        ));
        profile2.setMiddleName("Andrew");
        profile2.setNickName("Andyc123");
        profile3.setActivityTypes(Arrays.asList(
                walking, running, biking
        ));
        profile3.setMiddleName("12345");
        profile3.setNickName("Briecheese");
        profile4.setActivityTypes(Arrays.asList(
                tramping
        ));
        profile4.setNickName("JacknJill");
        profile5.setActivityTypes(Arrays.asList(
                scootering
        ));
        profile5.setNickName("JacknJill");
        profileRepository.save(profile1);
        profileRepository.save(profile2);
        profileRepository.save(profile3);
        profileRepository.save(profile4);
        profileRepository.save(profile5);
    }
    // TEST FOR ACTIVITIES
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
    //TEST FOR NICKNAMES
    @Test
    public void retrieveSingleUserWithPartialValidNickname() {
        String nickName = "And";
        List<Profile> result = profileRepository.findByNickNameStartingWith(nickName);
        assertEquals(1, result.size());
    }
    @Test
    public void retrieveSingleUserWithFullValidNickname() {
        String nickName = "Andyc123";
        List<Profile> result = profileRepository.findByNickNameStartingWith(nickName);
        assertEquals(1, result.size());
    }
    @Test
    public void retrieveMultipleUsersWithPartialValidNickname() {
        String nickName = "Br";
        List<Profile> result = profileRepository.findByNickNameStartingWith(nickName);
        assertEquals(2, result.size());
    }
    @Test
    public void retrieveNoUsersWithInvalidNickname() {
        String nickName = "INVALIDNICKNAME";
        List<Profile> result = profileRepository.findByNickNameStartingWith(nickName);
        assertEquals(0, result.size());
    }
    @Test
    public void retrieveMultipleUsersWithFullValidNickname() {
        String nickName = "JacknJill";
        List<Profile> result = profileRepository.findByNickNameStartingWith(nickName);
        assertEquals(2, result.size());
    }
    @Test
    public void retrieveSingleUserWithPartialInvalidCaseSensitiveNickname() {
        String nickName = "brILLy";
        List<Profile> result = profileRepository.findByNickNameStartingWith(nickName);
        assertEquals(0, result.size());
    }
    //TESTS FOR NAMING
    @ParameterizedTest
    @CsvSource({
            "Bill,Testman,8",
            "Andy,Warshaw,9",
            "Brie,Calaris,10",
            "Jack,Sandler,11",
            "Jill,Sandler,12",
    })
    public void searchForUsersGivenFullFirstLastNames(String Fname, String Lname, String userId) {
        Long userIdNum = Long.parseLong(userId);
        List<Profile> result = profileRepository.findByFirstNameStartingWithAndLastNameStartingWith(Fname, Lname);
        assertEquals(1, result.size());
        //assertEquals(userIdNum, result.get(0).getUser().getUserId());
    }
    @ParameterizedTest
    @CsvSource({
            "Bi,Te,8",
            "A,W,9",
            "Br,Cal,10",
            "Jack,San,11",
            "Ji,Sandler,12",
    })
    public void searchForUsersGivenPartialFirstLastNames(String Fname, String Lname, String userId) {
        Long userIdNum = Long.parseLong(userId);
        List<Profile> result = profileRepository.findByFirstNameStartingWithAndLastNameStartingWith(Fname, Lname);
        assertEquals(1, result.size());
        //assertEquals(userIdNum, result.get(0).getUser().getUserId());
    }
    @Test
    public void searchForUsersGivenIncorrectPartialFirstLastNames() {
        String Fname = "QQQ";
        String Lname = "YYY";
        List<Profile> result = profileRepository.findByFirstNameStartingWithAndLastNameStartingWith(Fname, Lname);
        assertEquals(0, result.size());
    }
    @Test
    public void searchForUsersGivenIncorrectFullFirstLastNames() {
        String Fname = "Joshua";
        String Lname = "Joshua";
        List<Profile> result = profileRepository.findByFirstNameStartingWithAndLastNameStartingWith(Fname, Lname);
        assertEquals(0, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "B,",
            "Andy,",
    })
    public void searchForUsersGivenOnlyFullAndPartialFirstName(String Fname, String Lname) {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> profileRepository.findByFirstNameStartingWithAndLastNameStartingWith(Fname, Lname));
    }
    @ParameterizedTest
    @CsvSource({
            ",Tes",
            ",WarShaw",
    })
    public void searchForUsersGivenOnlyFullAndPartialLastName(String Fname, String Lname) {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> profileRepository.findByFirstNameStartingWithAndLastNameStartingWith(Fname, Lname));
    }
}
