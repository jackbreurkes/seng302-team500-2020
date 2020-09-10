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
import javax.persistence.Table;

/**
 * JPA POJO class representing the relation between a user and
 * something they can subscribe to (such as Activities)
 */
@Entity
@Table(name = "subscriptions")
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
	private ChangeLogEntity entityType;
	
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
	public Subscription(Profile subscriber, ChangeLogEntity entityType, long entityId) {
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

	public ChangeLogEntity getEntityType() {
		return entityType;
	}

	public void setEntityType(ChangeLogEntity entityType) {
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
