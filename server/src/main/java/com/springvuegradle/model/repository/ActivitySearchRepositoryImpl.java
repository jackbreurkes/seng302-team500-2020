package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * custom implementation for the activity search repository interface.
 * will be used when autowiring an ActivityRepository.
 */
@Repository
public class ActivitySearchRepositoryImpl implements ActivitySearchRepository{

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * finds the list of activities that match any one of the search terms given.
     * search is case insensitive.
     * results are ordered based on the number of search terms they match, and then alphabetically.
     * @param searchTerms the list of terms to use when finding activities by name
     * @param pageable a pageable object to represent the section of query results to return
     * @return a List of activities returned by the search
     */
    public List<Activity> findUniqueActivitiesByListOfNames(List<String> searchTerms, Pageable pageable){
        if (searchTerms.isEmpty()) {
            return new ArrayList<>();
        }

        final String searchForNameNestedQuery = " (SELECT b.activity_id, b.activity_name FROM Activity b WHERE LOWER(b.activity_name) LIKE LOWER( ?%1$d ) ) ";
        StringBuilder queryBuilder = new StringBuilder("SELECT a.* FROM (SELECT activity_id, count(activity_id) AS matches FROM ( ");
        queryBuilder.append(String.format(searchForNameNestedQuery, 1));
        for (int i = 2; i < searchTerms.size() + 1; i++) {
            queryBuilder.append(" UNION ALL ");
            queryBuilder.append(String.format(searchForNameNestedQuery, i));
        }

        queryBuilder.append(" ) GROUP BY activity_id) result NATURAL JOIN Activity a GROUP BY a.activity_id ORDER BY matches DESC, a.activity_name ASC");
        Query query = entityManager.createNativeQuery(queryBuilder.toString(), Activity.class);
        for (int i = 1; i < searchTerms.size() + 1; i++) {
            query.setParameter(i, "%" + searchTerms.get(i - 1) + "%");
        }
        if (pageable.isPaged()) {
            final long offset = pageable.getOffset();
            final int pageSize = pageable.getPageSize();
            query.setFirstResult((int) offset);
            query.setMaxResults(pageSize);
        }
        return (List<Activity>) query.getResultList();
    }
}
