package com.springvuegradle.model.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * JPA representation of a change log entry
 *
 */
@NamedQuery(name = "ChangeLog.retrieveUserHomeFeedUpdates", query = "select c from ChangeLog c join Subscription s on c.entityId = s.entityId where s.subscriber = ?1")
@Entity
@Table(name = "changelog")
public class ChangeLog {

	@Id
	@GeneratedValue
	private long changeId;

	@NotNull
	@Enumerated(EnumType.STRING)
	protected ChangeLogEntity entity;

	@NotNull
	protected long entityId;

	@NotNull
	@Enumerated(EnumType.STRING)
	protected ChangedAttribute changedAttribute;

	@ManyToOne
	@JoinColumn(name = "uuid")
	protected User editingUser;

	@NotNull
	@Enumerated(EnumType.STRING)
	protected ActionType actionType;

	protected String oldValue;

	protected String newValue;

	@CreationTimestamp
	private LocalDateTime timestamp;

	/**
	 * Construct a change log entry object and automatically assign their ID
	 */
	protected ChangeLog() {
	}

	/**
	 * Construct a change log entry object with all parameters (excluding the automatically assigned id and timestamp)
	 * @param entity ChangeLogEntity enum which gives the type of application entity which the change has been made on
	 * @param entityId the id of the entity which the change was made to
	 * @param changedAttribute the attribute of the entity which was changed
	 * @param editingUser the user who made the change
	 * @param actionType ActionType enum representing the action done in the change
	 * @param oldValue (if present) the old value of the attribute that was changed
	 * @param newValue (if present) the new value that the changed attribute was set to
	 */
	public ChangeLog(@NotNull ChangeLogEntity entity, @NotNull long entityId,
					 @NotNull ChangedAttribute changedAttribute, User editingUser, @NotNull ActionType actionType,
					 String oldValue, String newValue) {
		super();
		this.entity = entity;
		this.entityId = entityId;
		this.changedAttribute = changedAttribute;
		this.editingUser = editingUser;
		this.actionType = actionType;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public long getChangeId() {
		return changeId;
	}

	public void setChangeId(long changeId) {
		this.changeId = changeId;
	}

	public ChangeLogEntity getEntity() {
		return entity;
	}

	public void setEntity(ChangeLogEntity entity) {
		this.entity = entity;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public ChangedAttribute getChangedAttribute() {
		return changedAttribute;
	}

	public void setChangedAttribute(ChangedAttribute changedAttribute) {
		this.changedAttribute = changedAttribute;
	}

	public User getEditingUser() {
		return editingUser;
	}

	public void setEditingUser(User editingUser) {
		this.editingUser = editingUser;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

}
