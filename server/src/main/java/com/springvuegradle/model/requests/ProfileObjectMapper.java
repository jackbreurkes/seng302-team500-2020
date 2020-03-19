package com.springvuegradle.model.requests;



import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
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
    
    private List<String> parseErrors = new ArrayList<>();

    protected ProfileObjectMapper() {}

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
        if (!FormValidator.validateName(mname)) {
            parseErrors.add( new String("Invalid middle name"));
        }
        this.mname = mname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        if (!FormValidator.validateNickname(nickname)) {
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
        if (!FormValidator.validateBio(bio)) {
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

    private void checkParseErrors() throws InvalidRequestFieldException {
        if (parseErrors.size() > 0) {
            throw new InvalidRequestFieldException(parseErrors.get(0));
        }
    }

    public void updateExistingProfile(Profile profile, ProfileRepository profileRepository) throws InvalidRequestFieldException {
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
        if (this.nickname != null) {
            profile.setNickName(this.nickname);
        }
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
        profileRepository.save(profile);
    }

    public User createNewProfile(UserRepository userRepository,
                                 EmailRepository emailRepository,
                                 ProfileRepository profileRepository) throws InvalidRequestFieldException, ParseException, NoSuchAlgorithmException {
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
}