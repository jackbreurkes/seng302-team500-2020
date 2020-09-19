package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivitySearchRepository {
    List<Activity> findUniqueActivitiesByListOfNames(List<String> partialNames, Pageable pageable);
}