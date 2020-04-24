package com.springvuegradle.model.data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Activity {

    @Id
    @GeneratedValue
    private long id;

    @Column(columnDefinition = "varchar(30) not null")
    private String activityName;

    @Column(columnDefinition = "boolean not null")
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

    @Column(columnDefinition = "varchar(30) not null")
    private String location;

    @ManyToOne
    private Profile creator;
    //TODO activity types

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

}
