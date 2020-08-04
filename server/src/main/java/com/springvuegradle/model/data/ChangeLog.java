package com.springvuegradle.model.data;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

/**
 * JPA representation of a change log entry
 *
 */

@Entity
@Table(name = "changelog")
public class ChangeLog {

	@Id
	@GeneratedValue
	private long changeId;

	@NotNull
	private ChangeLogEntity entity;

	@NotNull
	private long entityId;

	@NotNull
	private ChangedAttribute changedAttribute;

	@ManyToOne
	@JoinColumn(name = "uuid")
	private User editingUser;

	@NotNull
	private ActionType actionType;

	private String oldValue;

	private String newValue;

	@CreationTimestamp
	private LocalDate timestamp;

	/**
	 * Construct a change log entry object and automatically assign their ID
	 */
	public ChangeLog() {
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

	public LocalDate getTimestamp() {
		return timestamp;
	}

}
