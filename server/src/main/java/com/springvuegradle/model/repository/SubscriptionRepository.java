package com.springvuegradle.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.Subscription;

/**
 * JPA repository for subscriptions
 * @author Alex Hobson
 *
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

	/**
	 * Finds all the subscriptions relevant to a given user including
	 * instances where they have unsubscribed
	 * @param profile Profile to find subscriptions of
	 * @return List of subscriptions (may be empty)
	 */
	public List<Subscription> findSubscriptionsByUser(Profile profile);
}
