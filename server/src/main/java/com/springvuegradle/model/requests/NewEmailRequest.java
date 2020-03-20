package com.springvuegradle.model.requests;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Class for handling requests to add an email to an account
 * 
 * @author Riley Symon
 *
 */

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NewEmailRequest {
	
	private long profile_id;
	private String email;
	private int numEmails;
	private String additional_email;
	
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
	
	public String getAdditionalEmail() {
		return this.additional_email;
	}
	
	public void setAdditionalEmail(String emails) {
		this.additional_email = emails;
	}
	
}