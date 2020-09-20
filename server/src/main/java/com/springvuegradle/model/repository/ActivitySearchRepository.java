package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * custom interface to declare the search activities method.
 * declared outside of ActivityRepository to avoid implementations having to override all JpaRepository methods.
 */
public interface ActivitySearchRepository {

    /**
     * finds the list of activities that match any one of the search terms given.
     * search is case insensitive.
     * results are ordered based on the number of search terms they match, and then alphabetically.
     * @param searchTerms the list of terms to use when finding activities by name
     * @param pageable a pageable object to represent the section of query results to return
     * @return a List of activities returned by the search
     */
    List<Activity> findUniqueActivitiesByListOfNames(List<String> searchTerms, Pageable pageable);
}