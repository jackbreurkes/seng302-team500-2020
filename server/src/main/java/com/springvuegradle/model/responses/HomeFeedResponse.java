package com.springvuegradle.model.responses;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ProfileRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFeedResponse {

    private ChangeLogEntity entity_type;
    private long entity_id;
    private String entity_name;
    private long creator_id;
    private String creator_name;
    private LocalDate edited_timestamp;
    private long editor_id;
    private String editor_name;
    private ChangedAttribute changed_attribute;
    private ActionType action_type;
    private String old_value;
    private String new_value;

    public HomeFeedResponse(ChangeLog changeLog, ActivityRepository entityRepository, ProfileRepository profileRepository) throws InvalidRequestFieldException {

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
            Activity entity = entityRepository.getOne(this.entity_id);
            this.entity_name = entity.getActivityName();
            this.creator_id = entity.getCreator().getUser().getUserId();
            this.creator_name = entity.getCreator().getFullName(false);
            this.edited_timestamp = changeLog.getTimestamp();
            this.editor_id = changeLog.getEditingUser().getUserId();
            this.editor_name = profileRepository.getOne(changeLog.getEditingUser().getUserId()).getFullName(false);
            this.changed_attribute = changeLog.getChangedAttribute();
            this.action_type = changeLog.getActionType();
            this.old_value = changeLog.getOldValue();
            this.new_value = changeLog.getNewValue();

        } else {
            throw new InvalidRequestFieldException("changes to this entity are not supported in the home feed");
        }

    }

}
