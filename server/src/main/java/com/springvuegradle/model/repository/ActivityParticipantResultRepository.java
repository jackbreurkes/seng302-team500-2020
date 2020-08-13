package com.springvuegradle.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.springvuegradle.model.data.ActivityOutcome;
import com.springvuegradle.model.data.ActivityParticipantResult;

/**
 * Repository for ActivityParticipantResults
 */
public interface ActivityParticipantResultRepository extends JpaRepository<ActivityParticipantResult, Long> {

	/**
     * Named query for getting an activity result from the user and activity outcome
     * @param id of the user to get the result of
     * @param activity outcome the result is against
     * @return optional ActivityParticipantResult representing the result (can be empty)
     */
    @Query(
            value= "SELECT a FROM ActivityParticipantResult a WHERE a.user.uuid = ?1 AND a.outcome = ?2"
    )
    public Optional<ActivityParticipantResult> getParticipantResult(long uuid, ActivityOutcome outcomeId);
}
