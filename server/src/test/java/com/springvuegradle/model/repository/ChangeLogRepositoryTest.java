package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
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
        Subscription subscription = new Subscription(profile2, HomefeedEntityType.ACTIVITY, activities.get(2).getId());
        subscriptionRepository.save(subscription);

        List<ChangeLog> homeFeedUpdates = changeLogRepository.retrieveUserHomeFeedUpdates(profile2);
        Assertions.assertEquals(0, homeFeedUpdates.size());
    }

    @Test
    public void testRetrieveUserHomeFeedUpdatesWithUpdates() {
        Activity swimming = activities.get(0);
        // Subscribe to the activity "Swimming"
        Subscription subscription = new Subscription(profile1, HomefeedEntityType.ACTIVITY, swimming.getId());
        subscriptionRepository.save(subscription);

        Profile editingUser = new Profile(new User(), "Editing", "Man", LocalDate.now(), Gender.NON_BINARY);
        editingUser = profileRepository.save(editingUser);

        ChangeLog change1 = new ChangeLog(ChangeLogEntity.ACTIVITY, swimming.getId(), ChangedAttribute.ACTIVITY_DESCRIPTION,
                profile1.getUser(), ActionType.UPDATED, "Old description", "New description");
        ChangeLog change2 = new ChangeLog(ChangeLogEntity.ACTIVITY, swimming.getId(), ChangedAttribute.ACTIVITY_LOCATION,
                editingUser.getUser(), ActionType.UPDATED, "Old location", "New location");
        change1 = changeLogRepository.save(change1);
        change2 = changeLogRepository.save(change2);

        List<ChangeLog> homeFeedUpdates = changeLogRepository.retrieveUserHomeFeedUpdates(profile1);
        Assertions.assertEquals(2, homeFeedUpdates.size());
        Assertions.assertEquals(change1.getChangeId(), homeFeedUpdates.get(0).getChangeId());
        Assertions.assertEquals(change2.getChangeId(), homeFeedUpdates.get(1).getChangeId());
    }

}
