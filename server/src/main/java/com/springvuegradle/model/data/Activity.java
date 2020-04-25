package com.springvuegradle.model.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

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
    private long id;

    @NotNull  // will automatically set column to not null if hibernate.validator.apply_to_ddl = true
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

    //TODO activity types once they have been added

    /**
     * no arg constructor required by JPA
     */
    protected Activity() {}

    /**
     * Create an activity with required and optional fields
     * @param activityName
     * @param isDuration
     * @param location
     * @param creator
     */
    public Activity(String activityName, boolean isDuration, String location, Profile creator)
    {
        this.activityName = activityName;
        this.isDuration = isDuration;
        this.location = location;
        this.creator = creator;
    }

    public long getId() {
        return id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public boolean isDuration() {
        return isDuration;
    }

    public void setIsDuration(boolean isDuration) {
        this.isDuration = isDuration;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Profile getCreator() {
        return creator;
    }

}
