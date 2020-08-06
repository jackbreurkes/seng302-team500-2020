package com.springvuegradle.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.model.data.ActivityRole;

import javax.validation.constraints.NotNull;

/**
 * Request information wrapper class for PUT user role for an activity
 */
public class UpdateUserActivityRoleRequest {

    @JsonProperty("role")
    @NotNull(message = "Missing role field")
    private ActivityRole role;

    /**
     * No argument constructor required by Spring
     */
    public UpdateUserActivityRoleRequest(){};

    /**
     * Returns the user role
     * @return the role e.g. Participant or Organiser
     */
    public ActivityRole getRole() { return role; }

    /**
     * Set the user role for an activity
     * @param role The user role e.g. Participant or Organiser
     */
    public void setRole(ActivityRole role) {
        this.role = role;
    }
}
