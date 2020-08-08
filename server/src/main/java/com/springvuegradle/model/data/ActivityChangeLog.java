package com.springvuegradle.model.data;

import com.springvuegradle.model.requests.CreateActivityRequest;
import net.minidev.json.JSONArray;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * JPA representation of a change log entry for an activity type.
 */
@Entity
@Table(name = "changelog")
public class ActivityChangeLog extends ChangeLog {

    static final String TIME_FRAME_FORMAT = "{\"start_time\": \"%s\", \"end_time\": \"%s\"}";

    /**
     * Construct a change log entry object and automatically assign their ID
     */
    protected ActivityChangeLog() {
    }

    /**
     * Construct a change log entry object for an activity with all parameters (excluding the automatically assigned id and timestamp)
     * @param entityId the id of the entity which the change was made to
     * @param changedAttribute the attribute of the entity which was changed
     * @param editingUser the user who made the change
     * @param actionType ActionType enum representing the action done in the change
     * @param oldValue (if present) the old value of the attribute that was changed
     * @param newValue (if present) the new value that the changed attribute was set to
     */
    public ActivityChangeLog(@NotNull long entityId,
                             @NotNull ChangedAttribute changedAttribute, User editingUser, @NotNull ActionType actionType,
                             String oldValue, String newValue) {
        this.entity = ChangeLogEntity.ACTIVITY;
        this.entityId = entityId;
        this.changedAttribute = changedAttribute;
        this.editingUser = editingUser;
        this.actionType = actionType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * creates a list of ChangeLogs generated by the update operation on an activity
     * @param oldActivityInfo the activity whose information is being updated
     * @param newActivityInfo a valid request with the new activity information
     * @param editingUser the user who is making the change to the activity
     */
    public static List<ChangeLog> getLogsForUpdateActivity(Activity oldActivityInfo, CreateActivityRequest newActivityInfo, User editingUser) {
        List<ChangeLog> changes = new ArrayList<>();
        long entityId = oldActivityInfo.getId();

        if (!oldActivityInfo.getActivityName().equals(newActivityInfo.getActivityName())) {
            changes.add(new ActivityChangeLog(entityId,
                    ChangedAttribute.ACTIVITY_NAME,
                    editingUser, ActionType.UPDATED,
                    oldActivityInfo.getActivityName(), newActivityInfo.getActivityName()));
        }

        if (!Objects.equals(oldActivityInfo.getDescription(), newActivityInfo.getDescription())) {
            changes.add(new ActivityChangeLog(entityId,
                    ChangedAttribute.ACTIVITY_DESCRIPTION,
                    editingUser, ActionType.UPDATED,
                    oldActivityInfo.getDescription(), newActivityInfo.getDescription()));
        }

        if (!oldActivityInfo.getLocation().equals(newActivityInfo.getLocation())) {
            changes.add(new ActivityChangeLog(entityId,
                    ChangedAttribute.ACTIVITY_LOCATION,
                    editingUser, ActionType.UPDATED,
                    oldActivityInfo.getLocation(), newActivityInfo.getLocation()));
        }

        List<String> oldActivityTypeNames = oldActivityInfo.getActivityTypes().stream().map(ActivityType::getActivityTypeName).collect(Collectors.toList());
        boolean activityTypesSizeChanged = oldActivityTypeNames.size() != newActivityInfo.getActivityTypes().size();
        if (activityTypesSizeChanged || !oldActivityTypeNames.containsAll(newActivityInfo.getActivityTypes())) {
            String oldActivityTypesJson = JSONArray.toJSONString(oldActivityTypeNames);
            String newActivityTypesJson = JSONArray.toJSONString(newActivityInfo.getActivityTypes());
            changes.add(new ActivityChangeLog(entityId,
                    ChangedAttribute.ACTIVITY_ACTIVITY_TYPES,
                    editingUser, ActionType.UPDATED,
                    oldActivityTypesJson, newActivityTypesJson));
        }

        boolean startTimeChanged = !Objects.equals(oldActivityInfo.getStartTime(), newActivityInfo.getStartTime());
        boolean endTimeChanged = !Objects.equals(oldActivityInfo.getEndTime(), newActivityInfo.getEndTime());
        if (oldActivityInfo.isDuration() == newActivityInfo.isContinuous() || startTimeChanged || endTimeChanged) {
            changes.add(getTimeFrameChangedLog(oldActivityInfo, newActivityInfo, editingUser));
        }
        return changes;
    }

    /**
     * helper function to generate a changelog in the correct format depending on how the activity's time frame has been changed
     * @param oldActivityInfo the activity whose time frame is being changed
     * @param newActivityInfo a valid request with the new time frame information
     * @param editingUser the user who is making the change to the activity
     * @return
     */
    private static ChangeLog getTimeFrameChangedLog(Activity oldActivityInfo, CreateActivityRequest newActivityInfo, User editingUser) {
        String oldTimeFrameJson = null;
        String newTimeFrameJson = null;
        ActionType actionType = null;
        if (!oldActivityInfo.isDuration() && !newActivityInfo.isContinuous()) { // continuous -> duration
            newTimeFrameJson = String.format(TIME_FRAME_FORMAT, newActivityInfo.getStartTime(), newActivityInfo.getEndTime());
            actionType = ActionType.CREATED;
        } else if (oldActivityInfo.isDuration() && newActivityInfo.isContinuous()) { // duration -> continuous
            oldTimeFrameJson = String.format(TIME_FRAME_FORMAT, oldActivityInfo.getStartTime(), oldActivityInfo.getEndTime());
            actionType = ActionType.DELETED;
        } else { // duration -> duration, start or end time changed
            oldTimeFrameJson = String.format(TIME_FRAME_FORMAT, oldActivityInfo.getStartTime(), oldActivityInfo.getEndTime());
            newTimeFrameJson = String.format(TIME_FRAME_FORMAT, newActivityInfo.getStartTime(), newActivityInfo.getEndTime());
            actionType = ActionType.UPDATED;
        }
        return new ActivityChangeLog(oldActivityInfo.getId(),
                ChangedAttribute.ACTIVITY_TIME_FRAME,
                editingUser, actionType,
                oldTimeFrameJson, newTimeFrameJson);
    }

    /**
     * creates a ChangeLog generated by an activity's creation.
     * @param newActivityInfo an Activity object that has an id (i.e. has been saved to the database)
     */
    public static ChangeLog getLogForCreateActivity(Activity newActivityInfo) {
        if (newActivityInfo.getId() == 0L) {
            throw new IllegalArgumentException("new activity's id cannot be null");
        }
        return new ActivityChangeLog(newActivityInfo.getId(),
                ChangedAttribute.ACTIVITY_EXISTENCE,
                newActivityInfo.getCreator().getUser(),
                ActionType.CREATED, null, newActivityInfo.getActivityName());
    }


    /**
     * creates a ChangeLog generated by an activity's deletion.
     * @param oldActivityInfo the deleted activity
     * @param deletingUser the user who has deleted the activity
     */
    public static ChangeLog getLogForDeleteActivity(Activity oldActivityInfo, User deletingUser) {
        return new ActivityChangeLog(oldActivityInfo.getId(),
                ChangedAttribute.ACTIVITY_EXISTENCE,
                deletingUser,
                ActionType.DELETED, oldActivityInfo.getActivityName(), null);
    }

}
