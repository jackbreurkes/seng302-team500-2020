package com.springvuegradle.endpoints;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.Profile;
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
import com.springvuegradle.model.requests.CreateActivityRequest;
import com.springvuegradle.model.responses.ActivityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for all endpoints relating to activities
 * @author Michael Freeman
 * @author Jack van Heugten Breurkes
 * @author James Auman
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
    public ActivityResponse putActivity(@PathVariable("profileId") long profileId, @PathVariable("activityId") long activityId,
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

        //Validate request
        // validate request body
        if (updateActivityRequest.getActivityName() == null) {
            throw new InvalidRequestFieldException("missing activity_name field");
        }
        if (updateActivityRequest.getActivityName().length() < 4 || updateActivityRequest.getActivityName().length() > 30) {
            throw new InvalidRequestFieldException("activity_name must be between 4 and 30 characters inclusive");
        }
        if (updateActivityRequest.getActivityTypes() == null) {
            throw new InvalidRequestFieldException("missing activity_type field");
        }
        if (updateActivityRequest.getActivityTypes().size() == 0) {
            throw new InvalidRequestFieldException("must have at least one activity_type");
        }
        if (updateActivityRequest.getDescription() != null && updateActivityRequest.getDescription().length() < 8) {
            throw new InvalidRequestFieldException("activity description must be at least 8 characters");
        }
        if (updateActivityRequest.getLocation() == null) {
            throw new InvalidRequestFieldException("missing location field");
        }
        if(updateActivityRequest.isContinuous() != true && updateActivityRequest.isContinuous() != false){
            //checking if the continuous field is there
            throw new InvalidRequestFieldException("Missing continuous field");
        }


        if(!activityToEdit.isPresent()){
            throw new RecordNotFoundException("Activity does not exist");
        }else{
            Activity activity = activityToEdit.get();

            activity.setActivityName(updateActivityRequest.getActivityName());
            activity.setDescription(updateActivityRequest.getDescription());
            activity.setIsDuration(updateActivityRequest.isContinuous());
            activity.setLocation(updateActivityRequest.getLocation());
            for(String activityTypeString : updateActivityRequest.getActivityTypes()){
                Optional<ActivityType> activityType = activityTypeRepository.getActivityTypeByActivityTypeName(activityTypeString);
                if(!activityType.isPresent()){
                    //the activity type does not exist
                    throw new RecordNotFoundException("Activity type " + activityTypeString + " does not exist");
                }else{
                    //it does exist
                    activity.getActivityTypes().add(activityType.get());
                }
            }

            //check if it is continuous and if not then add date and time
            if(!updateActivityRequest.isContinuous()){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
                LocalDateTime startDateTime, endDateTime;
                try{
                    startDateTime = LocalDateTime.parse(updateActivityRequest.getStartTime(), formatter);
                    endDateTime = LocalDateTime.parse(updateActivityRequest.getEndTime(), formatter);
                }catch (Exception e){
                    throw new InvalidRequestFieldException("Time format is invalid");
                }

                activity.setStartDate(startDateTime.toLocalDate());
                activity.setEndDate(endDateTime.toLocalDate());

                activity.setStartTime(startDateTime.toLocalTime());
                activity.setEndTime(startDateTime.toLocalTime());
            }
            return new ActivityResponse(activityRepository.save(activity));
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

    /**
     * endpoint function for POST /profiles/{profileId}/activities
     * @param profileId the path variable to match to the creator's profile id
     * @param createActivityRequest request body information
     * @param httpRequest the HttpRequest object associated with this request
     * @return a response containing information about the newly created activity
     * @throws InvalidRequestFieldException if a request field is invalid
     * @throws RecordNotFoundException if a required object is not found in the database
     * @throws UserNotAuthenticatedException if the user is not correctly authenticated
     */
    @PostMapping("/profiles/{profileId}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public ActivityResponse createActivity(@PathVariable("profileId") long profileId,
            @RequestBody CreateActivityRequest createActivityRequest,
                                   HttpServletRequest httpRequest) throws InvalidRequestFieldException, RecordNotFoundException, UserNotAuthenticatedException {
        // check correct authentication
        Long authId = (Long) httpRequest.getAttribute("authenticatedid");
        Optional<User> editingUser = userRepository.findById(authId);
        if (!(authId == profileId) && (editingUser.isPresent() && !(editingUser.get().getPermissionLevel() > ADMIN_USER_MINIMUM_PERMISSION))) {
            throw new UserNotAuthenticatedException("you must be authenticated as the target user or an admin");
        }

        // validate request body
        if (createActivityRequest.getActivityName() == null) {
            throw new InvalidRequestFieldException("missing activity_name field");
        }
        if (createActivityRequest.getActivityName().length() < 4 || createActivityRequest.getActivityName().length() > 30) {
            throw new InvalidRequestFieldException("activity_name must be between 4 and 30 characters inclusive");
        }
        if (createActivityRequest.getActivityTypes() == null) {
            throw new InvalidRequestFieldException("missing activity_type field");
        }
        if (createActivityRequest.getActivityTypes().size() == 0) {
            throw new InvalidRequestFieldException("must have at least one activity_type");
        }
        if (createActivityRequest.isContinuous() == null) {
            throw new InvalidRequestFieldException("missing continuous field");
        }
        if (createActivityRequest.getDescription() != null && createActivityRequest.getDescription().length() < 8) {
            throw new InvalidRequestFieldException("activity description must be at least 8 characters");
        }
        if (createActivityRequest.getLocation() == null) {
            throw new InvalidRequestFieldException("missing location field");
        }

        List<ActivityType> activityTypeList = new ArrayList<>();
        for (String activityType : createActivityRequest.getActivityTypes()) {
            Optional<ActivityType> optionalActivityType = activityTypeRepository.getActivityTypeByActivityTypeName(activityType);
            if (optionalActivityType.isEmpty()) {
                throw new RecordNotFoundException("no activity type called " + activityType + " found");
            }
            activityTypeList.add(optionalActivityType.get());
        }

        LocalDate startDate = null;
        LocalDate endDate = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        if (!createActivityRequest.isContinuous()) {
            if (createActivityRequest.getStartTime() == null || createActivityRequest.getEndTime() == null) {
                throw new InvalidRequestFieldException("duration activities must have start_time and end_time values");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

            LocalDateTime startDateTime, endDateTime;
            try {
                startDateTime = LocalDateTime.parse(createActivityRequest.getStartTime(), formatter);
                endDateTime = LocalDateTime.parse(createActivityRequest.getEndTime(), formatter);
            } catch (DateTimeParseException e) {
                throw new InvalidRequestFieldException("invalid time string " + e.getParsedString());
            }
            startDate = startDateTime.toLocalDate();
            endDate = endDateTime.toLocalDate();
            startTime = startDateTime.toLocalTime();
            endTime = endDateTime.toLocalTime();
        }

        Optional<Profile> optionalCreator = profileRepository.findById(profileId);
        if (optionalCreator.isEmpty()) {
            throw new RecordNotFoundException("profile with id " + profileId + " not found");
        }

        // save activity
        Activity activity = new Activity(
                createActivityRequest.getActivityName(),
                !createActivityRequest.isContinuous(),
                createActivityRequest.getLocation(),
                optionalCreator.get(),
                activityTypeList);
        activity.setDescription(createActivityRequest.getDescription());
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setStartTime(startTime);
        activity.setEndTime(endTime);
        activity.setLocation(createActivityRequest.getLocation());

        return new ActivityResponse(activityRepository.save(activity));
    }

    @GetMapping("/profiles/{profileId}/activities/{activityId}")
    @CrossOrigin
    public ActivityResponse getActivity(@PathVariable("profileId") long profileId, @PathVariable("activityId") long activityId,
                                        HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException {
        //No body needed as it is just a get
        //check auth
        Long authId = (Long) request.getAttribute("authenticatedid");
        if(authId == null){
            //not authenticated
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        Optional<Activity> optionalActivity = activityRepository.findById(activityId);
        if(!optionalActivity.isPresent()){
            throw new RecordNotFoundException("Activity doesnt exist");
        }

        return new ActivityResponse(optionalActivity.get());
    }

}