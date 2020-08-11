package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.model.data.*;
import org.apache.tomcat.util.json.JSONParser;

import java.time.format.DateTimeFormatter;

/**
 * Response class to present information about an individual activity update to the client
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HomeFeedResponse {

    private ChangeLogEntity entityType;
    private long entityId;
    private String entityName;
    private long creatorId;
    private String creatorName;
    private String editedTimestamp;
    private long editorId;
    private String editorName;
    private ChangedAttribute changedAttribute;
    private ActionType actionType;
    private Object oldValue;
    private Object newValue;

    /**
     * Constructor to present HomeFeed Reponse for individual change to activity to the client
     * @param changeLog change log entry to present in home feed
     * @param activity activity in change log
     * @param editor editor profile that made the change
     */
    public HomeFeedResponse(ChangeLog changeLog, Activity activity, Profile editor) {
        this.entityType = changeLog.getEntity();
        this.entityId = changeLog.getEntityId();
        this.entityName = activity.getActivityName();
        this.creatorId = activity.getCreator().getUser().getUserId();
        this.creatorName = activity.getCreator().getFullName(false);
        this.editedTimestamp = changeLog.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        this.editorId = changeLog.getEditingUser().getUserId();
        this.editorName = editor.getFullName(false);
        this.changedAttribute = changeLog.getChangedAttribute();
        this.actionType = changeLog.getActionType();
        this.oldValue = getJsonFromString(changeLog.getOldValue());
        this.newValue = getJsonFromString(changeLog.getNewValue());
    }

    /**
     * Parses a JSON string to an object
     * @param raw JSON formatted string to parse
     * @return parsed object if the string was in valid JSON format, returns the raw string if invalid
     */
    private Object getJsonFromString(String raw) {
        JSONParser parser = new JSONParser(raw);
        try {
            return parser.parse();
        } catch (Exception exception) {
            return raw;
        } catch (Error error) {
            return raw;
        }
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

    public String getEditedTimestamp() {
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

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
