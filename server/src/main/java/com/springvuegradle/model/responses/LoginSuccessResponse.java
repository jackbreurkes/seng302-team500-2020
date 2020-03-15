package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * When a user submits a valid email/password and needs a token for authentication
 * @author Alex Hobson
 *
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoginSuccessResponse {

	/**
	 * Token to be sent to client
	 */
	private final String token;

	/**
	 * profile id to be sent to client
	 */
	private final Long profileId;
	
	/**
	 * Create a LoginSuccessResponse with a given token
	 * @param token Token this instance represents
	 */
	public LoginSuccessResponse(String token, Long profileId) {
		this.token = token;
		this.profileId = profileId;
	}

	/**
	 * Get the token
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Get the profile id
	 * @return the profile id
	 */
	public Long getProfileId() {
		return profileId;
	}
}
