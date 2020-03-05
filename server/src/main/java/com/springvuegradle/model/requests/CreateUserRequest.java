package com.springvuegradle.model.requests;

import com.springvuegradle.model.data.Gender;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CreateUserRequest {
    private String email, fname, lname, mname, nickname, password, bio;
    private Date dateOfBirth;
    private String genderG;

    private SimpleDateFormat format;

    protected CreateUserRequest() {}

    //TODO Validate this and maybe fix up the input (i.e will probs pass a string for the DOB

    // Some attributes can be null, instead of creating a bunch of constructors just let the DB validate it
    public CreateUserRequest(String lastname, String firstname, String middlename, String nickname, String email, String password, String bio, String date_of_birth, String gender){

        format = new SimpleDateFormat("yyyy-mm-dd");

        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
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
