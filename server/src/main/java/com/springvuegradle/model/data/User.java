package com.springvuegradle.model.data;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@NamedQuery(name = "User.findById", query = "select u from User u where u.uuid = ?1")
@NamedQuery(name = "User.getSuperAdmin", query = "select u from User u where u.permissionLevel = 127")
@NamedQuery(name = "User.superAdminExists", query = "select count(*) from User u where u.permissionLevel = 127")
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
	 * Permission level of the user
	 */
	@Column(columnDefinition = "tinyint default 0")
	private int permissionLevel;

	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			mappedBy = "user")
	private List<Session> sessions = new ArrayList<>();

	@OneToMany(
			cascade = CascadeType.ALL,
			mappedBy = "user")
	private List<Email> emails = new ArrayList<>();

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
	
	/**
	 * Gets the permission level of the user
	 * @return the permission level of the user
	 */
	public int getPermissionLevel() {
		return permissionLevel;
	}

	/**
	 * Changes the permission level of the user to the one specified
	 * @param permissionLevel the permission level of the user
	 */
	public void setPermissionLevel(int permissionLevel) {
		this.permissionLevel = permissionLevel;
	}

	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> session) {
		this.sessions = session;
	}

	public List<Email> getEmails() {
		return emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}
}
