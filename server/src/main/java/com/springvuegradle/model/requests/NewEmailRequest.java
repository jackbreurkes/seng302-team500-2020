package com.springvuegradle.model.requests;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	private int numEmails;
	private List<String> additional_emails;
	
	protected NewEmailRequest() {};
	
	public NewEmailRequest(long profile_id, List<String> emails, int numEmails) {
		System.out.println("Gdddddddddddddddddddddddddddddddddd");
		System.out.println(profile_id);
		System.out.println(emails);
		System.out.println("HEGKGEL");
		this.profile_id = profile_id;
		this.numEmails = 0;
		this.additional_emails = emails;
	}
	
	public long getUser() {
		return this.profile_id;
	}
	
	public List<String> getAdditionalEmails() {
		return this.additional_emails;
	}
	
	public int getNumEmails() {
		return this.numEmails;
	}
	
	public void setUser(long id) {
		this.profile_id = id;
	}
	
	public void setAdditionalEmails(List<String> newEmails) {
		this.additional_emails = newEmails;
	}
	
	public void setNumEmails(int num) {
		this.numEmails = num;
	}
}