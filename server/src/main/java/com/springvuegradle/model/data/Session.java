package com.springvuegradle.model.data;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * JPA representation of sessions (token when a user is logged in)
 * 
 * @author Alex Hobson
 *
 */
@Entity
@Table(name = "session")
public class Session {
	/**
	 * Token string for this session
	 */
	@Id
	private String token;

	/**
	 * User that the session belongs to
	 */
	@ManyToOne
	@JoinColumn(name = "uuid")
	private User user;

	/**
	 * When the session should expire
	 */
	private OffsetDateTime expiry;

	/**
	 * Constructor required by Spring
	 */
	protected Session() {
	}

	/**
	 * Create a new session for a user with a given token
	 * 
	 * @param user
	 *            User to create session for
	 * @param token
	 *            Token that each subsequent request should contain
	 * @param instant
	 *            When this session should expire
	 */
	public Session(User user, String token, OffsetDateTime expiry) {
		this.user = user;
		this.token = token;
		this.expiry = expiry;
	}

	/**
	 * Create a new session for a user with a generated token
	 * 
	 * @param user
	 *            User to create session for
	 */
	public Session(User user) {
		this.user = user;
	}

	/**
	 * Get the user this session is for
	 * 
	 * @return user this instance represents a session for
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * Gets the token of this session
	 * 
	 * @return token of this session
	 */
	public String getToken() {
		return this.token;
	}

	/**
	 * Gets when this session expires
	 * 
	 * @return when the session expires
	 */
	public OffsetDateTime getExpiry() {
		return this.expiry;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setExpiry(OffsetDateTime expiry) {
		this.expiry = expiry;
	}

	/**
	 * Gets whether this session is expired at this point in time
	 * 
	 * @return true if the session has expired and should not be valid
	 */
	/*public boolean isSessionExpired() {
		return (getExpiry().isBefore(Instant.now().atOffset(ZoneOffset.UTC)));
	}*/
}
