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
    private final GeoPosition coordinates;
    private final String role;
    private boolean isRecommended;
    
    /**
     * empty constructor used to map JSON responses to objects within tests.
     */
    protected ActivityPinResponse() {
    	activityId = 0;
    	coordinates = null;
    	role = null;
        isRecommended = false;
    }

    /**
     * constructor for a activity pin response.
     * @param pin the pin to create the response object for
     * @param role the role of the user making the request
     */
    public ActivityPinResponse(ActivityPin pin, String role) {
        activityId = pin.getActivity().getId();
        coordinates = new GeoPosition(pin.getLatitude(), pin.getLongitude());
        this.role = role;
        this.isRecommended = false;
    }

    /**
     * constructor for a activity pin response which specifies whether the activity is simply a recommended one.
     * @param pin the pin to create the response object for
     * @param role the role of the user making the request
     * @param recommended whether the activity is one of the user's recommended activities (true = is recommended, false otherwise)
     */
    public ActivityPinResponse(ActivityPin pin, String role, boolean recommended) {
        activityId = pin.getActivity().getId();
        coordinates = new GeoPosition(pin.getLatitude(), pin.getLongitude());
        this.role = role;
        this.isRecommended = recommended;
    }

    public long getActivityId() {
        return activityId;
    }

    public GeoPosition getCoordinates() {
        return coordinates;
    }

    public String getRole() {
        return role;
    }

    public boolean getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(boolean recommended) {
        this.isRecommended = recommended;
    }
}
