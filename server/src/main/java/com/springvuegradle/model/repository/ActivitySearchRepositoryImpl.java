package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityPin;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.UserActivityRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * custom implementation for the activity search repository interface.
 * will be used when autowiring an ActivityRepository.
 */
@Repository
public class ActivitySearchRepositoryImpl implements ActivitySearchRepository {

    /**
     * Float value for maximum distance that recommended activities
     * will be shown from users location, in degrees
     */
    private static final float RECOMMENDATIONS_BOUNDING_BOX_SIZE = 0.2f;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    @Autowired
    UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    ActivityPinRepository activityPinRepository;

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

        final String searchForNameNestedQuery = " (SELECT a%1$d.activity_id, a%1$d.activity_name FROM activity a%1$d WHERE LOWER(a%1$d.activity_name) LIKE LOWER( ?%1$d ) ) ";
        StringBuilder queryBuilder = new StringBuilder("SELECT a.* FROM (SELECT activity_id, count(activity_id) AS matches FROM ( ");
        queryBuilder.append(String.format(searchForNameNestedQuery, 1));
        for (int i = 2; i < searchTerms.size() + 1; i++) {
            queryBuilder.append(" UNION ALL ");
            queryBuilder.append(String.format(searchForNameNestedQuery, i));
        }

        queryBuilder.append(" ) all_results GROUP BY activity_id) grouped_results NATURAL JOIN activity a GROUP BY a.activity_id ORDER BY matches DESC, a.activity_name ASC");
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

    /**
     * Given a profile, finds the activities that are recommended based on the profile's interests and their location.
     * @param profile the profile to recommend activities for
     * @return the list of activities to recommend
     */
    public List<Activity> findRecommendedActivitiesByProfile(Profile profile) {
        if (profile.getLocation() == null) {
            return new ArrayList<>();
        }

        //Get the activities within the range of the users profile location
        List<ActivityPin> activityPinsInBox = activityPinRepository.findPinsInBounds(
                profile.getLocation().getLatitude() + RECOMMENDATIONS_BOUNDING_BOX_SIZE,
                profile.getLocation().getLongitude() + RECOMMENDATIONS_BOUNDING_BOX_SIZE,
                profile.getLocation().getLatitude() - RECOMMENDATIONS_BOUNDING_BOX_SIZE,
                profile.getLocation().getLongitude() - RECOMMENDATIONS_BOUNDING_BOX_SIZE, Pageable.unpaged());
        List<Activity> activityList = activityPinsInBox.stream().map(ActivityPin::getActivity).collect(Collectors.toList());
        List<Activity> candidateActivities = new ArrayList<>();
        for(Activity activity : activityList){
            UserActivityRole role = userActivityRoleRepository.getRoleEntryByUserId(profile.getUser().getUserId(), activity.getId()).orElse(null);
            if(role == null
                    && profile.getActivityTypes().stream().anyMatch(activity.getActivityTypes()::contains)
                    && !subscriptionRepository.isSubscribedToActivity(activity.getId(), profile)
                    && activity.getCreator() != profile
                    && (!activity.isDuration() || LocalDateTime.parse(activity.getStartTime(), DATE_FORMATTER).isAfter(LocalDateTime.now()))
            ){
                candidateActivities.add(activity);
            }
        }
        return candidateActivities;
    }
}
