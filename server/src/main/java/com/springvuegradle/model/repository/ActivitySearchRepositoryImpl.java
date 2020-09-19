package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActivitySearchRepositoryImpl implements ActivitySearchRepository{

    @PersistenceContext
    private EntityManager entityManager;

    public List<Activity> findUniqueActivitiesByListOfNames(List<String> partialNames, Pageable pageable){
        if (partialNames.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder queryBuilder = new StringBuilder("SELECT a FROM Activity a WHERE a.activityName LIKE ?1 ");
        for (int i = 2; i < partialNames.size() + 1; i++) {
            queryBuilder.append(String.format("OR activity_name LIKE ?%d ", i));
        }
        TypedQuery<Activity> query = entityManager.createQuery(queryBuilder.toString(), Activity.class);
        for (int i = 1; i < partialNames.size() + 1; i++) {
            query.setParameter(i, "%" + partialNames.get(i - 1) + "%");
        }
        if (pageable.isPaged()) {
            final long offset = pageable.getOffset();
            final int pageSize = pageable.getPageSize();
            query.setFirstResult((int) offset);
            query.setMaxResults(pageSize);
        }

        // A * O(m)

        return query.getResultList();
    }

    //future implementation of method with ordering
/*    public List<Activity> findUniqueActivitiesByListOfNames(List<String> partialNames, Pageable pageable){
        if (partialNames.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder queryBuilder = new StringBuilder("SELECT a FROM Activity a WHERE a.activityName LIKE ?1 ");
        for (int i = 2; i < partialNames.size() + 1; i++) {
            queryBuilder.append(String.format("(SELECT a FROM Activity a WHERE a.activityName LIKE ?%d )", i));
        }
        TypedQuery<Activity> query = entityManager.createQuery(queryBuilder.toString(), Activity.class);
        for (int i = 1; i < partialNames.size() + 1; i++) {
            query.setParameter(i, "%" + partialNames.get(i - 1) + "%");
        }
        if (pageable.isPaged()) {
            final long offset = pageable.getOffset();
            final int pageSize = pageable.getPageSize();
            query.setFirstResult((int) offset);
            query.setMaxResults(pageSize);
        }

        // A * O(m)

        return query.getResultList();
    }*/
}
