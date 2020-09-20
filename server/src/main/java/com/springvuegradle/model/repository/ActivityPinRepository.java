package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.ActivityPin;
import com.springvuegradle.model.data.ActivityType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository for retrieving information on ActivityPins from the database.
 */
public interface ActivityPinRepository extends JpaRepository<ActivityPin, Long> {

    /**
     * Query for finding activityPins within a given bounding box, north east point should always be greater than south
     * west point
     * @param northEastLat latitude of the northeastern corner of the bounding box
     * @param northEastLon longitude of the northeastern corner of the bounding box
     * @param southWestLat latitude of the southwestern corner of the bounding box
     * @param southWestLon longitude of the southwestern corner of the bounding box
     * @param pageable pagination object for this query
     * @return a list of activity pins that are within the requested bounds
     */
    @Query("SELECT p FROM ActivityPin p WHERE p.latitude <= :northEastLat " +
            "AND p.longitude <= :northEastLon " +
            "AND p.latitude > :southWestLat " +
            "AND p.longitude > :southWestLon")
    List<ActivityPin> findPinsInBounds(float northEastLat, float northEastLon, float southWestLat, float southWestLon, Pageable pageable);
}
