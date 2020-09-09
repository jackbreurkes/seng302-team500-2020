package com.springvuegradle.model.responses;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Country;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Location;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.repository.EmailRepository;

public class ProfileResponse {

    private final long profile_id;
    private final int permission_level;
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
    private float lat;
    private float lon;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Location location;

    /**
     * @param profile the profile whose information should populate the response fields
     * @param emailRepository an email repository to use when getting the user's primary email
     */
    public ProfileResponse(Profile profile, EmailRepository emailRepository) {
        profile_id = profile.getUser().getUserId();
        permission_level = profile.getUser().getPermissionLevel();
        lastname = profile.getLastName();
        firstname = profile.getFirstName();
        middlename = profile.getMiddleName();
        nickname = profile.getNickName();
        primary_email = emailRepository.getPrimaryEmail(profile.getUser());
        bio = profile.getBio();
        gender = profile.getGender().getJsonName();
        fitness = profile.getFitness();
        if (profile.getCountries() != null) {
            for (Country country : profile.getCountries()) {
                passports.add(country.getName());
            }
        }
        ArrayList<String> emailArray = new ArrayList<String>();
        for (Email email: emailRepository.getNonPrimaryEmails(profile.getUser())) {
        	emailArray.add(email.getEmail());
        }
        additional_email = emailArray.toArray(new String[emailArray.size()]);
        date_of_birth = profile.getDob().format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (profile.getActivityTypes() != null) {
            for (ActivityType activityType : profile.getActivityTypes()) {
                activities.add(activityType.getActivityTypeName());
            }
        }
        location = profile.getLocation();
    }

    public void setLat(float lat) { this.lat = lat; }

    public void setLon(float lon) { this.lon = lon; }

    public long getProfile_id() {
        return profile_id;
    }

    public int getPermission_level() { return permission_level; }

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

    /**
     * @return the location in which the user is normally based
     */
    public Location getLocation() {
        return location;
    }

    public float getLat() { return lat; }

    public float getLon() { return lon; }
}
