package com.springvuegradle.model.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class UserActivityRole {

    @OneToOne()
    @JoinColumn(columnDefinition = "activity_id")
    private Activity activity;

    @NotNull
    @Id
    private long activity_id;

    @NotNull
    private long uuid;

    @ManyToOne
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

    public long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(long activity_id) {
        this.activity_id = activity_id;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
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
