package com.springvuegradle.model.data;

/**
 * Enum for entity type changes stored in the change log
 */
public enum ChangeLogEntity {
	ACTIVITY("activity");
	
	/**
	 * Name of the entity type changed when sent by json
	 */
	private final String jsonName;
	
	/**
	 * Constructor for ChangeLogEntity
	 * @param jsonName Name of type of the changed entity given when sent by json
	 */
	private ChangeLogEntity(String jsonName) {
		this.jsonName = jsonName;
	}
	
	@Override
	public String toString() {
		return this.jsonName;
	}

}
