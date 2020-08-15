package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.ChangeLog;
import com.springvuegradle.model.data.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    /**
     * Retrieve all change log entries for entities which the user is subscribed to
     * @param profile the profile of the user whose home feed will be retrieved
     * @param pageable pagination information for this query. should not be offset.
     * @return a List of ChangeLog entities representing changes to the entities which the user is subscribed to
     */
    @Query(value = "SELECT DISTINCT c FROM ChangeLog c JOIN Subscription s ON c.entityId = s.entityId AND s.subscriber = ?1 " +
            "ORDER BY c.timestamp DESC")
    List<ChangeLog> retrieveUserHomeFeedUpdates(Profile profile, Pageable pageable);

    /**
     * retrieves all change log entries that are timestamped before or at the given time
     * for entities which the given profile is subscribed to.
     * @param profile the profile of the user whose home feed will be retrieved
     * @param latestTimestamp the timestamp after which no changelogs should be returned
     * @param pageable pagination information for this query. should not be offset.
     * @return
     */
    @Query(value = "select distinct c from ChangeLog c JOIN Subscription s ON c.entityId = s.entityId AND s.subscriber = ?1 " +
            "WHERE c.timestamp <= ?2 " +
            "ORDER BY c.timestamp DESC")
    List<ChangeLog> retrieveUserHomeFeedUpdatesUpToTime(Profile profile, OffsetDateTime latestTimestamp, Pageable pageable);

    /**
     * clears the editingUser information from changelogs edited by a user who is about to be deleted.
     * @param profileId the profile whose changes should have their editingUser field set to NULL
     */
    @Transactional
    @Modifying
    @Query(value = "update ChangeLog c SET c.editingUser = null WHERE c.editingUser.uuid = :profileId")
    void clearEditorInformation(long profileId);
}