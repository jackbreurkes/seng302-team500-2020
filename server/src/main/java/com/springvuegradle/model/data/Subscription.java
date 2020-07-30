package com.springvuegradle.model.data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * JPA POJO class representing the relation between a user and
 * something they can subscribe to (such as Activities)
 * 
 * @author Alex Hobson
 *
 */
@Entity
@Table(name = "subscriptions")
@NamedQuery(name = "Subscription.findSubscriptionsByUser", query = "SELECT s FROM Subscription s WHERE s.subscriber = ?1")
public class Subscription {

	@Id
	@GeneratedValue
	private long id;

	private Profile subscriber;
	
	@Enumerated(EnumType.ORDINAL)
	private HomefeedEntityType entityType;
	
	private long entityId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Profile getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Profile subscriber) {
		this.subscriber = subscriber;
	}

	public HomefeedEntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(HomefeedEntityType entityType) {
		this.entityType = entityType;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}
}
