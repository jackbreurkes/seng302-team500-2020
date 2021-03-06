package com.springvuegradle.model.requests;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateUserRequest {
    private String primary_email, fname, lname, mname, nickname, password, bio;
    private String dob;
    private String genderG;

    private SimpleDateFormat format;

    protected CreateUserRequest() {}

    // Some attributes can be null, instead of creating a bunch of constructors just let the DB validate it
    public CreateUserRequest(String lastname, String firstname, String middlename, String nickname, String primary_email, String password, String bio, String date_of_birth, String gender){

        format = new SimpleDateFormat("yyyy-MM-dd");

        this.primary_email = primary_email;
        this.fname = firstname;
        this.lname = lastname;
        this.mname = middlename;
        this.nickname = nickname;
        this.password = password;
        this.bio = bio;
        this.dob = date_of_birth;
        this.genderG = gender;
    }

    public String getPrimaryEmail() {
        return primary_email;
    }

    public void setPrimaryEmail(String primary_email) {
        this.primary_email = primary_email;
    }

    public String getFirstname() {
        return fname;
    }

    public void setFirstname(String fname) {
        this.fname = fname;
    }

    public String getLastname() {
        return lname;
    }

    public void setLastname(String lname) {
        this.lname = lname;
    }

    public String getMiddlename() {
        return mname;
    }

    public void setMiddlename(String mname) {
        this.mname = mname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        return genderG;
    }

    public void setGender(String gender) {
        this.genderG = gender;
    }
}
