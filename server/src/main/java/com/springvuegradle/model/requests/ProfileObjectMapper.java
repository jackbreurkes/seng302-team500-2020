package com.springvuegradle.model.requests;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.util.FormValidator;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProfileObjectMapper {

    private String primary_email;
    private String fname, lname, mname, nickname, password, bio;
    private String dob;
    private String gender;
    private Integer fitness;
    private List<String> parseErrors = new ArrayList<>();

    private SimpleDateFormat format;

    public ProfileObjectMapper() {}

    public String getPrimaryEmail() {
        return primary_email;
    }

    public void setPrimaryEmail(String primary_email) throws InvalidRequestFieldException {
        if (!FormValidator.validateEmail(primary_email, true)) {
            parseErrors.add(new String("invalid email address"));
        }
        this.primary_email = primary_email;
    }

    public String getFirstname() {
        return fname;
    }

    public void setFirstname(String fname) {
        if (!FormValidator.validateName(fname, true)) {
            parseErrors.add( new String("invalid first name"));
        }
        this.fname = fname;
    }

    public String getLastname() {
        return lname;
    }

    public void setLastname(String lname) {
        if (!FormValidator.validateName(lname, true)) {
            parseErrors.add( new String("invalid last name"));
        }
        this.lname = lname;
    }

    public String getMiddlename() {
        return mname;
    }

    public void setMiddlename(String mname) {
        if (!FormValidator.validateName(mname, true)) {
            parseErrors.add( new String("invalid middle name"));
        }
        this.mname = mname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        if (!FormValidator.validateNickname(nickname, true)) {
            parseErrors.add( new String("invalid nickname"));
        }
        this.nickname = nickname;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!FormValidator.validatePassword(password, true)) {
            parseErrors.add("invalid password");
        }
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        if (!FormValidator.validateBio(bio, true)) {
            parseErrors.add( new String("invalid bio"));
        }
        this.bio = bio;
    }

    public String getDateOfBirth() {
        return dob;
    }

    public void setDateOfBirth(String dateOfBirth) {
        if (!FormValidator.validateDateOfBirth(dateOfBirth, true)) {
            parseErrors.add( new String("invalid date of birth"));
        }
        this.dob = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (!FormValidator.validateGender(gender, true)) {
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
            Date validDob = FormValidator.getValidDateOfBirth(this.dob);
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
            throw new InvalidRequestFieldException("email already in use");
        }
        User user = new User();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dob = dateFormat.parse(getDateOfBirth());
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
            Date dob = dateFormat.parse(getDateOfBirth());
        } catch (ParseException e) {
            throw new InvalidRequestFieldException("invalid date");
        }
        if (getFirstname() == null) {
            throw new InvalidRequestFieldException("no firstname field");
        }
        if (getLastname() == null) {
            throw new InvalidRequestFieldException("no lastname field");
        }
        if (Gender.matchGender(this.gender) == null) {
            throw new InvalidRequestFieldException("invalid gender");
        }
        if (getPrimaryEmail() == null) {
            throw new InvalidRequestFieldException("no primary email");
        }
    }
}