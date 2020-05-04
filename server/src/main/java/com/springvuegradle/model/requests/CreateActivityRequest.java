package com.springvuegradle.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateActivityRequest {

    @NotNull(message = "missing activity_name field")
    @Size(min = 4, max = 30, message = "activity_name must be between 4 and 30 characters inclusive")
    private String activityName;

    @Size(min = 8, message = "activity description must be at least 8 characters")
    private String description;

    @JsonProperty("activity_type")
    @NotNull(message = "missing activity_type field")
    @Size(min = 1, message = "must include at least one activity_type")
    private List<String> activityTypes;

    @NotNull(message = "missing continuous field")
    private Boolean continuous;

    private String startTime;
    private String endTime;
    private String location;

    public CreateActivityRequest() {}

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

    public Boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(Boolean continuous) {
        this.continuous = continuous;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
