package com.springvuegradle.model.data;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * JPA class for ActivityParticipantResults This is the entity for when a
 * participant of an activity records their result.
 */
@Entity
public class ActivityParticipantResult {

	@GeneratedValue
	@Id
	private long participantResultId;

	@ManyToOne
	@JoinColumn(name = "user", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "outcome", nullable = false)
	private ActivityOutcome outcome;

	@Column(columnDefinition = "varchar(30) not null")
	private String value;

	private OffsetDateTime completedDate;

	/**
	 * default no-arg constructor required by hibernate
	 */
	protected ActivityParticipantResult() {

	}

	/**
	 * Creates a JPA ActivityParticipantResult object representing a user's results
	 * towards an ActivityOutcome in an Activity
	 * 
	 * @param user          User recording the score
	 * @param outcome       Which ActivityOutcome the user is recording against
	 * @param value         The score to be recorded
	 * @param completedDate The user specified time on when they completed the
	 *                      activity
	 */
	public ActivityParticipantResult(User user, ActivityOutcome outcome, String value, OffsetDateTime completedDate) {
		this.user = user;
		this.outcome = outcome;
		this.value = value;
		this.completedDate = completedDate;
	}

	public long getParticipantResultId() {
		return participantResultId;
	}

	public void setParticipantResultId(long participantResultId) {
		this.participantResultId = participantResultId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ActivityOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(ActivityOutcome outcome) {
		this.outcome = outcome;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public OffsetDateTime getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(OffsetDateTime completedDate) {
		this.completedDate = completedDate;
	}
}
