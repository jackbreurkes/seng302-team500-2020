package com.springvuegradle.model.repository;

import java.math.BigInteger;
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


	public List<List<String>> findByFirstLastname(String firstName, String lastName);

	public List<String> findByFirstName(String firstLetterName);





    public Optional<Profile> findById(User user);
	public List<Profile> findByNickNameStartingWith(String nickname);
	public List<Profile> findByFirstNameStartingWithAndLastNameStartingWith(String firstname, String lastname);
	public List<Profile> findByFirstNameStartingWithAndMiddleNameStartingWithAndLastNameStartingWith(String firstname, String middlename, String lastname);

	/**
	 * fetches all Profiles whose activity types contain any of the given activity types.
	 * i.e. the intersection of the given activity type names and the profile's activity types is non-empty.
	 * @param activityTypeNames the activity type names to search profiles for
	 * @return a list of all profiles who have an associated activity type that matches any of the given names
	 */
	@Query(
			value = "SELECT DISTINCT p FROM Profile p JOIN p.activityTypes a WHERE a.activityTypeName IN ?1"
	)
	public List<Profile> findByActivityTypesContainsAnyOf(Collection<String> activityTypeNames);

	/**
	 * fetches all Profiles whose activity types contain all of the given activity types.
	 * i.e. the set of given activity type names is a subset of the profile's activity types and is not empty.
	 * @param activityTypeNames the activity type names to search profiles for
	 * @return a list of all profiles who have an associated activity type that matches any of the given names
	 */
	@Query(value = "SELECT p FROM Profile p WHERE " +
			"(SELECT COUNT(p2) FROM Profile p2 JOIN p2.activityTypes a1 WHERE p.id = p2.id AND a1.activityTypeName IN ?1)" +
			" = " +
			"(SELECT COUNT(a2) FROM ActivityType a2 WHERE a2.activityTypeName IN ?1) " +
			"AND " +
			"(SELECT COUNT(a2) FROM ActivityType a2 WHERE a2.activityTypeName IN ?1) > 0")
	public List<Profile> findByActivityTypesContainsAllOf(Collection<String> activityTypeNames);

	@Query(value = "SELECT a.activityTypeName FROM ActivityType a WHERE a.activityTypeName IN ?1")
	public List<String> test(Collection<String> activityTypeNames);
}
