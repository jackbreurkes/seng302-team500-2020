package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdateActivityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private ActivityTypeRepository activityTypeRepository;

    private int ADMIN_USER_MINIMUM_PERMISSION = 120;

    @PutMapping("/profiles/{profileId}/activities/{activityId}")
    @CrossOrigin
    public ResponseEntity<Object> putActivity(@PathVariable("profileId") long profileId, @PathVariable("activityId") long activityId,
                                              @RequestBody UpdateActivityRequest updateActivityRequest,
                                              HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException, InvalidRequestFieldException {
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
            try{
                activity.setActivityName(updateActivityRequest.getActivityName());
                activity.setDescription(updateActivityRequest.getDescription());
                activity.setIsDuration(updateActivityRequest.isContinous());
                activity.setLocation(updateActivityRequest.getLocation());

                for(String activityType : updateActivityRequest.getActivityTypes()){
                    if(activityTypeRepository.getActivityTypeByActivityTypeName(activityType).isPresent()){
                        //todo
                    }
                }

                //check if it is continuous and if not then add date and time
                if(!updateActivityRequest.isContinous()){
                    activity.setStartDate(updateActivityRequest.getStartTime().toLocalDate());
                    activity.setEndDate(updateActivityRequest.getEndTime().toLocalDate());

                    activity.setStartTime(updateActivityRequest.getStartTime().toLocalTime());
                    activity.setEndTime(updateActivityRequest.getEndTime().toLocalTime());
                }
            }catch (Exception e){
                throw new InvalidRequestFieldException("Invalid request");
            }
            //need to do datetime, checking and activity types

            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }


    @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
    @CrossOrigin
    public ResponseEntity<Object> deleteActivity(@PathVariable("profileId") long profileId,
                                                 @PathVariable("activityId") long activityId,
                                                 HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException {
        //authenticate
        Long authId = (Long) request.getAttribute("authenticatedid");

        Optional<User> editingUser = userRepository.findById(authId);

        if (authId == null || !(authId == profileId) && (editingUser.isPresent() && !(editingUser.get().getPermissionLevel() > ADMIN_USER_MINIMUM_PERMISSION))) {
            //here we check permission level and update the password accordingly
            //assuming failure without admin
            throw new UserNotAuthenticatedException("you must be authenticated as the target user or an admin");
        }

        Optional<Activity> activityToDelete = activityRepository.findById(activityId);
        //check if activity exists
        if(!activityToDelete.isPresent()){
            throw new RecordNotFoundException("Activity Does not exist");
        }

        activityRepository.delete(activityToDelete.get());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
