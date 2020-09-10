package com.springvuegradle.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.springvuegradle.model.data.ActivityOutcome;

/**
 * Repository for ActivityOutcomes
 */
public interface ActivityOutcomeRepository extends JpaRepository<ActivityOutcome, Long> {

	/**
     * Named query for getting an activity result from the user and activity outcome
     * @param id of the user to get the result of
     * @return optional ActivityParticipantResult representing the result (can be empty)
     */
    @Query(
            value= "SELECT o FROM ActivityOutcome o WHERE o.outcomeId IN ?1"
    )
    public List<ActivityOutcome> getOutcomesById(List<Long> activityOutcomeIds);

}
