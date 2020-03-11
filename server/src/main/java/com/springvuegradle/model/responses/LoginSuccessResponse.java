package com.springvuegradle.model.responses;

/**
 * When a user submits a valid email/password and needs a token for authentication
 * @author Alex Hobson
 *
 */
public class LoginSuccessResponse {

	/**
	 * Token to be sent to client
	 */
	private final String token;
	
	/**
	 * Create a LoginSuccessResponse with a given token
	 * @param token Token this instance represents
	 */
	public LoginSuccessResponse(String token) {
		this.token = token;
	}
	
	/**
	 * Get the token
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
}
