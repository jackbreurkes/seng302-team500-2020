package com.springvuegradle.model.requests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateUserRequest {
    private String primary_email, fname, lname, mname, nickname, password, bio;
    private Date dateOfBirth;
    private String genderG;

    private SimpleDateFormat format;

    protected CreateUserRequest() {}

    //TODO Validate this and maybe fix up the input (i.e will probs pass a string for the DOB

    // Some attributes can be null, instead of creating a bunch of constructors just let the DB validate it
    public CreateUserRequest(String lastname, String firstname, String middlename, String nickname, String primary_email, String password, String bio, String date_of_birth, String gender){

        format = new SimpleDateFormat("yyyy-mm-dd");

        this.primary_email = primary_email;
        this.fname = firstname;
        this.lname = lastname;
        this.mname = middlename;
        this.nickname = nickname;
        this.password = password;
        this.bio = bio;
        try{
            this.dateOfBirth = format.parse(date_of_birth);
        } catch (ParseException e){
            this.dateOfBirth = null;
        }
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return genderG;
    }

    public void setGender(String gender) {
        this.genderG = gender;
    }
}
