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

    /**
     * constructor for a activity pin response.
     * @param pin the pin to create the response object for
     * @param role the role of the user making the request
     */
    public ActivityPinResponse(ActivityPin pin, String role) {
        activityId = pin.getActivity().getId();
        coordinates = new GeoPosition(pin.getLatitude(), pin.getLongitude());
        this.role = role;
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
}
