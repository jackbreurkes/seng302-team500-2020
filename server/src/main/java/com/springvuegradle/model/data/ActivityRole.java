package com.springvuegradle.model.data;
/**
 * Role Enum
 * For implementing user's roles in relation to activities
 */
public enum ActivityRole {
    CREATOR("creator"), ORGANISER("organiser"), PARTICIPANT("participant"), FOLLOWER("follower");

    /**
     * Json name for the Role
     */
    private final String jsonName;

    /**
     * Constructor for Role
     * @param jsonName the name for the role given in the json request
     */
    private ActivityRole(String jsonName){
        this.jsonName = jsonName;
    }
}