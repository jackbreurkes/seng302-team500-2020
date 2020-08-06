package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.data.UserActivityRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * JPA Repository of user_activity_roles
 */
public interface UserActivityRoleRepository extends JpaRepository<UserActivityRole, Long> {

    /**
     * Named query for getting involved activities for a certain user
     * @param uuid of user
     * @return list of activities that user is involved in
     */
    @Query(
            value= "SELECT a FROM Activity a JOIN UserActivityRole r ON a.activity_id = r.activity.activity_id WHERE r.user.uuid = ?1"
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

        /**
         * Named query for getting the number of participants in an activity
         * @param activity_id of activity
         * @return amount of users with participant role in the given activity
         */
            @Query(
                value= "SELECT COUNT(a) FROM UserActivityRole a WHERE a.activity.activity_id = ?1 AND a.activityRole = com.springvuegradle.model.data.ActivityRole.PARTICIPANT"
        )
        public Long getParticipantCountByActivityId(long activity_id);
}
