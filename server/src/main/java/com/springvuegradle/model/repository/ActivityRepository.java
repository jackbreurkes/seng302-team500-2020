package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * repository for accessing stored information about activities.
 * extends the custom interface ActivitySearchRepository.
 */
public interface ActivityRepository extends JpaRepository<Activity, Long>, ActivitySearchRepository {
    List<Activity> findActivitiesByCreator(Profile creator);
}
