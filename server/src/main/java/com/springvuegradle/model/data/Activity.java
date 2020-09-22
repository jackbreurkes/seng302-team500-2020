package com.springvuegradle.model.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * JPA POJO representing an Activity.
 */
@Entity
@Table(name = "activity")
public class Activity {

    /**
     * Activity id in the database
     */
    @Id
    @GeneratedValue
    private long activity_id;

    // the @NotNull annotation will automatically set the column to not null
    // if hibernate.validator.apply_to_ddl = true (true by default)

    /**
     * Name of the activity
     */
    @NotNull
    @Column(columnDefinition = "varchar(30)")
    private String activityName;

    /**
     * Boolean representing whether the activity is continuous - true if has a duration, false if it is continuous
     */
    @NotNull
    @Column(columnDefinition = "boolean")
    private boolean isDuration;

    /**
     * Activity's start datetime
     */
    @Column(columnDefinition = "varchar(30)", name = "start_time_string")
    private String startTime;

    /**
     * Activity's end datetime
     */
    @Column(columnDefinition = "varchar(30)", name = "end_time_string")
    private String endTime;

    /**
     * Activity's description
     */
    @Column(columnDefinition = "text")
    private String description;

    /**
     * The street address for the activity location
     */
    @NotNull
    @Column(columnDefinition = "varchar(255)")
    private String location;

    /**
     * Pin specifying the location of this activity
     */
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "activity")
    private ActivityPin activityPin;

    /**
     * Id of the profile belonging to the user who created the activity
     */
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="creator_uuid")
    private Profile creator;

    /**
     * Set of activity types for this activity
     */
    @NotNull
    @ManyToMany
    @JoinTable(
            name = "activity_activity_type",
            joinColumns = {@JoinColumn(name = "activity_id")},
            inverseJoinColumns = {@JoinColumn(name = "activity_type_id")}
    )
    private Set<ActivityType> activityTypes = new HashSet<>();

    /**
     * List of possible outcomes that can be recorded against for this activity
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "activity")
    private List<ActivityOutcome> outcomes = new ArrayList<>();

    /**
     * List of user roles related to this activity
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "activity")
    private List<UserActivityRole> relatedRoles = new ArrayList<>(); // needed for cascading

    /**
     * no arg constructor required by JPA
     */
    public Activity() {
    	activityTypes = new HashSet<>();
        activityName = "";
        isDuration = false;
        location = "";
    }

    /**
     * Create an activity with required and optional fields
     * @param activityName the name of the activity
     * @param isDuration true if the activity has a start and end date, false if it is continuous
     * @param location the location of the activity
     * @param creator the profile who created the activity
     */
    public Activity(String activityName, boolean isDuration, String location, Profile creator, Set<ActivityType> activityTypes)
    {
        this.activityName = activityName;
        this.isDuration = isDuration;
        this.location = location;
        this.creator = creator;
        this.activityTypes = activityTypes;
    }

    /**
     * Setter for activity types
     * @param activityTypes
     */
    public void setActivityTypes(Set<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    /**
     * @return the id associated with the activity in the database
     */
    public long getId() {
        return activity_id;
    }	
        	
    /**	
     * Sets the ID of this activity	
     * @param id ID to set to	
     */	
    public void setId(long id) {	
        this.activity_id = id;
    }

    /**
     * @return the name of the activity
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * @param activityName the name to give to the activity
     */
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     * @return true if the activity should have a non-null start and end date,
     * false if the activity is continuous
     */
    public boolean isDuration() {
        return isDuration;
    }

    /**
     * @param isDuration true of the activity should have a non-null start and end date,
     * false if the activity is continuous
     */
    public void setIsDuration(boolean isDuration) {
        this.isDuration = isDuration;
    }

    /**
     * @return the activity start time in API format
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the activity start time in API format
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the activity end time in API format
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the activity start time in API format
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return a text description of the activity
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the text description of the activity
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the location at which the activity will commence
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location at which the activity will commence
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the pin for this activity
     */
    public ActivityPin getActivityPin() {
        return activityPin;
    }

    /**
     * @param activityPin pin to associate with this activity
     */
    public void setActivityPin(ActivityPin activityPin) {
        this.activityPin = activityPin;
    }

    /**
     * @return the profile of the user who created the activity
     */
    public Profile getCreator() {
        return creator;
    }

    /**
     * @return the list of activity types associated with the activity
     */
    public Set<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    /**
     * @return the outcomes associated with this activity
     */
    public List<ActivityOutcome> getOutcomes() {
        return outcomes;
    }

    /**
     * @param outcomes the list of ActivityOutcomes to associate with this activity
     */
    public void setOutcomes(List<ActivityOutcome> outcomes) {
        this.outcomes = outcomes;
    }

    /**
     * helper method to associate an outcome with this activity and add it to the list of outcomes.
     * @param outcome the outcome to associate with this activity
     */
    public void addOutcome(ActivityOutcome outcome) {
        outcome.setActivity(this);
        outcomes.add(outcome);
    }
}