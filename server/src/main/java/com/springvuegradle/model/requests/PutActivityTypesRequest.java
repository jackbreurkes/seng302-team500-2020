package com.springvuegradle.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Request information wrapper class for PUT activity types for a profile
 */
public class PutActivityTypesRequest {

    @JsonProperty("activities")
    @NotNull(message = "missing activities field")
    private List<String> activityTypes;

    /**
     * no arg constructor required by Spring
     */
    public PutActivityTypesRequest() {};

    /**
     * @return the list of activity type names given in the request
     */
    public List<String> getActivityTypes() {
        return activityTypes;
    }

    /**
     * @param activityTypes list of activity type names corresponding to existing activity types in the database
     */
    public void setActivityTypes(List<String> activityTypes) {
        this.activityTypes = activityTypes;
    }
}
