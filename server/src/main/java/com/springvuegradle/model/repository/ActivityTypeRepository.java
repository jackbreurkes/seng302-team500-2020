package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {

    public Optional<ActivityType> getActivityTypeByActivityTypeName(String name);

}
