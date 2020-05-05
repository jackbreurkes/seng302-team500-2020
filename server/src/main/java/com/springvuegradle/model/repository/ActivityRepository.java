package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ActivityRepository extends JpaRepository<Activity, Long> {
    public List<Activity> findActivitiesByCreator(Profile creator);
}
