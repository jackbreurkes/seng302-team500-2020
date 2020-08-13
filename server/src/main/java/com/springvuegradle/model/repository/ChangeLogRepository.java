package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.ChangeLog;
import com.springvuegradle.model.data.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    /**
     * Retrieve all change log entries for entities which the user is subscribed to
     * @param profile the profile of the user whose home feed will be retrieved
     * @return a List of ChangeLog entities representing changes to the entities which the user is subscribed to
     */
    @Query(value = "SELECT DISTINCT c FROM ChangeLog c JOIN Subscription s ON c.entityId = s.entityId AND s.subscriber = ?1 " +
            "ORDER BY c.timestamp DESC")
    List<ChangeLog> retrieveUserHomeFeedUpdates(Profile profile, Pageable pageable);

    @Query(value = "select distinct c from ChangeLog c JOIN Subscription s ON c.entityId = s.entityId AND s.subscriber = :profile " +
            "WHERE c.timestamp < :latestTimestamp " +
            "ORDER BY c.timestamp DESC")
    List<ChangeLog> retrieveUserHomeFeedUpdatesBeforeTime(Profile profile, ZonedDateTime latestTimestamp, Pageable pageable);
}