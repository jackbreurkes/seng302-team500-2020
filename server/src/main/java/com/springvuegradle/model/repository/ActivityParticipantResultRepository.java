package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.ActivityParticipantResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ActivityParticipantResults
 */
    public interface ActivityParticipantResultRepository extends JpaRepository<ActivityParticipantResult, Long> {

        /**
         * Named query for getting an activity result from the user and activity outcome
         * @param profileId of the user to get the result of
         * @param outcomeId activity outcome the result is against
         * @return optional ActivityParticipantResult representing the result (can be empty)
         */
        @Query(
                value= "SELECT a FROM ActivityParticipantResult a WHERE a.user.uuid = ?1 AND a.outcome.outcomeId = ?2"
        )
        Optional<ActivityParticipantResult> getParticipantResult(long profileId, long outcomeId);

    /**
     * returns the list of results a user has logged against a particular activity
     * @param profileId The user that has created the result
     * @param activityId the activity that the result is related to
     * @return optional ActivityParticipantResult representing the result (can be empty)
     */
    @Query(
                value = "SELECT r FROM ActivityParticipantResult r JOIN ActivityOutcome o ON r.outcome.outcomeId = o.outcomeId WHERE r.user.uuid = ?1 AND o.activity.activity_id = ?2"
        )
    List<ActivityParticipantResult> getParticipantResultsByUserIdAndActivityId(long profileId, long activityId);

    int countActivityParticipantResultByOutcomeOutcomeId(long outcomeId);

}
