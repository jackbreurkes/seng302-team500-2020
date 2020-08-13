package com.springvuegradle.model.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityOutcome;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ActivityOutcomeRepositoryTest {

	@Autowired
    private ActivityOutcomeRepository activityOutcomeRepository;
	
	@Autowired
	private ActivityRepository activityRepository;
	
	@Autowired
	private ProfileRepository profileRepo;
	
	Activity activity;
	
	@BeforeEach
    public void beforeEach() {
		Profile profile = new Profile(new User(), "Bob", "Builder", LocalDate.EPOCH, Gender.MALE);
		profile = profileRepo.save(profile);
		
		Activity activity = new Activity("climb mt everest", false, "mount everest", profile, new HashSet<ActivityType>());
		
		this.activity = activityRepository.save(activity);
    }
	
	@ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 5, 10, 100})
	public void testGettingMultipleOutcomesByList(int amount) {
		ArrayList<Long> ids = new ArrayList<Long>();
		
		for (int i = 0; i < amount; i++) {
			ActivityOutcome outcome = new ActivityOutcome("this is a description", "tests failing");
			outcome.setActivity(activity);
			outcome = activityOutcomeRepository.save(outcome);
			ids.add(outcome.getOutcomeId());
		}
		
		List<ActivityOutcome> outcomeList = activityOutcomeRepository.getOutcomesById(ids);
		
		assertEquals(ids.size(), outcomeList.size(), "Query returned a different number of activities than it was supposed to");
	}
	
	@ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 5, 10, 100})
	public void testGettingMultipleAccurateOutcomesByList(int amount) {
		ArrayList<Long> ids = new ArrayList<Long>();
		
		for (int i = 0; i < amount; i++) {
			ActivityOutcome outcome = new ActivityOutcome("this is a description", "tests failing");
			outcome.setActivity(activity);
			outcome = activityOutcomeRepository.save(outcome);
			ids.add(outcome.getOutcomeId());
		}
		
		for (int i = 0; i < 10; i++) {
			ActivityOutcome outcome = new ActivityOutcome("this is another activity outcome", "oh no");
			outcome.setActivity(activity);
			activityOutcomeRepository.save(outcome);
		}
		
		List<ActivityOutcome> outcomeList = activityOutcomeRepository.getOutcomesById(ids);
		
		assertEquals(ids.size(), outcomeList.size(), "Query returned a different number of activities than it was supposed to");
		
		for (ActivityOutcome outcome : outcomeList) {
			if (outcome.getUnits().equals("oh no")) {
				fail("Query returned an activity it was not supposed to");
			}
		}
	}
}
