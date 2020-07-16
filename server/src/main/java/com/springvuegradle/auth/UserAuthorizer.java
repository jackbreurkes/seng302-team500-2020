package com.springvuegradle.auth;

import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
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
     * @param profileId the ID of the data being changed
     * @return The user Id if the user is authenticated
     * @throws UserNotAuthenticatedException if the user is not authenticated
     */

    public long checkIsAuthenticated(HttpServletRequest request, Long profileId, UserRepository userRepository) throws UserNotAuthenticatedException {
        Long authId = (Long) request.getAttribute("authenticatedid");
        if(authId != null){
            Optional<User> editingUser = userRepository.findById(authId);
            if(authId.equals(profileId)){

                return authId;
            }else{
                if(editingUser.get().getPermissionLevel() >= ADMIN_USER_MINIMUM_PERMISSION){
                    return authId;
                }else{
                    throw new UserNotAuthenticatedException("you must be authenticated as the target user or an admin");
                }
            }
        }else{
            throw new UserNotAuthenticatedException("You are not authenticated");
        }
    }


}