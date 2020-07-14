package com.springvuegradle.endpoints;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.springvuegradle.exceptions.UserNotAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
import com.springvuegradle.model.requests.CreateActivityRequest;
import com.springvuegradle.model.responses.ActivityResponse;

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
                                              @RequestBody CreateActivityRequest updateActivityRequest,
                                              HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException, InvalidRequestFieldException, UserNotAuthorizedException {
        // check correct authentication
        Long authId = (Long) request.getAttribute("authenticatedid");

        Optional<User> editingUser = userRepository.findById(authId);

        if (authId == null || editingUser.isEmpty()) {
            throw new UserNotAuthenticatedException("user is not authenticated");
        }

        if (!(authId == profileId) && !(editingUser.get().getPermissionLevel() > ADMIN_USER_MINIMUM_PERMISSION)) {
            throw new UserNotAuthorizedException("you must be authenticated as the target user or an admin");
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
        if (updateActivityRequest.getActivityTypes() == null || updateActivityRequest.getActivityTypes().size() == 0) {
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
            activity.getActivityTypes().clear();
            
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
//                LocalDateTime startDateTime = parseDateString(updateActivityRequest.getStartTime());
//                LocalDateTime endDateTime = parseDateString(updateActivityRequest.getEndTime());

                activity.setIsDuration(true);
//                activity.setStartDate(startDateTime.toLocalDate());
//                activity.setEndDate(endDateTime.toLocalDate());
//
//                activity.setStartTime(startDateTime.toLocalTime());
//                activity.setEndTime(startDateTime.toLocalTime());

                activity.setStartTime(updateActivityRequest.getStartTime());
                activity.setEndTime(updateActivityRequest.getEndTime());
            } else {
            	activity.setIsDuration(false);
            }
            return new ActivityResponse(activityRepository.save(activity));
        }
    }

    /**
     * takes a string in ISO_OFFSET_DATE_TIME format and converts it to a LocalDateTime object.
     * @param dateTimeString the date time string to convert
     * @return a LocalDateTime object representing the given date time string
     * @throws InvalidRequestFieldException if the given date time string is not valid
     */
    public LocalDateTime parseDateString(String dateTimeString) throws InvalidRequestFieldException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidRequestFieldException("Time format is invalid");
        }
        return dateTime;
    }


    @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
    @CrossOrigin
    public ResponseEntity<Object> deleteActivity(@PathVariable("profileId") long profileId,
                                                 @PathVariable("activityId") long activityId,
                                                 HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException, UserNotAuthorizedException {
        //authenticate
        Long authId = (Long) request.getAttribute("authenticatedid");

        Optional<User> editingUser = userRepository.findById(authId);

        if (authId == null || editingUser.isEmpty()) {
            throw new UserNotAuthenticatedException("user is not authenticated");
        }

        if (!(authId == profileId) && !(editingUser.get().getPermissionLevel() > ADMIN_USER_MINIMUM_PERMISSION)) {
            throw new UserNotAuthorizedException("you must be authenticated as the target user or an admin");
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
                                   HttpServletRequest httpRequest) throws InvalidRequestFieldException, RecordNotFoundException, UserNotAuthenticatedException, UserNotAuthorizedException {
        // check correct authentication
        Long authId = (Long) httpRequest.getAttribute("authenticatedid");
        Optional<User> editingUser = userRepository.findById(authId);
        if (authId == null || editingUser.isEmpty()) {
            throw new UserNotAuthenticatedException("user is not authenticated");
        }

        if (!(authId == profileId) && !(editingUser.get().getPermissionLevel() > ADMIN_USER_MINIMUM_PERMISSION)) {
            throw new UserNotAuthorizedException("you must be authenticated as the target user or an admin");
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
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
//
//            LocalDateTime startDateTime, endDateTime;
//            try {
//                startDateTime = LocalDateTime.parse(createActivityRequest.getStartTime(), formatter);
//                endDateTime = LocalDateTime.parse(createActivityRequest.getEndTime(), formatter);
//            } catch (DateTimeParseException e) {
//                throw new InvalidRequestFieldException("invalid time string " + e.getParsedString());
//            }
//            LocalDateTime startDateTime = parseDateString(createActivityRequest.getStartTime());
//            LocalDateTime endDateTime = parseDateString(createActivityRequest.getEndTime());
//            startDate = startDateTime.toLocalDate();
//            endDate = endDateTime.toLocalDate();
//            startTime = startDateTime.toLocalTime();
//            endTime = endDateTime.toLocalTime();
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
                new HashSet<>(activityTypeList));
        activity.setDescription(createActivityRequest.getDescription());
//        activity.setStartDate(startDate);
//        activity.setEndDate(endDate);
//        activity.setStartTime(startTime);
//        activity.setEndTime(endTime);
        activity.setStartTime(createActivityRequest.getStartTime());
        activity.setEndTime(createActivityRequest.getEndTime());
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

    /**
     * returns the list of all activities created by a given creator
     * @param profileId path variable for the creator's id
     * @param request the http servlet request associated with this request
     * @return a list of activity types
     * @throws UserNotAuthenticatedException if the user is not authenticated
     * @throws RecordNotFoundException if the creator is not found
     */
    @GetMapping("/profiles/{profileId}/activities")
    @CrossOrigin
    public List<ActivityResponse> getActivitiesByCreator(@PathVariable("profileId") long profileId,
                                        HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException {
        //check auth
        Long authId = (Long) request.getAttribute("authenticatedid");
        if(authId == null){
            //not authenticated
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        Optional<Profile> optionalCreator = profileRepository.findById(profileId);
        if (optionalCreator.isEmpty()) {
            throw new RecordNotFoundException("target creator does not exist");
        }

        List<Activity> activities = activityRepository.findActivitiesByCreator(optionalCreator.get());
        List<ActivityResponse> responseActivities = new ArrayList<>();
        for (Activity activity : activities) {
            responseActivities.add(new ActivityResponse(activity));
        }

        return responseActivities;
    }

}
