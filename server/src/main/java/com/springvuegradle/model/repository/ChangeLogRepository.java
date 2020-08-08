package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {
    public List<ChangeLog> retrieveUserHomeFeedUpdates(long userId);
}