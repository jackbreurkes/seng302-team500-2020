package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ActivityParticipantResultRepositoryTest {

    @Autowired
    ActivityParticipantResultRepository activityParticipantResultRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    ActivityOutcomeRepository activityOutcomeRepository;

    @Autowired
    ActivityTypeRepository activityTypeRepository;


    Profile profile1, profile2, profile3, profile4, profile5, profile6;
    Activity activity1, activity2, activity3;
    ActivityOutcome outcome1, outcome2, outcome3;
    ActivityParticipantResult participantResult1, participantResult2, participantResult3, participantResult4, participantResult5, participantResult6;

    @BeforeAll
    public void populateData(){
        //create and save Activity types
        ActivityType walking = new ActivityType("A");
        ActivityType running = new ActivityType("B");
        ActivityType biking = new ActivityType("D");
        activityTypeRepository.save(walking);
        activityTypeRepository.save(running);
        activityTypeRepository.save(biking);

        //create and save profiles
        profile1 = new Profile(new User(), "Bill", "Testman", LocalDate.EPOCH, Gender.NON_BINARY);
        profile2 = new Profile(new User(), "Andy", "Warshaw", LocalDate.EPOCH, Gender.NON_BINARY);
        profile3 = new Profile(new User(), "Brie", "Calaris", LocalDate.EPOCH, Gender.NON_BINARY);
        profile4 = new Profile(new User(), "hi", "hi", LocalDate.EPOCH, Gender.NON_BINARY);
        profile5 = new Profile(new User(), "yaaa", "eek", LocalDate.EPOCH, Gender.NON_BINARY);
        profile6 = new Profile(new User(), "New", "Fella", LocalDate.EPOCH, Gender.NON_BINARY);
        profileRepository.save(profile1);
        profileRepository.save(profile2);
        profileRepository.save(profile3);
        profileRepository.save(profile4);
        profileRepository.save(profile5);
        profileRepository.save(profile6);

        //create and save activities
        activity1 = new Activity("Run 100m", false, "New Zealand", profile1, Collections.singleton(walking));
        activity2 = new Activity("top speed ", false, "New Zealand", profile2, Collections.singleton(running));
        activity3 = new Activity("HOW MANY LEGS YA GOT?!??!", false, "New Zealand", profile3, Collections.singleton(biking));
        activityRepository.save(activity1);
        activityRepository.save(activity2);
        activityRepository.save(activity3);

        //create and save activity outcomes (The ones saved by creator)
        outcome1 = new ActivityOutcome("How long did it take you to run 100m", "seconds");
        outcome2 = new ActivityOutcome("How fast can u run", "km/h");
        outcome3 = new ActivityOutcome("How many legs can u run with", "legs");
        outcome1.setActivity(activity1);
        outcome2.setActivity(activity2);
        outcome3.setActivity(activity3);
        activityOutcomeRepository.save(outcome1);
        activityOutcomeRepository.save(outcome2);
        activityOutcomeRepository.save(outcome3);

        //create and save activity participant results (The ones saved by the participant)
        participantResult1 = new ActivityParticipantResult(profile1.getUser(),outcome1,"15", null);
        participantResult2 = new ActivityParticipantResult(profile2.getUser(),outcome2,"10000", null);
        participantResult3 = new ActivityParticipantResult(profile3.getUser(),outcome3,"2", null);
        participantResult4 = new ActivityParticipantResult(profile4.getUser(),outcome1,"20", null);
        participantResult5 = new ActivityParticipantResult(profile5.getUser(),outcome1,"10", null);
        participantResult6 = new ActivityParticipantResult(profile6.getUser(),outcome2,"50", null);
        activityParticipantResultRepository.save(participantResult1);
        activityParticipantResultRepository.save(participantResult2);
        activityParticipantResultRepository.save(participantResult3);

    }

    @Test
    public void testGetResultByUserAndActivityId() {
        Optional<ActivityParticipantResult> result = activityParticipantResultRepository.getParticipantResultByUserIdAndActivityId(profile1.getUser().getUserId(),activity1.getId());
        ActivityParticipantResult object = result.get();
        assertEquals(profile1.getUser().getUserId(), object.getUser().getUserId());
    }
    @Test
    public void testGetResultByNonExistentUserAndActivityId() {
        Optional<ActivityParticipantResult> result = activityParticipantResultRepository.getParticipantResultByUserIdAndActivityId(profile2.getUser().getUserId(),activity1.getId());
        assertTrue(result.isEmpty());
    }


}
