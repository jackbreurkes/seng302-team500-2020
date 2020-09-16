package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.model.data.ActivityPin;
import com.springvuegradle.model.data.GeoPosition;

/**
 * JSON response wrapper class for Activity Pins.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActivityPinResponse {

    private final long activityId;
    private final GeoPosition location;
    private final String role;
    
    /**
     * empty constructor used to map JSON responses to objects within tests.
     */
    protected ActivityPinResponse() {
    	activityId = 0;
    	location = null;
    	role = null;
    }

    /**
     * constructor for a activity pin response.
     * @param pin the pin to create the response object for
     * @param role the role of the user making the request
     */
    public ActivityPinResponse(ActivityPin pin, String role) {
        activityId = pin.getActivity().getId();
        location = new GeoPosition(pin.getLatitude(), pin.getLongitude());
        this.role = role;
    }

    public long getActivityId() {
        return activityId;
    }

    public GeoPosition getLocation() {
        return location;
    }

    public String getRole() {
        return role;
    }
}
