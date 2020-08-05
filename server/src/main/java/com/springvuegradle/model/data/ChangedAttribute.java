package com.springvuegradle.model.data;

/**
 * Enum for changed attribute types used in the change log
 */
public enum ChangedAttribute {
	ACTIVITY_TIME_FRAME("activity_time_frame"),
	ACTIVITY_NAME("activity_name"),
	ACTIVITY_DESCRIPTION("activity_description"),
	ACTIVITY_LOCATION("activity_location"),
	ACTIVITY_EXISTENCE("activity_existence");
	
	/**
	 * Name of the attribute changed when sent by json
	 */
	private final String jsonName;
	
	/**
	 * Constructor for ChangedAttribute
	 * @param jsonName Name of attribute changed that is given when sent by json
	 */
	private ChangedAttribute(String jsonName) {
		this.jsonName = jsonName;
	}
	
	@Override
	public String toString() {
		return this.jsonName;
	}

}
