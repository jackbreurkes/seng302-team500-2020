package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ProfileRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HomeFeedResponse {

    private ChangeLogEntity entityType;
    private long entityId;
    private String entityName;
    private long creatorId;
    private String creatorName;
    private LocalDateTime editedTimestamp;
    private long editorId;
    private String editorName;
    private ChangedAttribute changedAttribute;
    private ActionType actionType;
    private String oldValue;
    private String newValue;

    public HomeFeedResponse(ChangeLog changeLog, Activity activity, Profile editor) {
        this.entityType = changeLog.getEntity();
        this.entityId = changeLog.getEntityId();
        this.entityName = activity.getActivityName();
        this.creatorId = activity.getCreator().getUser().getUserId();
        this.creatorName = activity.getCreator().getFullName(false);
        this.editedTimestamp = changeLog.getTimestamp();
        this.editorId = changeLog.getEditingUser().getUserId();
        this.editorName = editor.getFullName(false);
        this.changedAttribute = changeLog.getChangedAttribute();
        this.actionType = changeLog.getActionType();
        this.oldValue = changeLog.getOldValue();
        this.newValue = changeLog.getNewValue();
    }

    public ChangeLogEntity getEntityType() {
        return entityType;
    }

    public long getEntityId() {
        return entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public LocalDateTime getEditedTimestamp() {
        return editedTimestamp;
    }

    public long getEditorId() {
        return editorId;
    }

    public String getEditorName() {
        return editorName;
    }

    public ChangedAttribute getChangedAttribute() {
        return changedAttribute;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }
}
