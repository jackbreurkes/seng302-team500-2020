package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserResponse {

	/**
	 * Profile ID of this response
	 */
	private final long profileId;
	
	/**
	 * Construct a user response from specified profile id
	 * @param profileId Profile ID to send to client
	 */
	public UserResponse(long profileId) {
		this.profileId = profileId;
	}

	/***
	 * Gets the profile id of this object
	 * @return profile id
	 */
	public long getProfileId() {
		return profileId;
	}
}
