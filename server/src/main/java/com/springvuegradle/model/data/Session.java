package com.springvuegradle.model.data;

import java.time.OffsetDateTime;

import javax.persistence.*;

/**
 * JPA representation of sessions (token when a user is logged in)
 */
@Entity
@Table(name = "session")
@NamedQuery(name = "Session.deleteUserSession", query = "delete from Session s where s.user = ?1")
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
	@JoinColumn(name = "uuid", nullable = false)
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
	 * @param expiry
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
}
