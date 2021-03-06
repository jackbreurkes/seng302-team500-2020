package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Response when a profile is created
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProfileCreatedResponse {

	/**
	 * ID of the profile created
	 */
	private final long profile_id;
	
	/**
	 * Constructor of profile created response
	 * @param profile_id ID of profile created
	 */
	public ProfileCreatedResponse(long profile_id) {
		this.profile_id = profile_id;
	}
	
	/**
	 * Get the created profile ID
	 * @return id of created profile
	 */
	public long getProfileId() {
		return this.profile_id;
	}
}
