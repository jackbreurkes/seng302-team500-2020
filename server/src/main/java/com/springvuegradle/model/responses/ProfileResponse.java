package com.springvuegradle.model.responses;

import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Country;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProfileResponse {

    private final long profile_id;
    private final String lastname;
    private final String firstname;
    private final String middlename;
    private final String nickname;
    private final String primary_email;
    private final String bio;
    private final String date_of_birth;
    private final String gender;
    private final int fitness;
    private final List<String> passports = new ArrayList<>();
    private final String[] additional_email;
    private final List<String> activities = new ArrayList<>();

    /**
     * @param profile the profile whose information should populate the response fields
     * @param emailRepository an email repository to use when getting the user's primary email
     */
    public ProfileResponse(Profile profile, EmailRepository emailRepository) {
        profile_id = profile.getUser().getUserId();
        lastname = profile.getLastName();
        firstname = profile.getFirstName();
        middlename = profile.getMiddleName();
        nickname = profile.getNickName();
        primary_email = emailRepository.getPrimaryEmail(profile.getUser());
        bio = profile.getBio();
        gender = profile.getGender().getJsonName();
        fitness = profile.getFitness() != -1 ? profile.getFitness() : 0;
        for (Country country : profile.getCountries()) {
            passports.add(country.getName());
        }
        ArrayList<String> emailArray = new ArrayList<String>();
        for (Email email: emailRepository.getNonPrimaryEmails(profile.getUser())) {
        	emailArray.add(email.getEmail());
        }
        additional_email = emailArray.toArray(new String[emailArray.size()]);
        date_of_birth = profile.getDob().format(DateTimeFormatter.ISO_LOCAL_DATE);
        for (ActivityType activityType : profile.getActivityTypes()) {
            activities.add(activityType.getActivityTypeName());
        }
    }

    public long getProfile_id() {
        return profile_id;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPrimary_email() {
        return primary_email;
    }

    public String getBio() {
        return bio;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getGender() {
        return gender;
    }

    public int getFitness() {
        return fitness;
    }

    public List<String> getPassports() {
        return passports;
    }

    public String[] getAdditional_email() {
        return additional_email;
    }

    /**
     * @return the list of activity type names the user is interested in
     */
    public List<String> getActivities() {
        return activities;
    }
}
