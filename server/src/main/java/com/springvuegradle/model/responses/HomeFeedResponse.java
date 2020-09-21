package com.springvuegradle.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.model.data.*;
import org.apache.tomcat.util.json.JSONParser;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Response class to present information about an individual activity update to the client
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HomeFeedResponse {

    private long changeId;
    private ChangeLogEntity entityType;
    private long entityId;
    private String entityName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long creatorId = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String creatorName = null;

    private Long editorId = null;
    private String editorName = null;

    private String editedTimestamp;
    private ChangedAttribute changedAttribute;
    private ActionType actionType;
    private Object oldValue; // either a string or a JSON object
    private Object newValue; // either a string or a JSON object

    private String activityLocation;
    private long activityFollowers;
    private List<String> activityTypes;

    /**
     * default constructor used for serialising response JSON in tests
     */
    protected HomeFeedResponse() {}

    /**
     * Constructor to present HomeFeed Reponse for individual change to activity to the client
     * @param changeLog change log entry to present in home feed
     * @param activity activity in change log
     * @param editor editor profile that made the change, or null if the profile has since been deleted
     */
    public HomeFeedResponse(ChangeLog changeLog, Activity activity, Profile editor) {
        this(changeLog, activity.getActivityName(), editor);
        this.creatorId = activity.getCreator().getUser().getUserId();
        this.creatorName = activity.getCreator().getFullName(false);
    }

    /**
     * constructs a homefeed response given a changelog, the profile that is editing the information
     * and the name of the entity being edited.
     * @param changeLog change log entry to present in home feed
     * @param entityName name of the entity being edited
     * @param editor editor profile that made the change, or null if the profile has since been deleted
     */
    public HomeFeedResponse(ChangeLog changeLog, String entityName, Profile editor) {
        this.changeId = changeLog.getChangeId();
        this.entityType = changeLog.getEntity();
        this.entityId = changeLog.getEntityId();
        this.entityName = entityName;
        this.editedTimestamp = changeLog.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        if (editor != null) {
            this.editorId = editor.getUser().getUserId();
            this.editorName = editor.getFullName(false);
        } else {
            this.editorName = "<deleted user>";
        }
        this.changedAttribute = changeLog.getChangedAttribute();
        this.actionType = changeLog.getActionType();
        if (changeLog.getOldValue() != null) {
            this.oldValue = getJsonFromString(changeLog.getOldValue());
        }
        if (changeLog.getNewValue() != null) {
            this.newValue = getJsonFromString(changeLog.getNewValue());
        }
    }
    /**
     * Constructs a homefeed response for recommended activities
     * @param activity The activity being recommended to the user
     * @param activityFollowers The follower count of the activity being recommended
     * @param changedAttribute the attribute being changed, normally should be recommended_activity
     */
    public HomeFeedResponse(Activity activity, Long activityFollowers, ChangedAttribute changedAttribute){
        this.entityId = activity.getId();
        this.entityName = activity.getActivityName();

        this.creatorId = activity.getCreator().getUser().getUserId();
        this.creatorName = activity.getCreator().getFullName(false);

        this.changedAttribute = changedAttribute;

        this.activityLocation = activity.getLocation();
        this.activityFollowers = activityFollowers;
        this.activityTypes = activity.getActivityTypes().stream().map(object -> object.getActivityTypeName()).collect(Collectors.toList());
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
        } catch (Exception | Error e) {
            return raw;
        }
    }

    public long getChangeId() {
        return changeId;
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

    public Long getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getEditedTimestamp() {
        return editedTimestamp;
    }

    public Long getEditorId() {
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
