package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.TestTransaction;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChangeLogRepositoryTest {

    @Autowired
    private ChangeLogRepository changeLogRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityTypeRepository activityTypeRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private Profile profile1;
    private Profile profile2;
    private List<Activity> activities = new ArrayList<>();


    @BeforeAll
    public void setupTests() {

        profile1 = new Profile(new User(), "firstName", "lastName", LocalDate.now(), Gender.NON_BINARY);
        profile1 = profileRepository.save(profile1);
        profile2 = new Profile(new User(), "firstName", "lastName", LocalDate.now(), Gender.NON_BINARY);
        profile2 = profileRepository.save(profile2);

        Set<ActivityType> activityTypes = new HashSet<>();
        activityTypes.add(activityTypeRepository.save(new ActivityType("Exhausting")));
        activityTypes.add(activityTypeRepository.save(new ActivityType("Slow")));

        String[] activityNames = {"Swimming", "Cycling", "Walking"};
        for (String activityName : activityNames) {
            Activity activity = new Activity(activityName, false, "A fake location", profile1, activityTypes);
            activity = activityRepository.save(activity);
            this.activities.add(activity);
        }
    }

    @Test
    public void testRetrieveUserHomeFeedUpdatesWithNoUpdates() {
        // Subscribe to the activity "Walking" which has no updates in the repository
        Subscription subscription = new Subscription(profile2, ChangeLogEntity.ACTIVITY, activities.get(2).getId());
        subscriptionRepository.save(subscription);

        List<ChangeLog> homeFeedUpdates = changeLogRepository.retrieveUserHomeFeedUpdates(profile2, Pageable.unpaged());
        assertEquals(0, homeFeedUpdates.size());
    }

    @Test
    public void testRetrieveUserHomeFeedUpdatesWithUpdates() {
        Activity swimming = activities.get(0);
        // Subscribe to the activity "Swimming"
        Subscription subscription = new Subscription(profile1, ChangeLogEntity.ACTIVITY, swimming.getId());
        subscriptionRepository.save(subscription);

        Profile editingUser = new Profile(new User(), "Editing", "Man", LocalDate.now(), Gender.NON_BINARY);
        editingUser = profileRepository.save(editingUser);

        ChangeLog change1 = new ChangeLog(ChangeLogEntity.ACTIVITY, swimming.getId(), ChangedAttribute.ACTIVITY_DESCRIPTION,
                profile1.getUser(), ActionType.UPDATED, "Old description", "New description");
        ChangeLog change2 = new ChangeLog(ChangeLogEntity.ACTIVITY, swimming.getId(), ChangedAttribute.ACTIVITY_LOCATION,
                editingUser.getUser(), ActionType.UPDATED, "Old location", "New location");
        change1 = changeLogRepository.save(change1);
        change2 = changeLogRepository.save(change2);

        List<ChangeLog> homeFeedUpdates = changeLogRepository.retrieveUserHomeFeedUpdates(profile1, Pageable.unpaged());
        assertEquals(2, homeFeedUpdates.size());
        assertEquals(change2.getChangeId(), homeFeedUpdates.get(0).getChangeId()); // top result is last saved
        assertEquals(change1.getChangeId(), homeFeedUpdates.get(1).getChangeId());
    }

    @Test
    public void testDeleteProfileWithChangeLog_SetsEditorToNull() {
        Profile profile = new Profile(new User(), "First", "Last", LocalDate.EPOCH, Gender.FEMALE);
        ChangeLog changeLog = new ChangeLog(ChangeLogEntity.ACTIVITY, 1L, ChangedAttribute.ACTIVITY_EXISTENCE, profile.getUser(), ActionType.CREATED, null, "Test Activity");
        profile = profileRepository.save(profile);
        changeLog = changeLogRepository.save(changeLog);
        TestTransaction.flagForCommit(); // need this, otherwise the next line does a rollback
        TestTransaction.end(); // ensures the test session is deleted before trying findById
        TestTransaction.start();

        changeLogRepository.clearEditorInformation(profile.getUser().getUserId());
        profileRepository.delete(profile);
        TestTransaction.flagForCommit(); // need this, otherwise the next line does a rollback
        TestTransaction.end(); // ensures the test session is deleted before trying findById
        TestTransaction.start();

        assertFalse(profileRepository.existsById(profile.getUser().getUserId()));
        ChangeLog updatedChangeLog = changeLogRepository.findById(changeLog.getChangeId()).orElse(null);
        assertNotNull(updatedChangeLog);
        assertNull(updatedChangeLog.getEditingUser());
    }

    @Test
    public void testRetrieveUserHomeFeed_Paginated_Succeeds() {
        Activity swimming = activities.get(0);
        // Subscribe to the activity "Swimming"
        Subscription subscription = new Subscription(profile1, ChangeLogEntity.ACTIVITY, swimming.getId());
        subscriptionRepository.save(subscription);

        Profile editingUser = new Profile(new User(), "Editing", "Man", LocalDate.EPOCH, Gender.NON_BINARY);
        editingUser = profileRepository.save(editingUser);

        OffsetDateTime paginationTime = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"));
        ChangeLog change1 = new ChangeLog(ChangeLogEntity.ACTIVITY, swimming.getId(), ChangedAttribute.ACTIVITY_DESCRIPTION,
                profile1.getUser(), ActionType.UPDATED, "Old description", "New description");
        ChangeLog change2 = new ChangeLog(ChangeLogEntity.ACTIVITY, swimming.getId(), ChangedAttribute.ACTIVITY_LOCATION,
                editingUser.getUser(), ActionType.UPDATED, "Old location", "New location");
        ChangeLog change3 = new ChangeLog(ChangeLogEntity.ACTIVITY, swimming.getId(), ChangedAttribute.ACTIVITY_NAME,
                profile1.getUser(), ActionType.UPDATED, "Old Name", "New Name");
        change1 = changeLogRepository.save(change1);
        change2 = changeLogRepository.save(change2);
        change3 = changeLogRepository.save(change3);

        // timestamps are set on initial save, so for testing must be manually overridden after saving
        change1.setTimestamp(paginationTime.minusDays(1));
        change2.setTimestamp(paginationTime);
        change3.setTimestamp(paginationTime.plusDays(1));
        change1 = changeLogRepository.save(change1);
        change2 = changeLogRepository.save(change2);
        change3 = changeLogRepository.save(change3);

        List<ChangeLog> homeFeedUpdates = changeLogRepository.retrieveUserHomeFeedUpdatesUpToTime(profile1, paginationTime, Pageable.unpaged());
        assertEquals(2, homeFeedUpdates.size());
        assertEquals(change2.getChangeId(), homeFeedUpdates.get(0).getChangeId()); // results sorted by timestamp
        assertEquals(change1.getChangeId(), homeFeedUpdates.get(1).getChangeId());
    }

}
