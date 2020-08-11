package com.springvuegradle.auth;

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

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;
import java.util.NoSuchElementException;
import java.util.Optional;

public class UserAuthorizer {
    /**
     * Instance of the UserAuthorizer
     */
    private static UserAuthorizer authInstance = null;

    private final short ADMIN_USER_MINIMUM_PERMISSION = 120;
    private final short STD_ADMIN_USER_PERMISSION = 126;
    private final short SUPER_ADMIN_USER_PERMISSION = 127;

    /**
     * Private constructor for singlton pattern
     */
    private UserAuthorizer() { }

    /**
     * Get the instance of the singleton obejct
     * @return the singleton instance
     */
    public static UserAuthorizer getInstance(){
        if(authInstance == null){
            authInstance = new UserAuthorizer();
        }
        return authInstance;
    }

    /**
     * Checks if the user making the request is authorised to perform operation requested
     * @param request the HttpServletRequest for the operation
     * @param profileId the ID of the user to be authenticated
     * @return The user Id if the user is authenticated
     * @throws UserNotAuthenticatedException if the user is not authenticated
     */

    public long checkIsAuthenticated(HttpServletRequest request, Long profileId, UserRepository userRepository) throws UserNotAuthenticatedException, UserNotAuthorizedException {
        Long authId = (Long) request.getAttribute("authenticatedid");

        if(authId == null) {
            throw new UserNotAuthenticatedException("You are not authenticated");
        }

        if(authId.equals(profileId)){
            return authId;
        }
        User editingUser = userRepository.findById(authId).orElse(null);
        if (editingUser == null) {
            throw new UserNotAuthenticatedException("You are not authenticated");
        }

        if(editingUser.getPermissionLevel() < ADMIN_USER_MINIMUM_PERMISSION) {
            throw new UserNotAuthorizedException("you must be authenticated as the target user or an admin");
        }
        return authId;

    }

    /**
     * For requests to do with activities, this checks if the user is either an admin, organiser or creator and then allows
     * them to carry on.
     * @param request the HttpServletRequest for the operation
     * @param profileId the ID of the user being authenticated
     * @param activityId the ID of the activity the user is a part of
     * @return The user ID if the user is authenticated
     * @throws UserNotAuthenticatedException if the user isn't logged in
     * @throws UserNotAuthorizedException if the user does not have permission
     */
    public long checkIsRoleAuthenticated(HttpServletRequest request, Long profileId, Long activityId, UserRepository userRepository, UserActivityRoleRepository userActivityRoleRepository, ActivityRepository activityRepository) throws UserNotAuthenticatedException, UserNotAuthorizedException, RecordNotFoundException {
        Long authId = (Long) request.getAttribute("authenticatedid");
        if (authId == null) {
            throw new UserNotAuthenticatedException("You are not authenticated");
        }

        User editingUser = userRepository.findById(authId).orElse(null);
        if (editingUser == null) {
            throw new UserNotAuthenticatedException("You are not authenticated");
        }
        if (editingUser.getPermissionLevel() >= ADMIN_USER_MINIMUM_PERMISSION) { // Checks if the editing user is an admin
            return authId;
        }
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) {
            throw new RecordNotFoundException("Cannot find this activity");
        }
        Long creatorId = activity.getCreator().getUser().getUserId();
        if (creatorId.equals(authId)) { // Checks if the editing user is the Creator of the activity
            return authId;
        }
        UserActivityRole activityRole = userActivityRoleRepository.getRoleEntryByUserId(authId, activityId).orElse(null);
        if (activityRole == null) {
            throw new UserNotAuthorizedException("you must be authenticated as the target user or an admin");
        }
        if (!activityRole.getActivityRole().equals(ActivityRole.ORGANISER)) { // Checks if the editing user is an Organiser of the activity
            throw new UserNotAuthorizedException("you must be authenticated as the target user or an admin");
        }
        return authId; //if an organiser

    }

}
