package com.springvuegradle.model.data;

import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.springvuegradle.auth.ChecksumUtils;

/**
 * JPA representation of a user (email and password, not tied to a profile)
 * @author Alex Hobson
 *
 */

/**
 * Used so that the password is never returned to the client
 */
@JsonIgnoreProperties(value = {"password"})

@Entity
@Table(name = "user")
public class User {
	
	/**
	 * uuid of the user
	 */
	@Id
	@GeneratedValue
	private long uuid;

	/**
	 * hashed password of the user
	 */

	private String password;
	
	/**
	 * Construct a user and automatically assign their ID
	 */
	public User() {}
	
	/**
	 * Construct a User object
	 * @param uuid User ID to be created
	 */
	public User(long uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * Get the unique user id
	 * @return user uuid
	 */
	public long getUserId() {
		return this.uuid;
	}
	
	/**
	 * Get the hashed password of the user
	 * @return hashed (SHA256 + salt) password of the user
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * Set the password of the user
	 * @param unhashed Unhashed password (plaintext) to set to
	 * @throws NoSuchAlgorithmException If SHA-256 doesn't exist in your version of java
	 */
	public void setPassword(String unhashed) throws NoSuchAlgorithmException {
		String hashed = ChecksumUtils.hashPassword(this.uuid, unhashed);
		this.password = hashed;
	}

}
