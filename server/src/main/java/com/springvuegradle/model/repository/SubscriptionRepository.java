package com.springvuegradle.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

	public List<Subscription> findSubscriptionsByUser(Profile profile);
}
