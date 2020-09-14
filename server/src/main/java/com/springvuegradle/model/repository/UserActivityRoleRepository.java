package com.springvuegradle.model.repository;

import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityRole;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.data.UserActivityRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


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
     * @param activityId of activity
     * @return list of activities that user is involved in
     */
        @Query(
            value= "SELECT u FROM User u JOIN UserActivityRole a ON a.user.uuid = u.uuid WHERE a.activity.activity_id = ?1"
    )
    public List<User> getInvolvedUsersByActivityId(long activityId);

	/**
	 * Named query for getting the number of participants in an activity
	 * @param activityId of activity
	 * @return amount of users with participant role in the given activity
	 */
		@Query(
			value= "SELECT COUNT(a) FROM UserActivityRole a WHERE a.activity.activity_id = ?1 AND a.activityRole = com.springvuegradle.model.data.ActivityRole.PARTICIPANT"
	)
	Long getParticipantCountByActivityId(long activityId);

    /**
     * retrieves the role of a user in a given activity.
     * @param uuid the id of the user
     * @param activityId the id of the activity
     * @return the role entry in an activity that a user is involved in
     */
    @Query(
            value = "SELECT a FROM UserActivityRole a WHERE a.user.uuid = ?1 AND a.activity.activity_id = ?2"
    )
    Optional<UserActivityRole> getRoleEntryByUserId(long uuid, long activityId);

    /**
     * Updates the role for a given user in a given activity
     * @param activityRole The new role of the user
     * @param uuid The users id
     * @param activityId The activity id
     */
    @Transactional
    @Modifying
    @Query(
            value = "UPDATE UserActivityRole SET activityRole = ?1 where user.uuid = ?2 and activity.activity_id = ?3"
    )
    public void updateUserActivityRole(ActivityRole activityRole, long uuid, long activityId);

    public List<UserActivityRole> getAllByActivityAndActivityRole(Activity activity, ActivityRole role);
    
}
