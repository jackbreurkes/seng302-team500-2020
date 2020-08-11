package com.springvuegradle.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.Subscription;

/**
 * JPA repository for subscriptions
 * @author Alex Hobson
 *
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

	/**
	 * Finds all the subscriptions relevant to a given user
	 * @param profile Profile to find subscriptions of
	 * @return List of subscriptions (may be empty)
	 */
	@Query(value = "SELECT s FROM Subscription s WHERE s.subscriber = ?1")
	public List<Subscription> findSubscriptionsByUser(Profile profile);
	
	/**
	 * Gets the number of followers of an activity
	 * @param activityId Activity ID to get followers of
	 * @return Number of users following the given activity
	 */
	@Query(value = "SELECT COUNT(s) FROM Subscription s WHERE s.entityId = ?1 AND "
			+ "entityType = com.springvuegradle.model.data.HomefeedEntityType.ACTIVITY")
	public Long getFollowerCount(long activityId);

	/**
	 * Checks if uesr is subscribed to activity
	 * @param entityId Id of the activity
	 * @param profile User to check if subscribed
	 * @return boolean true if subscibed
	 */
	@Query(value = "SELECT CASE WHEN (count(s) > 0)  THEN 'TRUE' ELSE 'FALSE' END FROM Subscription s "
			+ "WHERE entityType = com.springvuegradle.model.data.HomefeedEntityType.ACTIVITY AND "
			+ "entityId = ?1 AND subscriber = ?2")
	public boolean isSubscribedToActivity(long entityId, Profile profile);

	/**
	 * Query for getting subscription id from profile and entity id
	 */
	@Query(value = "SELECT DISTINCT s.id FROM Subscription s WHERE s.subscriber = ?2 AND s.entityId = ?1")
	public long findSubscriptionId(long entityId, Profile profile);

}
