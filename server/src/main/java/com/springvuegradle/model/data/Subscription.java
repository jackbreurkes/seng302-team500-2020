package com.springvuegradle.model.data;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@NamedQuery(name = "Subscription.getFollowerCount", query = "SELECT COUNT(s) FROM Subscription s WHERE s.entityId = ?1 AND "
		+ "entityType = com.springvuegradle.model.data.HomefeedEntityType.ACTIVITY")
@NamedQuery(name = "Subscription.isSubscribedToActivity", query = "SELECT CASE WHEN (count(s) > 0)  THEN 'TRUE' ELSE 'FALSE' END FROM Subscription s WHERE entityType = com.springvuegradle.model.data.HomefeedEntityType.ACTIVITY AND entityId = ?1 AND subscriber = ?2")
public class Subscription {

	/**
	 * auto generated unique ID
	 */
	@Id
	@GeneratedValue
	private long id;

	/**
	 * The profile this subscription is relevant to
	 */
	@ManyToOne
	@JoinColumn(name="subscriber", nullable=false)
	private Profile subscriber;
	
	/**
	 * The type of entity that entityId represents
	 */
	@Enumerated(EnumType.ORDINAL)
	private HomefeedEntityType entityType;
	
	/**
	 * The unique ID of the subscribed entity 
	 */
	private long entityId;
	
	@Column(nullable = false)
	private LocalDateTime subscribedDate;

	/**
	 * Default JPA constructor
	 */
	public Subscription() {
		subscribedDate = LocalDateTime.now();
	}
	
	/**
	 * Create an object representing when a user subscribed to something
	 * This constructor will set the subscribed date to the current system time,
	 * you can call {@link Subscription#setSubscribedDate(LocalDateTime)} to
	 * override this time
	 * 
	 * @param subscriber Who this subscription represents
	 * @param entityType The type of entity this subscription represents
	 * @param entityId The unique ID of the entity this subscription represents
	 */
	public Subscription(Profile subscriber, HomefeedEntityType entityType, long entityId) {
		this.subscriber = subscriber;
		this.entityType = entityType;
		this.entityId = entityId;
		subscribedDate = LocalDateTime.now();
	}

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
	
	public LocalDateTime getSubscribedDate() {
		return subscribedDate;
	}

	public void setSubscribedDate(LocalDateTime subscribedDate) {
		this.subscribedDate = subscribedDate;
	}
}
