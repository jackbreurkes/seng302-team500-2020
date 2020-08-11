package com.springvuegradle.endpoints;

import com.springvuegradle.auth.AuthInterceptor;
import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityRole;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.data.UserActivityRole;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdateUserActivityRoleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

/**
 * Controller for all endpoints relating to the User Activity Roles
 */
@RestController
public class UserActivityRoleController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Endpoint for deleting a UserActivityRole entry from the database table
     * @param activityId the id of the activity that the role is associated with
     * @param profileId the id of the profile that the role is associated with
     * @param request  the request object associated with the request
     * @return a response
     * @throws UserNotAuthorizedException if the user does not have the right permissions
     * @throws UserNotAuthenticatedException if the user is not logged in
     * @throws RecordNotFoundException If there is no UserActivityRole related to the user specified
     */
    @DeleteMapping("/activities/{activityId}/roles/{profileId}")
    @CrossOrigin
    public ResponseEntity<Object> deleteUserActivityRole(@PathVariable("activityId") long activityId,
                                                         @PathVariable("profileId") long profileId,
                                                         HttpServletRequest request) throws UserNotAuthorizedException, UserNotAuthenticatedException, RecordNotFoundException {
        UserAuthorizer.getInstance().checkIsRoleAuthenticated(request, profileId, activityId, userRepository, userActivityRoleRepository, activityRepository);

        Optional <UserActivityRole> roleToDelete = userActivityRoleRepository.getRoleEntryByUserId(profileId, activityId);
        if(!roleToDelete.isPresent()){
            throw new RecordNotFoundException("This user currently does not have a role in this activity");
        }

        userActivityRoleRepository.delete(roleToDelete.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Updates the Activity Role for a specific user in a specific activity, if the user doesn't already have a role it creates a new one for that user/activity
     * @param activityId The id of the activity the user is a part of
     * @param profileId The id of the user participating in the activity
     * @param updateUserActivityRoleRequest RequestBody created from JSON parse
     * @param request HttpServletRequest object
     * @return ResponseEntity object
     * @throws UserNotAuthorizedException if the user does not have the right permissions
     * @throws UserNotAuthenticatedException if the user is not logged in
     * @throws RecordNotFoundException If there is no UserActivityRole related to the user specified
     */

    @PutMapping("/activities/{activityId}/roles/{profileId}")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    public void setUserActivityRole(@PathVariable("activityId") long activityId,
                                                         @PathVariable("profileId") long profileId,
                                                        @Valid @RequestBody UpdateUserActivityRoleRequest updateUserActivityRoleRequest,
                                                         HttpServletRequest request) throws UserNotAuthorizedException, UserNotAuthenticatedException, InvalidRequestFieldException, RecordNotFoundException {
        UserAuthorizer.getInstance().checkIsRoleAuthenticated(request, profileId, activityId, userRepository, userActivityRoleRepository, activityRepository);

        ActivityRole userRole = updateUserActivityRoleRequest.getRole();

        if (userActivityRoleRepository.getRoleEntryByUserId(profileId, activityId).isPresent()) {
            // Update the role of the existing user
            userActivityRoleRepository.updateUserActivityRole(updateUserActivityRoleRequest.getRole(), profileId, activityId);
        } else {
            // Create new entry for user
            Optional<Activity> activity = activityRepository.findById(activityId);
            if (!activity.isPresent()) {
                throw new RecordNotFoundException("Activity not found");
            }
            else {
                UserActivityRole userActivityRole = new UserActivityRole(activity.get(), (User) request.getAttribute("authenticateduser"), updateUserActivityRoleRequest.getRole());
                userActivityRoleRepository.save(userActivityRole);
            }

        }

    }
}
