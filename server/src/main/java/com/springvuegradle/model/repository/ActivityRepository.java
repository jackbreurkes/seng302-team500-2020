package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * repository for accessing stored information about activities.
 * extends the custom interface ActivitySearchRepository.
 */
public interface ActivityRepository extends JpaRepository<Activity, Long>, ActivitySearchRepository {

    /**
     * Find all activities created by the given profile.
     * @param creator profile of the person whose activities should be returned
     * @return all activities created by the given profile
     */
    List<Activity> findActivitiesByCreator(Profile creator);

}
