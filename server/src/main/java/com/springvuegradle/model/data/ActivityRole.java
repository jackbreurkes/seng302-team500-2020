package com.springvuegradle.model.data;
/**
 * Role Enum
 * For implementing user's roles in relation to activities
 */
public enum ActivityRole {
    ORGANISER("organiser", "Organiser"), PARTICIPANT("participant", "Participant");

    /**
     * Json name for the Role
     */
    private final String jsonName;
    
    /**
     * Friendly name for the role
     */
    private final String friendlyName;

    public String toString() {
        return jsonName.toUpperCase();
    }
    
    /**
     * Get the friendly name of this activity role (as to be displayed on the client)
     * @return the user friendly name of the activity role
     */
    public String getFriendlyName() {
    	return this.friendlyName;
    }

    /**
     * Constructor for Role
     * @param jsonName the name for the role given in the json request
     * @param friendlyName the friendly name for this role
     */
    ActivityRole(String jsonName, String friendlyName){
        this.jsonName = jsonName;
        this.friendlyName = friendlyName;
    }
}