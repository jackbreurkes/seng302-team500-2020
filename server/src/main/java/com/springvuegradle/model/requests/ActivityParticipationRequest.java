package com.springvuegradle.model.requests;

import javax.validation.constraints.NotNull;

/**
 *
 * Activity Participation Request class
 * Formed when trying to participate in an activity.
 *
 */
public class ActivityParticipationRequest {

    /**
     * Provided email address
     */
    @NotNull(message = "missing email field")
    private String email;

    /**
     * Provided password (plaintext)
     */
    @NotNull(message = "missing password field")
    private String password;

    /**
     * Constructor required by spring
     */
    protected ActivityParticipationRequest() {}

    /**
     * Create a login request instance
     * @param email Email address provided by the user
     * @param password Password (plaintext) provided by the user
     */
    public ActivityParticipationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Get the email address of this request
     * @return email address of this request
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Get the plaintext password of this request
     * @return plaintext password of this request
     */
    public String getPassword() {
        return this.password;
    }
}
