package com.springvuegradle.model.requests;

/**
 * Login request spring should create when user tries to log in
 * @author Alex Hobson
 *
 */
public class LoginRequest {

	/**
	 * Provided email address
	 */
	private String email;
	
	/**
	 * Provided password (plaintext)
	 */
	private String password;

	/**
	 * Constructor required by spring
	 */
	protected LoginRequest() {}

	/**
	 * Create a login request instance
	 * @param email Email address provided by the user
	 * @param password Password (plaintext) provided by the user
	 */
	public LoginRequest(String email, String password) {
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
