package com.springvuegradle.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Activity;
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
	public List<Subscription> findSubscriptionsByUser(Profile profile);
	
	/**
	 * Gets the number of followers of an activity
	 * @param activity Activity to get followers of
	 * @return Number of users following the given activity
	 */
	public Long getFollowerCount(Activity activity);
}
