package com.springvuegradle.model.requests;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Country;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Location;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.CountryRepository;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.LocationRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.util.FormValidator;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProfileObjectMapper {

	@JsonProperty(value = "primary_email", required = false)
    private String primaryEmail;
	
	@JsonProperty(value = "firstname", required = false)
    private String fname;
	
	@JsonProperty(value = "lastname", required = false)
	private String lname;
	
	@JsonProperty(value = "middlename", required = false)
	private String mname;
	
	@JsonProperty(value = "nickname", required = false)
	private String nickname;
	
	@JsonProperty(value = "password", required = false)
	private String password;
	
	@JsonProperty(value = "bio", required = false)
	private String bio;
	
	@JsonProperty(value = "date_of_birth", required = false)
    private String dob;
    
	@JsonProperty(value = "gender", required = false)
    private String gender;
    
	@Min(-1)
	@Max(5)
	@JsonProperty(value = "fitness", required = false)
    private Integer fitness;

    public String[] getPassports() {
        return passports;
    }

    @JsonProperty(value = "passports", required = false)
    private String[] passports;
    
    @JsonProperty(value = "activities", required = false)
    private List<String> activities;

    @JsonProperty(required = false)
    private Location location;
    
    private List<String> parseErrors = new ArrayList<>();

    public ProfileObjectMapper() {}

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primary_email) {
        if (!FormValidator.validateEmail(primary_email)) {
            parseErrors.add(new String("Invalid e-mail address"));
        }
        this.primaryEmail = primary_email;
    }

    public String getFirstname() {
        return fname;
    }

    public void setFirstname(String fname) {
        if (!FormValidator.validateName(fname)) {
            parseErrors.add( new String("Invalid first name"));
        }
        this.fname = fname;
    }

    public String getLastname() {
        return lname;
    }

    public void setLastname(String lname) {
        if (!FormValidator.validateName(lname)) {
            parseErrors.add( new String("Invalid last name"));
        }
        this.lname = lname;
    }

    public String getMiddlename() {
        return mname;
    }

    public void setMiddlename(String mname) {
        if (mname.equals("")) {
            mname = null;
        }
        if (mname != null && !FormValidator.validateName(mname)) {
            parseErrors.add( new String("Invalid middle name"));
        }
        this.mname = mname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        if (nickname.equals("")) {
            nickname = null;
        }
        if (nickname != null && !FormValidator.validateNickname(nickname)) {
            parseErrors.add( new String("Invalid nickname"));
        }
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!FormValidator.validatePassword(password)) {
            parseErrors.add("Invalid password");
        }
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        if (bio.equals("")) {
            bio = null;
        }
        if (bio != null && !FormValidator.validateBio(bio)) {
            parseErrors.add( new String("Invalid bio"));
        }
        this.bio = bio;
    }

    public String getDateOfBirth() {
        return dob;
    }

    public void setDateOfBirth(String dateOfBirth) {
        if (!FormValidator.validateDateOfBirth(dateOfBirth)) {
            parseErrors.add( new String("Invalid date of birth"));
        }
        this.dob = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (!FormValidator.validateGender(gender)) {
            parseErrors.add( new String("Invalid gender"));
        }
        this.gender = gender;
    }

    public Integer getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public void setPassports(String[] passports) {
        if (!FormValidator.validatePassportCountries(passports)) {
            parseErrors.add("invalid passport countries");
        }
        this.passports = passports;
    }
    
    public void setActivities(List<String> activities) {
    	this.activities = activities;
    }
    
    public List<String> getActivities() {
        return activities;
    }

    /**
     * @return the location the user is normally based in
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location the user is normally based in
     */
    public void setLocation(Location location) {
        if (location.getCity() == null || location.getCountry() == null) {
            this.parseErrors.add("location must have a city and country");
        }
        this.location = location;
    }


    public void checkParseErrors() throws InvalidRequestFieldException {
        if (parseErrors.size() > 0) {
            throw new InvalidRequestFieldException(parseErrors.get(0));
        }
    }

    private List<Country> countries(String[] countryNames, CountryRepository countryRepository) throws RecordNotFoundException {
        List<Country> countries = new ArrayList<>();
        for (String countryName : countryNames) {
            Optional<Country> country = countryRepository.findByName(countryName);
            if (country.isEmpty()) {
                throw new RecordNotFoundException("country " + countryName + " not found");
            }
            countries.add(country.get());
        }
        return countries;
    }

    public User createNewProfile(UserRepository userRepository,
                                 EmailRepository emailRepository,
                                 ProfileRepository profileRepository,
                                 CountryRepository countryRepository,
                                 ActivityTypeRepository activityTypeRepository,
                                 LocationRepository locationRepository) throws InvalidRequestFieldException, ParseException, NoSuchAlgorithmException, RecordNotFoundException {
        checkParseErrors();
        checkRequiredFields();
        if (emailRepository.existsById(getPrimaryEmail())) {
            throw new InvalidRequestFieldException("E-mail already in use");
        }
        User user = new User();
        LocalDate dob = FormValidator.getValidDateOfBirth(getDateOfBirth());
        Gender gender = Gender.matchGender(this.gender);
        Profile profile = new Profile(user, getFirstname(), getLastname(), dob, gender);

        profile.setBio(bio);
        profile.setMiddleName(getMiddlename());
        profile.setNickName(getNickname());
        if (this.passports != null) {
            profile.setCountries(countries(this.passports, countryRepository));
        }

        if (this.location != null) {
            this.location = addLocationIfNotExisting(this.location.lookupAndValidate(), locationRepository);
            profile.setLocation(this.location);
        }
        if (this.fitness == null) {
            this.fitness = -1;  // default to no fitness level
        }
        profile.setFitness(this.fitness);
        
        List<ActivityType> activityTypes = new ArrayList<>();
        if (this.activities != null) {
	        // validate activity types
	        for (String activityTypeName : getActivities()) {
	            Optional<ActivityType> optionalActivityType = activityTypeRepository.getActivityTypeByActivityTypeName(activityTypeName);
	            System.out.println(activityTypeName);
	            if (optionalActivityType.isPresent()) {
	                activityTypes.add(optionalActivityType.get());
	            } else {
	                throw new RecordNotFoundException("no activity type with name " + activityTypeName + " found");
	            }
	        }
        }
        // if no activity types are to be associated with the profile
        if (activityTypes.size() == 0) {
        	activityTypes = null;
        }
                
        // workaround since userid is not known until saved to the DB
        userRepository.save(user);
        user.setPassword(password);
        userRepository.save(user);

        Email dbemail = new Email(user, getPrimaryEmail(), true);
        emailRepository.save(dbemail);
        
        profile.setActivityTypes(activityTypes);

        profileRepository.save(profile);

        return user;
    }

    private void checkRequiredFields() throws InvalidRequestFieldException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFormat.parse(getDateOfBirth());
        } catch (ParseException e) {
            throw new InvalidRequestFieldException("Invalid date");
        }
        if (getFirstname() == null) {
            throw new InvalidRequestFieldException("No firstname field");
        }
        if (getLastname() == null) {
            throw new InvalidRequestFieldException("No lastname field");
        }
        if (getPassword() == null) {
            throw new InvalidRequestFieldException("No password field");
        }
        if (Gender.matchGender(this.gender) == null) {
            throw new InvalidRequestFieldException("Invalid gender");
        }
        if (getPrimaryEmail() == null) {
            throw new InvalidRequestFieldException("No primary email");
        }
    }

    /**
     * adds a location to the database if it doesn't exist, otherwise returns the existing value.
     * @param location the location to find a match for
     * @return a location from the database
     */
    private Location addLocationIfNotExisting(Location location, LocationRepository locationRepository) {
        Optional<Location> existing = locationRepository.findLocationByCityAndCountry(location.getCity(), location.getCountry());
        if (existing.isPresent()) {
            return existing.get();
        }

        return locationRepository.save(location);
    }
}