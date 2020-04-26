package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ActivityRepository extends JpaRepository<Activity, Long> {

}
