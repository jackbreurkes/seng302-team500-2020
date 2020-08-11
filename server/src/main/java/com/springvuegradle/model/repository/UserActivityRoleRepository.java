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

    /**
     * Named query for getting the table entry to ret
     * @param uuid of user
     * @return the role entry in an activity that a user is involved in
     */
    @Query(
            value = "SELECT a FROM UserActivityRole a WHERE a.user.uuid = ?1 AND a.activity.activity_id = ?2"
    )
    public Optional<UserActivityRole> getRoleEntryByUserId(long uuid, long activity_id);

    /**
     * Updates the role for a given user in a given activity
     * @param activityRole The new role of the user
     * @param uuid The users id
     * @param activity_id The activity id
     */
    @Transactional
    @Modifying
    @Query(
            value = "UPDATE UserActivityRole SET activityRole = ?1 where user.uuid = ?2 and activity.activity_id = ?3"
    )
    public void updateUserActivityRole(ActivityRole activityRole, long uuid, long activity_id);
    
}
