package com.springvuegradle.model.data;

/**
 * Enum for entity type changes stored in the change log
 *
 * ***************************************************
 * NOTE: When adding other entity types, ensure they
 * are added to the end of the enum so the index of
 * all previous existing items does not change
 * ***************************************************
 *
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
