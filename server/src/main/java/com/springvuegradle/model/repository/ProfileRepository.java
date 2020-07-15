package com.springvuegradle.model.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.springvuegradle.model.data.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import org.springframework.data.jpa.repository.Query;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    public Optional<Profile> findById(User user);
	public List<Profile> findByNickNameStartingWith(String nickname);
	public List<Profile> findByFirstNameStartingWithAndLastNameStartingWith(String firstname, String lastname);
	public List<Profile> findByFirstNameStartingWithAndMiddleNameStartingWithAndLastNameStartingWith(String firstname, String middlename, String lastname);

	/**
	 * fetches all Profiles whose activity types contain any of the given activity types.
	 * @param activityTypeNames the activity type names to search profiles for
	 * @return a list of all profiles who have an associated activity type that matches any of the given names
	 */
	@Query(
			value = "SELECT DISTINCT p FROM Profile p JOIN p.activityTypes a WHERE a.activityTypeName IN ?1"
	)
	public List<Profile> findByActivityTypesContainsAnyOf(Collection<String> activityTypeNames);

	@Query(
			value = "select * from Profile where 1 = 2",
			nativeQuery = true
	)
	public List<Profile> findByActivityTypesContainsAllOf(Collection<String> activityTypeNames);
}
