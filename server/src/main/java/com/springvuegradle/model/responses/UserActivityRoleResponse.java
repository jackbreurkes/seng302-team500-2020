package com.springvuegradle.model.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.model.data.Profile;

/**
 * Class that responds with a single user's role in an activity
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserActivityRoleResponse {

	long profileId;
	String firstname;
	String lastname;
	String nickname;
	String role;

	/**
	 * Create a response for a user/activity role representation
	 * @param profile Profile represented by this class
	 * @param role Role of the profile in the activity
	 */
	public UserActivityRoleResponse(Profile profile, String role) {
		this.profileId = profile.getUser().getUserId();
		this.firstname = profile.getFirstName();
		this.lastname = profile.getLastName();
		this.nickname = profile.getNickName();
		this.role = role;
	}

	public long getProfileId() {
		return profileId;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getNickname() {
		return nickname;
	}

	public String getRole() {
		return role;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
