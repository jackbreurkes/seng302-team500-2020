package com.springvuegradle.model.requests;



import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.CountryRepository;
import com.springvuegradle.model.repository.EmailRepository;
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
    
	@JsonProperty(value = "fitness", required = false)
    private Integer fitness;

    public String[] getPassports() {
        return passports;
    }

    @JsonProperty(value = "passports", required = false)
    private String[] passports;
    
    private List<String> parseErrors = new ArrayList<>();

    protected ProfileObjectMapper() {}

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primary_email) {
        if (!FormValidator.validateEmail(primary_email)) {
            parseErrors.add(new String("invalid email address"));
        }
        this.primaryEmail = primary_email;
    }

    public String getFirstname() {
        return fname;
    }

    public void setFirstname(String fname) {
        if (!FormValidator.validateName(fname)) {
            parseErrors.add( new String("invalid first name"));
        }
        this.fname = fname;
    }

    public String getLastname() {
        return lname;
    }

    public void setLastname(String lname) {
        if (!FormValidator.validateName(lname)) {
            parseErrors.add( new String("invalid last name"));
        }
        this.lname = lname;
    }

    public String getMiddlename() {
        return mname;
    }

    public void setMiddlename(String mname) {
        if (!FormValidator.validateName(mname)) {
            parseErrors.add( new String("invalid middle name"));
        }
        this.mname = mname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        if (!FormValidator.validateNickname(nickname)) {
            parseErrors.add( new String("invalid nickname"));
        }
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!FormValidator.validatePassword(password)) {
            parseErrors.add("invalid password");
        }
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        if (!FormValidator.validateBio(bio)) {
            parseErrors.add( new String("invalid bio"));
        }
        this.bio = bio;
    }

    public String getDateOfBirth() {
        return dob;
    }

    public void setDateOfBirth(String dateOfBirth) {
        if (!FormValidator.validateDateOfBirth(dateOfBirth)) {
            parseErrors.add( new String("invalid date of birth"));
        }
        this.dob = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (!FormValidator.validateGender(gender)) {
            parseErrors.add( new String("invalid gender"));
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

    private void checkParseErrors() throws InvalidRequestFieldException {
        if (parseErrors.size() > 0) {
            throw new InvalidRequestFieldException(parseErrors.get(0));
        }
    }

    public void updateExistingProfile(Profile profile, ProfileRepository profileRepository, CountryRepository countryRepository) throws InvalidRequestFieldException, RecordNotFoundException {
        checkParseErrors();
        if (this.fname != null) {
            profile.setFirstName(this.fname);
        }
        if (this.lname != null) {
            profile.setLastName(this.lname);
        }
        if (this.mname != null) {
            profile.setMiddleName(this.mname);
        }
        /*if (this.nickname != null) {
            profile.setNickName(this.nickname);
        }*/
        profile.setNickName(this.nickname);
        if (this.bio != null) {
            profile.setBio(this.bio);
        }
        if (this.dob != null) {
            LocalDate validDob = FormValidator.getValidDateOfBirth(this.dob);
            if (validDob != null) {
                profile.setDob(validDob);
            }
        }
        if (this.gender != null) {
            Gender gender = Gender.matchGender(this.gender);
            if (gender != null) {
                profile.setGender(gender);
            }
        }
        if (this.fitness != null) {
            profile.setFitness(this.fitness);
        }
        if (this.passports != null) {
            profile.setCountries(countries(this.passports, countryRepository));
        }
        profileRepository.save(profile);
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
                                 CountryRepository countryRepository) throws InvalidRequestFieldException, ParseException, NoSuchAlgorithmException, RecordNotFoundException {
        checkParseErrors();
        checkRequiredFields();
        if (emailRepository.existsById(getPrimaryEmail())) {
            throw new InvalidRequestFieldException("email already in use");
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

        // workaround since userid is not known until saved to the DB
        userRepository.save(user);
        user.setPassword(password);
        userRepository.save(user);

        Email dbemail = new Email(user, getPrimaryEmail(), true);
        emailRepository.save(dbemail);

        profileRepository.save(profile);

        return user;
    }

    private void checkRequiredFields() throws InvalidRequestFieldException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFormat.parse(getDateOfBirth());
        } catch (ParseException e) {
            throw new InvalidRequestFieldException("invalid date");
        }
        if (getFirstname() == null) {
            throw new InvalidRequestFieldException("no firstname field");
        }
        if (getLastname() == null) {
            throw new InvalidRequestFieldException("no lastname field");
        }
        if (getPassword() == null) {
            throw new InvalidRequestFieldException("no password field");
        }
        if (Gender.matchGender(this.gender) == null) {
            throw new InvalidRequestFieldException("invalid gender");
        }
        if (getPrimaryEmail() == null) {
            throw new InvalidRequestFieldException("no primary email");
        }
    }
}