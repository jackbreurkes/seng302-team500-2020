package com.springvuegradle.model.requests;

/**
 * Class for update password requests
 * @Author Michael Freeeman
 */
public class UpdatePasswordRequest {

    private long profile_id;
    private String oldPassword, newPassword, repeatPassword;

    protected UpdatePasswordRequest() {}

    public UpdatePasswordRequest(long profile_id, String old_password, String new_password, String repeat_password){
        this.profile_id = profile_id;
        this.oldPassword = old_password;
        this.newPassword = new_password;
        this.repeatPassword = repeat_password;
    }

    public long getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(long profile_id) {
        this.profile_id = profile_id;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
