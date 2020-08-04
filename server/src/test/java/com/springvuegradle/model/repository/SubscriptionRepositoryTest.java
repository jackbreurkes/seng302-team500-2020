package com.springvuegradle.model.repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.HomefeedEntityType;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.Subscription;
import com.springvuegradle.model.data.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubscriptionRepositoryTest {

	@Autowired
	SubscriptionRepository subscriptionRepo;
	
	@Autowired
	ProfileRepository profileRepo;
	
	@Autowired
	ActivityRepository activityRepo;
	
	@Autowired
	ActivityTypeRepository activityTypeRepo;
	
	Profile testProfile;
	Map<String, ActivityType> activityTypes;
	
	@BeforeAll
    public void setupTests() {
		
		activityTypes = new HashMap<String, ActivityType>();
		for (String activityTypeName : new String[] {"kayaking", "walking", "coding", "building"}) {
			ActivityType activityType = new ActivityType(activityTypeName);
			activityTypes.put(activityTypeName, activityType);
			activityTypeRepo.save(activityType);
        }
		
		
		testProfile = new Profile(new User(), "Bob","Builder", LocalDate.of(2000, 10, 15), Gender.MALE);
		testProfile = profileRepo.save(testProfile);
	}
	
	@Test
	public void testSubscribingToOurOwnActivity() {
		HashSet<ActivityType> types = new HashSet<ActivityType>();
		types.add(activityTypes.get("building"));
		Activity activity = new Activity("Sandcastle making competition", 
				false, "Aoteroa", testProfile, types);
		
		activityRepo.save(activity);
		
		Subscription subscription = new Subscription(testProfile, HomefeedEntityType.ACTIVITY, activity.getId());
		subscriptionRepo.save(subscription);
		
		List<Subscription> savedSubscriptions = subscriptionRepo.findSubscriptionsByUser(testProfile);
		Assertions.assertEquals(1, savedSubscriptions.size());
		Assertions.assertEquals(activity.getId(), savedSubscriptions.get(0).getEntityId());
	}
	
	@Test
	public void testSubscribingToAnotherPersonsActivity() {
		Profile testProfile2 = new Profile(new User(), "Lisa","Simpson", LocalDate.of(1981, 5, 9), Gender.FEMALE);
		testProfile2 = profileRepo.save(testProfile2);
		
		HashSet<ActivityType> types = new HashSet<ActivityType>();
		types.add(activityTypes.get("building"));
		Activity activity = new Activity("Sandcastle destroying", 
				false, "Aoteroa", testProfile, types);
		
		activityRepo.save(activity);
		
		Subscription subscription = new Subscription(testProfile2, HomefeedEntityType.ACTIVITY, activity.getId());
		subscriptionRepo.save(subscription);
		
		List<Subscription> savedSubscriptions = subscriptionRepo.findSubscriptionsByUser(testProfile2);
		Assertions.assertEquals(1, savedSubscriptions.size());
		Assertions.assertEquals(activity.getId(), savedSubscriptions.get(0).getEntityId());
	}
	
	@Test
	public void testUnsubscribingToOurOwnActivity() {
		Profile testProfile2 = new Profile(new User(), "Lisa","Simpson", LocalDate.of(1981, 5, 9), Gender.FEMALE);
		testProfile2 = profileRepo.save(testProfile2);
		
		HashSet<ActivityType> types = new HashSet<ActivityType>();
		types.add(activityTypes.get("building"));
		Activity activity = new Activity("Sandcastle appreciation", 
				false, "Aoteroa", testProfile2, types);
		
		activityRepo.save(activity);
		
		Subscription subscription = new Subscription(testProfile2, HomefeedEntityType.ACTIVITY, activity.getId());
		subscriptionRepo.save(subscription);
		
		List<Subscription> savedSubscriptions = subscriptionRepo.findSubscriptionsByUser(testProfile2);
		Assertions.assertEquals(1, savedSubscriptions.size());
		Assertions.assertEquals(activity.getId(), savedSubscriptions.get(0).getEntityId());
		
		//the unsubscribing part:
		subscriptionRepo.delete(savedSubscriptions.get(0));
		savedSubscriptions = subscriptionRepo.findSubscriptionsByUser(testProfile2);
		Assertions.assertEquals(0, savedSubscriptions.size());
	}
	
	@Test
	public void testUnsubscribingSomeoneElsesActivity() {
		Profile testProfile2 = new Profile(new User(), "Lisa","Simpson", LocalDate.of(1981, 5, 9), Gender.FEMALE);
		testProfile2 = profileRepo.save(testProfile2);
		
		HashSet<ActivityType> types = new HashSet<ActivityType>();
		types.add(activityTypes.get("building"));
		Activity activity = new Activity("Sandcastle appreciation", 
				false, "Aoteroa", testProfile, types);
		
		activityRepo.save(activity);
		
		Subscription subscription = new Subscription(testProfile2, HomefeedEntityType.ACTIVITY, activity.getId());
		subscriptionRepo.save(subscription);
		
		List<Subscription> savedSubscriptions = subscriptionRepo.findSubscriptionsByUser(testProfile2);
		Assertions.assertEquals(1, savedSubscriptions.size());
		Assertions.assertEquals(activity.getId(), savedSubscriptions.get(0).getEntityId());
		
		//the unsubscribing part:
		subscriptionRepo.delete(savedSubscriptions.get(0));
		savedSubscriptions = subscriptionRepo.findSubscriptionsByUser(testProfile2);
		Assertions.assertEquals(0, savedSubscriptions.size());
	}
	
	@Test
	public void testFindSubscriptionsNoFalsePositives() {
		Profile testProfile2 = new Profile(new User(), "Lisa","Simpson", LocalDate.of(1981, 5, 9), Gender.FEMALE);
		testProfile2 = profileRepo.save(testProfile2);
		
		HashSet<ActivityType> types = new HashSet<ActivityType>();
		types.add(activityTypes.get("building"));
		Activity activity = new Activity("Sandcastle eating competition", 
				false, "Aoteroa", testProfile, types);
		
		Activity activity2 = new Activity("Sandcastle eating competition", 
				false, "Aoteroa", testProfile2, types);
		
		activityRepo.save(activity);
		activityRepo.save(activity2);
		
		List<Subscription> savedSubscriptions = subscriptionRepo.findSubscriptionsByUser(testProfile2);
		Assertions.assertEquals(0, savedSubscriptions.size());
	}
	
	@Test
	public void testUnsubscribeOnlyUnsubscribesOne() {
		Profile testProfile2 = new Profile(new User(), "Lisa","Simpson", LocalDate.of(1981, 5, 9), Gender.FEMALE);
		testProfile2 = profileRepo.save(testProfile2);
		
		HashSet<ActivityType> types = new HashSet<ActivityType>();
		types.add(activityTypes.get("building"));
		Activity activity = new Activity("Sandcastle javelin throw", 
				false, "Aoteroa", testProfile, types);
		
		Activity activity2 = new Activity("Sandcastle javelin throw", 
				false, "Aoteroa", testProfile, types);
		
		activityRepo.save(activity);
		activityRepo.save(activity2);
		
		Subscription subscription = new Subscription(testProfile2, HomefeedEntityType.ACTIVITY, activity.getId());
		subscriptionRepo.save(subscription);
		Subscription subscription2 = new Subscription(testProfile2, HomefeedEntityType.ACTIVITY, activity2.getId());
		subscriptionRepo.save(subscription2);
		
		List<Subscription> savedSubscriptions = subscriptionRepo.findSubscriptionsByUser(testProfile2);
		Assertions.assertEquals(2, savedSubscriptions.size());
		
		subscriptionRepo.delete(subscription);
		
		savedSubscriptions = subscriptionRepo.findSubscriptionsByUser(testProfile2);
		Assertions.assertEquals(1, savedSubscriptions.size());
		Assertions.assertEquals(subscription2.getEntityId(), savedSubscriptions.get(0).getEntityId());
	}
}
