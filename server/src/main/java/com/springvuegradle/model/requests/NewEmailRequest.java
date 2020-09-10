package com.springvuegradle.model.requests;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Class for handling requests to add an email to an account
 */

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NewEmailRequest {
	
	private long profile_id;
	private String email;
	private int numEmails;
	
	@JsonProperty("additional_email")
	private List<String> additional_email;
	
	protected NewEmailRequest() {};
	
	public NewEmailRequest(long profile_id, List<String> additional_email, String email) {
		this.profile_id = profile_id;
		this.email = email;
		this.numEmails = 0;
		this.additional_email = additional_email;
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
	
	public List<String> getAdditional_Email() {
		return this.additional_email;
	}
	
	public void setAdditional_Email(List<String> additional_email) {
		this.additional_email = additional_email;
	}
	
}