package com.springvuegradle.model.data;

/**
 * Gender enum
 * @author Alex Hobson
 *
 */
public enum Gender {
	MALE("male"), FEMALE("female"), NON_BINARY("nonbinary");
	
	/**
	 * Name the gender is when sent by json
	 */
	private final String jsonName;
	
	/**
	 * Constructor for Gender
	 * @param jsonName Name the gender is when sent by json
	 */
	private Gender(String jsonName) {
		this.jsonName = jsonName;
	}
	
	/**
	 * Get the name of the gender to be sent to/from the client
	 * @return gender as it should be sent to/from the client
	 */
	public String getJsonName() {
		return this.jsonName;
	}
}
