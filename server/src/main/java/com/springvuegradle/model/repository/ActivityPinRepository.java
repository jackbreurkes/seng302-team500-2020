package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.ActivityPin;
import com.springvuegradle.model.data.GeoPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityPinRepository extends JpaRepository<ActivityPin, Long> {

    /**
     * Query for finding activityPins within a given bounding box, north east point should always be greater than south
     * west point
     * @param northEastLat latitude of the northeastern corner of the bounding box
     * @param northEastLon longitude of the northeastern corner of the bounding box
     * @param southWestLat latitude of the southwestern corner of the bounding box
     * @param southWestLon longitude of the southwestern corner of the bounding box
     * @return
     */
    @Query("SELECT p FROM ActivityPin p WHERE p.latitude <= :northEastLat " +
            "AND p.longitude <= :northEastLon " +
            "AND p.latitude > :southWestLat " +
            "AND p.longitude > :southWestLon")
    List<ActivityPin> findPinsInBounds(float northEastLat, float northEastLon, float southWestLat, float southWestLon);
}
