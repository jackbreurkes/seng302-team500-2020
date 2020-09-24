package com.springvuegradle.model.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.model.data.GeoPosition;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * jackson JSON binding class for requests to POST /profiles/:id/activities
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateActivityRequest {

    @NotNull(message = "missing activity_name field")
    @Size(min = 4, max = 50, message = "activity_name must be between 4 and 50 characters inclusive")
    private String activityName;

    @Size(min = 8, message = "activity description must be at least 8 characters")
    private String description;

    @JsonProperty("activity_type")
    @NotNull(message = "missing activity_type field")
    @Size(min = 1, message = "must include at least one activity_type")
    private List<String> activityTypes;

    @Valid
    private List<ActivityOutcomeRequest> outcomes = new ArrayList<>();

    @NotNull(message = "missing continuous field")
    private Boolean continuous;
    private String startTime;
    private String endTime;
    private String location;


    public CreateActivityRequest() {}

    /**
     * @return the value of activity_name in the request body
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * setter used by jackson when databinding
     * @param activityName the value of activity_name
     */
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     * @return the value of description in the request body
     */
    public String getDescription() {
        return description;
    }

    /**
     * setter used by jackson when databinding
     * @param description the value of description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the value of activity_type given in the request body
     */
    public List<String> getActivityTypes() {
        return activityTypes;
    }

    /**
     * setter used by jackson when databinding
     * @param activityTypes the value of activity_type
     */
    public void setActivityTypes(List<String> activityTypes) {
        this.activityTypes = activityTypes;
    }

    /**
     * @return the value of continuous given in the request body
     */
    public Boolean isContinuous() {
        return continuous;
    }

    /**
     * setter used by jackson when databinding
     * @param continuous the value of continuous
     */
    public void setContinuous(Boolean continuous) {
        this.continuous = continuous;
    }

    /**
     * @return the value of start_time given in the request body
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * setter used by jackson when databinding
     * @param startTime the value of start_time
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the value of end_time given in the request body
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * setter used by jackson when databinding
     * @param endTime the value of end_time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the value of location given in the request body
     */
    public String getLocation() {
        return location;
    }

    /**
     * setter used by jackson when databinding
     * @param location the value of location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return json resolution for outcomes given to this activity
     */
    public List<ActivityOutcomeRequest> getOutcomes() {
        return outcomes;
    }

    /**
     * @param outcomes the outcomes associated with this activity request
     */
    public void setOutcomes(List<ActivityOutcomeRequest> outcomes) {
        this.outcomes = outcomes;
    }
}
