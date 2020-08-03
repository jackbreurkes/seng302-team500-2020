package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.data.UserActivityRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * INSERT INTO USER_ACTIVITY_ROLE (USER_UUID, ACTIVITY_ACTIVITY_ID, ACTIVITY_ROLE, USER_ACTIVITY_ROLE_ID) VALUES (5, 3, 6, 11)
 * SELECT u FROM UserActivityRole u JOIN Activity ON Activity.activity_id = u.activity.activity_id WHERE u.user.uuid = ?1
 * SELECT a FROM UserActivityRole a JOIN User ON User.uuid = a.user.uuid WHERE a.activity.activity_id = ?1
 */
public interface UserActivityRoleRepository extends JpaRepository<UserActivityRole, Long> {

    /**
     * Named query for getting involved activities for a certain user
     * @param uuid of user
     * @return list of activities that user is involved in
     */
    @Query(
            value= "SELECT a FROM Activity a JOIN UserActivityRole u ON u.activity.activity_id = a.activity_id WHERE u.activity.activity_id= ?1"
    )
    public List<Activity> getInvolvedActivitiesByUserId(long uuid);

    /**
     * Named query for getting all info on users who are involved with an activity
     * @param activity_id of activity
     * @return list of activities that user is involved in
     */
        @Query(
            value= "SELECT u FROM User u JOIN UserActivityRole a ON a.user.uuid = u.uuid WHERE a.activity.activity_id = ?1"
    )
    public List<User> getInvolvedUsersByActivityId(long activity_id);

}
