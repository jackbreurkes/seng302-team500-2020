package com.springvuegradle.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.ActivityParticipantResult;

/**
 * Repository for ActivityParticipantResults
 */
public interface ActivityParticipantResultRepository extends JpaRepository<ActivityParticipantResult, Long> {

}
