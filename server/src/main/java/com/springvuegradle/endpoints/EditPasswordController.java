package com.springvuegradle.endpoints;

import com.springvuegradle.auth.ChecksumUtils;
import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.ForbiddenOperationException;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdatePasswordRequest;
import com.springvuegradle.util.FormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
public class EditPasswordController {

    int ADMIN_USER_MINIMUM_PERMISSION = 120;

    @Autowired
    private UserRepository userRepository;

    /**
     * endpoint for updating a user's password
     * @param profileId PathVariable id of the user whose password should be changed
     * @param updatePasswordRequest RequestBody UpdatePasswordRequest created from JSON parse
     * @param request HttpServletRequest object
     * @return ResponseEntity object with 201 Created status if password changed successfully
     * @throws InvalidRequestFieldException if a request field is invalid
     * @throws RecordNotFoundException if no user is found from the auth id
     * @throws ForbiddenOperationException if the old_password field is incorrect
     * @throws NoSuchAlgorithmException If SHA-256 doesn't exist in your version of java
     * @throws UserNotAuthenticatedException if the user is not authenticated as the target user or an admin
     */
    @PutMapping("/profiles/{profileId}/password")
    @CrossOrigin
    public ResponseEntity<Object> editPassword(
            @PathVariable("profileId") long profileId,
            @RequestBody UpdatePasswordRequest updatePasswordRequest,
            HttpServletRequest request) throws InvalidRequestFieldException, RecordNotFoundException, ForbiddenOperationException, NoSuchAlgorithmException, UserNotAuthenticatedException, UserNotAuthorizedException {
        // check correct authentication
        Long authId = (Long) request.getAttribute("authenticatedid");

        Optional<User> editingUser = userRepository.findById(authId);

        UserAuthorizer.getInstance().checkIsAuthenticated(request, profileId, userRepository);

        // check for missing fields
        if (updatePasswordRequest.getNewPassword() == null) {
            throw new InvalidRequestFieldException("no new_password field found");
        }

        if (updatePasswordRequest.getRepeatPassword() == null) {
            throw new InvalidRequestFieldException("no repeat_password field found");
        }

        // check if new password is a valid password
        if (!FormValidator.validatePassword(updatePasswordRequest.getNewPassword())) {
            throw new InvalidRequestFieldException("new_password should be at least 8 characters");
        }

        // check if new passwords do not match
        if (!updatePasswordRequest.getNewPassword().equals(updatePasswordRequest.getRepeatPassword())){
            throw new InvalidRequestFieldException("new_password and repeat_password are not the same");
        }

        // checks if target user exists
        Optional<User> optionalUser = userRepository.findById(profileId);
        if (optionalUser.isEmpty()) {
            throw new RecordNotFoundException("profile not found");
        }

        //get the user if they exist
        User user = optionalUser.get();

        //all completed checks apply to both admin user and normal user

        if(!(editingUser.get().getPermissionLevel() > ADMIN_USER_MINIMUM_PERMISSION)){
            //admin can edit
            //assuming user does not have the correct permission levels but is editing their own profile
            if (updatePasswordRequest.getOldPassword() == null) {
                throw new InvalidRequestFieldException("no old_password field found");
            }

            // checks if old_password matches user's current password
            if(!ChecksumUtils.checkPassword(user, updatePasswordRequest.getOldPassword())){
                throw new ForbiddenOperationException("old_password does not match user's current password");
            }
        }

        // updates the user's password
        user.setPassword(updatePasswordRequest.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
