package com.springvuegradle.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Request information wrapper class for PUT role for a user profile
 */
public class UpdateRoleRequest {

    @JsonProperty("role")
    @NotNull(message = "missing role field")
    private String role;

    /**
     * no arg constructor required by Spring
     */
    public UpdateRoleRequest() {};

    /**
     * @return the role name given in the request
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role name string corresponding to a valid role in the system
     */
    public void setRole(String role) {
        this.role = role;
    }
}
