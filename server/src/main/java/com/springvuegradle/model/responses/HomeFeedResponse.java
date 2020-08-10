package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ProfileRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HomeFeedResponse {

    private ChangeLogEntity entity_type;
    private long entity_id;
    private String entity_name;
    private long creator_id;
    private String creator_name;
    private LocalDateTime edited_timestamp;
    private long editor_id;
    private String editor_name;
    private ChangedAttribute changed_attribute;
    private ActionType action_type;
    private String old_value;
    private String new_value;

    public HomeFeedResponse(ChangeLog changeLog, ActivityRepository entityRepository, ProfileRepository profileRepository) throws InvalidRequestFieldException, RecordNotFoundException {

        //{
        //        "entity_type": "activity",
        //        "entity_id": 1,
        //        "entity_name": "Boo",
        //        "creator_id": 2,
        //        "creator_name": "Alex",
        //        "edited_timestamp": "<ISO8601 timestamp>",
        //        "editor_id": 3,
        //        "editor_name": "Olivia"
        //        "change_attribute": "activity_timeframe",
        //        "action_type": "create",
        //        "old_value": null
        //        "new_value": {
        //            "start_time": "<ISO8601 timestamp>",
        //            "end_time": "<ISO8601 timestamp>"
        //        }
        //    }

        this.entity_type = changeLog.getEntity();
        this.entity_id = changeLog.getEntityId();
        if (changeLog.getEntity().equals(ChangeLogEntity.ACTIVITY)) {
            Optional<Activity> optionalEntity = entityRepository.findById(this.entity_id);
            if (optionalEntity.isEmpty()) {
                throw new RecordNotFoundException("activity in change log does not exist"); // Would cause issues if an activity could be deleted without deleting its change log entries
            }
            Activity entity = optionalEntity.get();
            this.entity_name = entity.getActivityName();
            this.creator_id = entity.getCreator().getUser().getUserId();
            this.creator_name = entity.getCreator().getFullName(false);
            this.edited_timestamp = changeLog.getTimestamp();
            this.editor_id = changeLog.getEditingUser().getUserId();
            Optional<Profile> optionalEditor = profileRepository.findById(changeLog.getEditingUser().getUserId());
            if (optionalEditor.isEmpty()) {
                throw new RecordNotFoundException("editor profile does not exist"); //Cause similar issue as described above
            }
            this.editor_name = optionalEditor.get().getFullName(false);
            this.changed_attribute = changeLog.getChangedAttribute();
            this.action_type = changeLog.getActionType();
            this.old_value = changeLog.getOldValue();
            this.new_value = changeLog.getNewValue();

        } else {
            throw new InvalidRequestFieldException("changes to this entity are not supported in the home feed");
        }

    }

    public ChangeLogEntity getEntity_type() {
        return entity_type;
    }

    public long getEntity_id() {
        return entity_id;
    }

    public String getEntity_name() {
        return entity_name;
    }

    public long getCreator_id() {
        return creator_id;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public LocalDateTime getEdited_timestamp() {
        return edited_timestamp;
    }

    public long getEditor_id() {
        return editor_id;
    }

    public String getEditor_name() {
        return editor_name;
    }

    public ChangedAttribute getChanged_attribute() {
        return changed_attribute;
    }

    public ActionType getAction_type() {
        return action_type;
    }

    public String getOld_value() {
        return old_value;
    }

    public String getNew_value() {
        return new_value;
    }
}
