package com.springvuegradle.model.requests;

/**
 * Class for handling requests to add an email to an account
 * 
 * @author Riley Symon
 *
 */

public class NewEmailRequest {
	
	private long profile_id;
	private String email;
	private int numEmails;
	
	protected NewEmailRequest() {};
	
	public NewEmailRequest(long profile_id, String email) {
		this.profile_id = profile_id;
		this.email = email;
		this.numEmails = 0;
	}
	
	public long getUser() {
		return this.profile_id;
	}
	public String getEmail() {
		return this.email;
	}
	
	public int getNumEmails() {
		return this.numEmails;
	}
}