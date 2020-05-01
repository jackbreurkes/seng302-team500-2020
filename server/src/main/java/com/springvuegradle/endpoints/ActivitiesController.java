package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdateActivityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Controller for all endpoints relating to activities
 * @Author Michael Freeman
 */
@RestController
public class ActivitiesController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    private int ADMIN_USER_MINIMUM_PERMISSION = 120;

    @PutMapping("/profiles/{profileId}/activities/{activityId}")
    @CrossOrigin
    public ResponseEntity<Object> putActivity(@PathVariable("profileId") long profileId, @PathVariable("activityId") long activityId,
                                              @RequestBody UpdateActivityRequest updateActivityRequest,
                                              HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException
    {
        // check correct authentication
        Long authId = (Long) request.getAttribute("authenticatedid");

        Optional<User> editingUser = userRepository.findById(authId);

        if (authId == null || !(authId == profileId) && (editingUser.isPresent() && !(editingUser.get().getPermissionLevel() > ADMIN_USER_MINIMUM_PERMISSION))) {
            //here we check permission level and update the password accordingly
            //assuming failure without admin
            throw new UserNotAuthenticatedException("you must be authenticated as the target user or an admin");
        }
        //now user is either correct or an admin
        //then update fields
        Optional<Activity> activityToEdit = activityRepository.findById(activityId);

        if(!activityToEdit.isPresent()){
            throw new RecordNotFoundException("Activity does not exist");
        }else{
            Activity activity = activityToEdit.get();
            activity.setActivityName(updateActivityRequest.getActivityName());
            activity.setDescription(updateActivityRequest.getDescription());
            activity.setIsDuration(updateActivityRequest.isContinous());
            activity.setLocation(updateActivityRequest.getLocation());
            //need to do datetime, checking and activity types
        }
    }
}
