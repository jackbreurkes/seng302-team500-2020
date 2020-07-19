package com.springvuegradle.model.data;

import java.io.Serializable;

import javax.persistence.*;

/**
 * JPA class for Email addresses in persistent storage
 * @author Alex Hobson
 * @author Riley Symon
 * @author Olivia Mackintosh
 *
 */
@Entity
@Table(name = "email")
@NamedQuery(name = "Email.findByEmail", query = "select e from Email e where e.email = ?1")
@NamedQuery(name = "Email.getNumberOfEmails", query = "select count(user) from Email e where e.user = ?1")
@NamedQuery(name = "Email.getNonPrimaryEmails", query = "select e from Email e where e.user = ?1 and isPrimary = false")
@NamedQuery(name = "Email.getPrimaryEmail", query = "select email from Email e where e.user = ?1 and isPrimary = true")
@NamedQuery(name = "Email.deleteUserEmails", query = "delete from Email e where e.user = ?1")
public class Email implements Serializable {

	/**
	 * Auto generated serial ID
	 */
	private static final long serialVersionUID = -4485265932326379395L;

	/**
	 * User this email address belongs to
	 */
	@ManyToOne
	@JoinColumn(name = "user")
	private User user;
	
	/**
	 * Email address this instance represents
	 */
	@Id
	private String email;
	
	/**
	 * Whether this email address is the user's primary email
	 */
	private boolean isPrimary;
	
	/**
	 * Constructor required by Spring
	 */
	protected Email() {}
	
	/**
	 * Constructor for Email
	 * @param user User this email belongs to
	 * @param email Email address
	 * @param is_primary Whether this email address is the user's primary email
	 */
	public Email(User user, String email, boolean is_primary) {
		this.user = user;
		this.email = email;
		this.isPrimary = is_primary;
	}

	/**
	 * Get the user that this email belongs to
	 * @return User this email belongs to
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Get the email address
	 * @return email address of this instance
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Get whether this email is the primary email address of the user
	 * @return true if this is the user's primary email address
	 */
	public boolean getIsPrimary() {
		return isPrimary;
	}

	public Email getPrimaryEmail(User u){
		if(u.equals(this.user) && this.isPrimary){
			return this;
		}else{
			return null;
		}
	}

	/**
	 * Set whether this email is the primary email address of the user
	 * @param is_primary
	 */
	public void setIsPrimary(boolean is_primary) {
		this.isPrimary = is_primary;
	}
}
