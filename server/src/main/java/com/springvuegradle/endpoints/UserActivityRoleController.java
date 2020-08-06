package com.springvuegradle.endpoints;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.data.ActivityRole;
import com.springvuegradle.model.data.UserActivityRole;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
        UserAuthorizer.getInstance().checkIsAuthenticated(request, profileId, userRepository);

        Optional <UserActivityRole> roleToDelete = userActivityRoleRepository.getRoleEntryByUserId(profileId, activityId);
        if(!roleToDelete.isPresent()){
            throw new RecordNotFoundException("This user currently does not have a role in this activity");
        }

        userActivityRoleRepository.delete(roleToDelete.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
