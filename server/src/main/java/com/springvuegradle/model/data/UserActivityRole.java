package com.springvuegradle.model.data;

import javax.annotation.processing.Generated;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class UserActivityRole {

    @NotNull
    @GeneratedValue
    @Id
    private long userActivityRoleId;

    @OneToOne
    @JoinColumn(columnDefinition = "activity_id")
    private Activity activity;

    @OneToOne
    @JoinColumn(columnDefinition = "uuid")
    private User user;

    @NotNull
    private ActivityRole activityRole;


    public UserActivityRole(){}

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
