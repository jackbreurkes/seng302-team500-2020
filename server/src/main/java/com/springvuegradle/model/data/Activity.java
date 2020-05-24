package com.springvuegradle.model.data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * JPA POJO representing an Activity.
 * @author Jack van Heugten Breurkes
 * @author Riley Symon
 */
@Entity
@Table(name = "activity")
public class Activity {

    @Id
    @GeneratedValue
    private long activity_id;

    // the @NotNull annotation will automatically set the column to not null
    // if hibernate.validator.apply_to_ddl = true (true by default)

    @NotNull
    @Column(columnDefinition = "varchar(30)")
    private String activityName;

    @NotNull
    @Column(columnDefinition = "boolean")
    private boolean isDuration;

    @Column(columnDefinition = "varchar(30)", name = "start_time_string")
    private String startTime;

    @Column(columnDefinition = "varchar(30)", name = "end_time_string")
    private String endTime;

    @Column(columnDefinition = "text")
    private String description;

    @NotNull
    @Column(columnDefinition = "varchar(30)")
    private String location;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="creator_uuid")
    private Profile creator;

    @NotNull
    @ManyToMany
    @JoinTable(
            name = "activity_activity_type",
            joinColumns = {@JoinColumn(name = "activity_id")},
            inverseJoinColumns = {@JoinColumn(name = "activity_type_id")}
    )
    private Set<ActivityType> activityTypes;

    @ManyToMany
    @JoinTable(
            name = "activity_hashtag",
            joinColumns = {@JoinColumn(name = "activity_id")},
            inverseJoinColumns = {@JoinColumn(name = "hashtag_id")}
    )
    private Set<Hashtag> hashtags;

    /**
     * no arg constructor required by JPA
     */
    public Activity() {
    	activityTypes = new HashSet<ActivityType>();
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
     * @return the id associated with the activity in the database
     */
    public long getId() {
        return activity_id;
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
     * Setter for activity types
     * @param activityTypes
     */
    public void setActivityTypes(Set<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    /**
     * @return the hashtags associated with this activity
     */
    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    /**
     * @param hashtags sets the set of hashtags associated with this activity
     */
    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }
}
