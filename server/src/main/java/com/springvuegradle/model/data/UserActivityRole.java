package com.springvuegradle.model.data;

import javax.annotation.processing.Generated;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class UserActivityRole {

    /**
     * A generated ID identifier for each user-activity-role relationship
     */
    @NotNull
    @GeneratedValue
    @Id
    private long userActivityRoleId;

    /**
     * The ID of the activity the user is a part of
     */
    @OneToOne
    @JoinColumn(columnDefinition = "activity_id")
    private Activity activity;

    /**
     * The ID of the user
     */
    @OneToOne
    @JoinColumn(columnDefinition = "uuid")
    private User user;

    /**
     * The specific role of a user in an activity
     */
    @NotNull
    private ActivityRole activityRole;

    /**
     * Default JPA Constructor
     */
    public UserActivityRole(){}

    /**
     * Create an object that represents the role a user has for a specific activity
     * @param activity The ID of the activity the user is a part of
     * @param user The ID of the user
     * @param role The new role of a user for an activity
     */
    public UserActivityRole(Activity activity, User user, ActivityRole role){
        this.activity = activity;
        this.user = user;
        this.activityRole = role;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ActivityRole getActivityRole() {
        return activityRole;
    }

    public void setActivityRole(ActivityRole activityRole) {
        this.activityRole = activityRole;
    }
}
