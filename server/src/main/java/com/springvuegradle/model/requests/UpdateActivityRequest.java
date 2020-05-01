package com.springvuegradle.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Update actitivity request
 * For POST /profiles/{id}/activities/{ActivitiyId}
 * @Author Michael Freeman
 */
public class UpdateActivityRequest {

    @JsonProperty("activity_name")
    private String activityName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("activity_type")
    private List<String> activityTypes;

    @JsonProperty("continous")
    private boolean continous;

    //TODO
    @JsonProperty("start_time")
    private String startTime;

    //TODO
    @JsonProperty("end_time")
    private String endTime;

    @JsonProperty("location")
    private String location;

    public UpdateActivityRequest() {};

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<String> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public boolean isContinous() {
        return continous;
    }

    public void setContinous(boolean continous) {
        this.continous = continous;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
