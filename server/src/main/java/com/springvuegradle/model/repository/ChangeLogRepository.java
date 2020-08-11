package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.ChangeLog;
import com.springvuegradle.model.data.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    /**
     * Retrieve all change log entries for entities which the user is subscribed to
     * @param profile the profile of the user whose home feed will be retrieved
     * @return a List of ChangeLog entities representing changes to the entities which the user is subscribed to
     */
    @Query(value = "select distinct c from ChangeLog c where c.entityId in (select s.entityId from Subscription s where s.subscriber = ?1)")
    public List<ChangeLog> retrieveUserHomeFeedUpdates(Profile profile);
}