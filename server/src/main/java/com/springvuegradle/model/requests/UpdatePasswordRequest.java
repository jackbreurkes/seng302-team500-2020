package com.springvuegradle.model.requests;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Class for update password requests
 * @author Michael Freeeman
 * @author Jack van Heugten Breurkes
 * @author Josh Yee
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdatePasswordRequest {

	@NotNull(message = "missing old password field")
    private String oldPassword;
    
	@NotNull(message = "missing new password field")
    private String newPassword;
    
	@NotNull(message = "missing repeat password field")
    private String repeatPassword;

    protected UpdatePasswordRequest() {}

    /**
     * @param oldPassword the user's old password
     * @param newPassword the new password to set
     * @param repeatPassword repeat of the newPassword field
     */
    public UpdatePasswordRequest(String oldPassword, String newPassword, String repeatPassword){
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.repeatPassword = repeatPassword;
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
