package com.springvuegradle.model.data;
/**
 * Role Enum
 * For implementing user's roles in relation to activities
 */
public enum ActivityRole {
    ORGANISER("organiser"), PARTICIPANT("participant");

    /**
     * Json name for the Role
     */
    private final String jsonName;

    public String toString() {
        return jsonName.toUpperCase();
    }

    /**
     * Constructor for Role
     * @param jsonName the name for the role given in the json request
     */
    ActivityRole(String jsonName){
        this.jsonName = jsonName;
    }
}