package com.springvuegradle.model.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * JPA POJO representing an ActivityType.
 * @author Jack van Heugten Breurkes
 */
@Entity
@Table(name = "activity_type")
public class ActivityType {

    @Id
    @GeneratedValue
    private long activityTypeId;

    @NotNull
    @Column(unique = true, columnDefinition = "varchar(30)")
    private String activityTypeName;

    /**
     * no arg constructor required by JPA
     */
    public ActivityType() {}

    /**
     * creates a new activity type with the given name.
     * @param activityTypeName a name that should be unique among all activity types
     */
    public ActivityType(String activityTypeName) {
        this.activityTypeName = activityTypeName;
    }

    /**
     * @return the ID of the activity type
     */
    public long getActivityTypeId() {
        return activityTypeId;
    }

    /**
     * @return the name of the activity type
     */
    public String getActivityTypeName() {
        return activityTypeName;
    }
}
