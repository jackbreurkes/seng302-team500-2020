package com.springvuegradle.model.data;

/**
 * Enum for action types in the change log
 */
public enum ActionType {
	CREATED("created"), UPDATED("updated"), DELETED("deleted");
	
	/**
	 * Name the action type is when sent by json
	 */
	private final String jsonName;
	
	/**
	 * Constructor for ActionType
	 * @param jsonName Name of the action type that is given when sent by json
	 */
	private ActionType(String jsonName) {
		this.jsonName = jsonName;
	}
	
	@Override
	public String toString() {
		return this.jsonName;
	}

}
