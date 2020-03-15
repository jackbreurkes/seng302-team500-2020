package com.springvuegradle.model.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.text.SimpleDateFormat;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateProfileRequest {
    private String primary_email, fname, lname, mname, nickname, password, bio;
    private String dob;
    private String gender;

    private SimpleDateFormat format;

    protected UpdateProfileRequest() {}

    //TODO Validate this and maybe fix up the input (i.e will probs pass a string for the DOB

    // Some attributes can be null, instead of creating a bunch of constructors just let the DB validate it
    public UpdateProfileRequest(String lastname, String firstname, String middlename, String nickname, String primary_email, String bio, String date_of_birth, String gender){

        format = new SimpleDateFormat("yyyy-MM-dd");

        this.primary_email = primary_email;
        this.fname = firstname;
        this.lname = lastname;
        this.mname = middlename;
        this.nickname = nickname;
        this.bio = bio;
        this.dob = date_of_birth;
        this.gender = gender;
    }

    public String getPrimaryEmail() {
        return primary_email;
    }

    public void setPrimaryEmail(String primary_email) {
        this.primary_email = primary_email;
    }

    public String getFirstName() {
        return fname;
    }

    public void setFirstName(String fname) {
        this.fname = fname;
    }

    public String getLastName() {
        return lname;
    }

    public void setLastName(String lname) {
        this.lname = lname;
    }

    public String getMiddleName() {
        return mname;
    }

    public void setMiddleName(String mname) {
        this.mname = mname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDateOfBirth() {
        return dob;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dob = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
