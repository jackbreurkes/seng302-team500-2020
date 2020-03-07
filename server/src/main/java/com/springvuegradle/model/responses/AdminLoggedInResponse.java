package com.springvuegradle.model.responses;

public class AdminLoggedInResponse {

	private final boolean admin;
	
	public AdminLoggedInResponse() {
		this.admin = true;
	}
	
	public boolean isAdmin() {
		return admin;
	}
}
