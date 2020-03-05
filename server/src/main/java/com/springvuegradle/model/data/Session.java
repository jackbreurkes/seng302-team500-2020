package com.springvuegradle.model.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * JPA representation of sessions (token when a user is logged in)
 * @author Alex Hobson
 *
 */
@Entity
@Table(name = "session")
public class Session {
	/**
	 * User that the session belongs to
	 */
    @ManyToOne
    @JoinColumn(name = "uuid")
    private User user;
    
    /**
     * Token string for this session
     */
    @Id
    private String token;
    
    /**
     * Constructor required by Spring
     */
    protected Session() {}
    
    /**
     * Create a new session for a user with a given token
     * @param user User to create session for
     * @param token Token that each subsequent request should contain
     */
    public Session(User user, String token) {
    	this.user = user;
    	this.token = token;
    }
    
    /**
     * Create a new session for a user with a generated token
     * @param user User to create session for
     */
    public Session(User user) {
    	this.user = user;
    }
    
    /**
     * Get the user this session is for
     * @return user this instance represents a session for
     */
    public User getUser() {
    	return this.user;
    }
    
    /**
     * Gets the token of this session
     * @return token of this session
     */
    public String getToken() {
    	return this.token;
    }
}
