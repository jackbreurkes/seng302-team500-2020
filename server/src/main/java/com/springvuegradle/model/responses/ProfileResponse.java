package com.springvuegradle.model.responses;

import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;

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
    private final String[] passports;
    private final String[] additional_email;

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
        passports = new String[0];
        additional_email = new String[0];

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date_of_birth = sdf.format(profile.getDob());
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

    public String[] getPassports() {
        return passports;
    }

    public String[] getAdditional_email() {
        return additional_email;
    }
}
