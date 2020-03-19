package com.springvuegradle.model.data;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.*;

/**
 * JPA class for user profiles in persistent storage
 * @author Alex Hobson
 *
 */
@Entity
@Table(name = "profile")
/*@NamedQuery(name = "updateFirstName", query = "update profile p set p.firstName = ?1 where p.user = ?2")
@NamedQuery(name = "updateLastName", query = "update profile p set p.lastName = ?1 where p.user = ?2")
@NamedQuery(name = "updateMiddleName", query = "update profile p set p.middleName = ?1 where p.user = ?2")
@NamedQuery(name = "updateNickName", query = "update profile p set p.nickname = ?1 where p.user = ?2")
@NamedQuery(name = "updateFitness", query = "update profile p set p.fitness = ?1 where p.user = ?2")
@NamedQuery(name = "updateBio", query = "update profile p set p.bio = ?1 where p.user = ?2")
@NamedQuery(name = "updateDOB", query = "update profile p set p.dob = ?1 where p.user = ?2")
@NamedQuery(name = "updateGender", query = "update profile p set p.gender = ?1 where p.user = ?2")
@NamedQuery(name = "Profile.findById", query = "select p from Profile p where p.user = ?1")
@NamedQuery(name = "Profile.updateFitness", query = "update Profile p set p.fitness = ?2 where p.user = ?1")*/
public class Profile implements Serializable {

	/**
	 * Auto generated serial ID
	 */
	private static final long serialVersionUID = 8073593111622309803L;

	/**
	 * User this profile represents
	 */

	@Id
	private long id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "uuid")
	private User user;
	
    /**
     * Fitness level of the user (default null or -1)
     */
    @Column(columnDefinition = "smallint default -1")
    private int fitness;
    
    /**
     * First name of the profile
     */
    @Column(columnDefinition = "varchar(30) not null")
    private String firstName;
    
    /**
     * Last name of the profile
     */
    @Column(columnDefinition = "varchar(30) not null")
    private String lastName;
    
    /**
     * Middle name of the profile
     */
    @Column(columnDefinition = "varchar(30)")
    private String middleName;
    
    /**
     * Nickname of the profile
     */
    @Column(columnDefinition = "varchar(30)")
    private String nickName;
    
    /**
     * Bio of the profile
     */
    @Column(columnDefinition = "text")
    private String bio;
    
    /**
     * Date of birth of the profile
     */
    private LocalDate dob;
    
    /**
     * Gender of the profile
     */
    private Gender gender;
    
    /**
     * Constructor required by spring
     */
    protected Profile() {}
    
    /**
     * Create a profile with the minimum required values
     * @param user User this profile belongs to
     * @param firstName First name of the user
     * @param lastName Last name of the user
     * @param dob Date of birth of the user
     * @param gender Gender of the user
     */
    public Profile(User user, String firstName, String lastName, LocalDate dob, Gender gender) {
    	this.user = user;
    	this.id = user.getUserId();
    	this.firstName = firstName;
    	this.lastName = lastName;
    	this.dob = dob;
    	this.gender = gender;
    }

    /**
     * Get the user this profile belongs to
     * @return User this profile belongs to
     */
	public User getUser() {
		return user;
	}

	/**
	 * Get the fitness level of the user
	 * @return fitness level or -1 if unspecified
	 */
	public int getFitness() {
		return fitness;
	}

	/**
	 * Get the first name of the profile
	 * @return first name of the profile
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Get the last name of the profile
	 * @return last name of the profile
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Get the middle name of the profile
	 * @return middle name of the profile or empty string if undefined
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * Get the nickname of the profile
	 * @return nickname of the profile or empty string if undefined
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * Get the bio of this profile
	 * @return bio of the profile or empty string if undefined
	 */
	public String getBio() {
		return bio;
	}

	/**
	 * Get the date of birth of this profile
	 * @return date of birth of this profile
	 */
	public LocalDate getDob() {
		return dob;
	}

	/**
	 * Get the gender of this profile
	 * @return gender of this profile
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * Set the fitness level of the profile
	 * @param fitness Fitness level of the profile or -1 to delete
	 */
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	/**
	 * Set the first name of this profile
	 * @param firstName First name to set to (mandatory field cannot be null)
	 */
	public void setFirstName(String firstName) {
		if (firstName == null || firstName.length() == 0) {
			throw new IllegalArgumentException("Cannot set first name to null (mandatory field)");
		}
		this.firstName = firstName;
	}

	/**
	 * Set the last name of this profile
	 * @param lastName Last name to set to (mandatory field cannot be null)
	 */
	public void setLastName(String lastName) {
		if (lastName == null || lastName.length() == 0) {
			throw new IllegalArgumentException("Cannot set last name to null (mandatory field)");
		}
		this.lastName = lastName;
	}

	/**
	 * Set the middle name of this profile
	 * @param middleName Middle name to set to
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * Set the nickname of this profile
	 * @param nickName Nick name of this profile
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/**
	 * Set the bio (biography) of this profile
	 * @param bio Biography to set to
	 */
	public void setBio(String bio) {
		this.bio = bio;
	}

	/**
	 * Set the date of birth of this profile
	 * @param dob DOB to set to (mandatory field cannot be null)
	 */
	public void setDob(LocalDate dob) {
		if (dob == null) {
			throw new IllegalArgumentException("Cannot set dob to null (mandatory field)");
		}
		this.dob = dob;
	}

	/**
	 * Set the profile's gender
	 * @param gender Gender to set the profile to
	 */
	public void setGender(Gender gender) {
		if (gender == null) {
			 throw new IllegalArgumentException("Cannot set gender to null (mandatory field)");
		}
		this.gender = gender;
	}
}
