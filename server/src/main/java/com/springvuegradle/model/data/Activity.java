package com.springvuegradle.model.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    @Column(columnDefinition = "date")
    private LocalDate startDate;

    @Column(columnDefinition = "date")
    private LocalDate endDate;

    @Column(columnDefinition = "time")
    private LocalTime startTime;

    @Column(columnDefinition = "time")
    private LocalTime endTime;

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
    private List<ActivityType> activityTypes;

    /**
     * no arg constructor required by JPA
     */
    public Activity() {}

    /**
     * Create an activity with required and optional fields
     * @param activityName the name of the activity
     * @param isDuration true if the activity has a start and end date, false if it is continuous
     * @param location the location of the activity
     * @param creator the profile who created the activity
     */
    public Activity(String activityName, boolean isDuration, String location, Profile creator, List<ActivityType> activityTypes)
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
    public void setActivityTypes(List<ActivityType> activityTypes) {
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
     * @return the start date of the activity or null if the activity is not a duration activity
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the start date of the activity required for duration activities
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return gets the end date of the activity or null if the activity is not a duration activity
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the end date of the activity required for duration activities
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the start time of the activity or null if the activity does not have a start time set
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the start time which is optionally used by duration activities
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the end time of the activity or null if the activity does not have an end time set
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the end time which is optionally used by duration activities
     */
    public void setEndTime(LocalTime endTime) {
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
    public List<ActivityType> getActivityTypes() {
        return activityTypes;
    }
}
