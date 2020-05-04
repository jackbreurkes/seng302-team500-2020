package com.springvuegradle.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;

import java.util.List;
import java.util.stream.Collectors;

public class ActivityResponse {

    private Long activityId;
    private String activityName;
    private boolean continuous;
    private String startTime;
    private String endTime;
    private String description;
    private String location;

    @JsonProperty("creator")
    private Long creatorId;

    @JsonProperty("activity_type")
    private List<String> activityTypes;

    public ActivityResponse(Activity activity) {
        this.activityId = activity.getId();
        this.activityName = activity.getActivityName();
        this.continuous = !activity.isDuration();
        this.description = activity.getDescription();
        this.location = activity.getLocation();
        this.startTime = activity.getStartDate().toString() + "T" + activity.getStartTime().toString() + "+0000";
        this.endTime = activity.getEndDate().toString() + "T" + activity.getEndTime().toString() + "+0000";
        this.creatorId = activity.getCreator().getUser().getUserId();
        this.activityTypes = activity.getActivityTypes()
                .stream()
                .map(ActivityType::getActivityTypeName)
                .collect(Collectors.toList());
    }

    public Long getActivityId() {
        return activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public List<String> getActivityTypes() {
        return activityTypes;
    }
}
