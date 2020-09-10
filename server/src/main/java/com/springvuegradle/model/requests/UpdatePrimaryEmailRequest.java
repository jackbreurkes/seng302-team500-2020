package com.springvuegradle.model.requests;

/**
 * Class for handling requests to change primary email
 */

public class UpdatePrimaryEmailRequest {
	
	private long profile_id;
	private String newPrimaryEmail;
	private int numEmails;
	
	protected UpdatePrimaryEmailRequest() {};
	
	public UpdatePrimaryEmailRequest(long profile_id, String new_email) {
		this.profile_id = profile_id;
		this.newPrimaryEmail = new_email;
		this.numEmails = 0;
	}
	
	public long getUser() {
		return this.profile_id;
	}

	public String getNewPrimaryEmail() {
		return this.newPrimaryEmail;
	}
	
	public int getNumEmails() {
		return this.numEmails;
	}
}