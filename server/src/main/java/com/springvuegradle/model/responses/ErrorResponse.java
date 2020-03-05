package com.springvuegradle.model.responses;

public class ErrorResponse {

	private final String error;
	
	public ErrorResponse(String message) {
		this.error = message;
	}
	
	public String getError() {
		return this.error;
	}
}
