/*package com.springvuegradle.model.requests;



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
public class EmailObjectMapper {

	@JsonProperty(value = "additional_emails", required = false)
    private List<String> additional_emails;
	
    private List<String> parseErrors = new ArrayList<>();

    protected EmailObjectMapper() {}

    public List<String> getAdditionalEmails() {
        return additional_emails;
    }

    public void setAdditionalEmails(List<String> emails) {
    	for (String email: emails) {
    		if (!FormValidator.validateEmail(email)) {
            parseErrors.add(new String("invalid email address"));
    		}
    	}
        this.additional_emails = emails;
    }

    private void checkParseErrors() throws InvalidRequestFieldException {
        if (parseErrors.size() > 0) {
            throw new InvalidRequestFieldException(parseErrors.get(0));
        }
    }

    public void updateEmailList(User user, EmailRepository emailRepository) throws InvalidRequestFieldException {
        this.additional_emails = emails;
        for (String newEmail: emails) {
            emailRepository.save((S) new Email(newEmail));        	
        }
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
}*/