package com.springvuegradle.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request information wrapper class for PUT activity types for a profile
 */
public class PutActivityTypesRequest {

    @JsonProperty("activities")
    private List<String> activityTypes;

    /**
     * no arg constructor required by Spring
     */
    protected PutActivityTypesRequest() {};

    /**
     * @param activityTypes list of activity type names corresponding to existing activity types in the database
     */
    public PutActivityTypesRequest(List<String> activityTypes) {
        this.activityTypes = activityTypes;
    }

    /**
     * @return the list of activity type names given in the request
     */
    public List<String> getActivityTypes() {
        return activityTypes;
    }
}
