package com.springvuegradle.endpoints;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.springvuegradle.model.data.*;
import com.springvuegradle.model.requests.ActivityOutcomeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.ChangeLogRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.SubscriptionRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
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

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    private ChangeLogRepository changeLogRepository;

    private int ADMIN_USER_MINIMUM_PERMISSION = 120;


    @PutMapping("/profiles/{profileId}/activities/{activityId}")
    @CrossOrigin
    public ActivityResponse putActivity(@PathVariable("profileId") long profileId, @PathVariable("activityId") long activityId,
                                        @Valid @RequestBody CreateActivityRequest updateActivityRequest,
                                        Errors errors,
                                        HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException, InvalidRequestFieldException, UserNotAuthorizedException {
        if (errors.hasErrors()) {
            String errorMessage = errors.getAllErrors().get(0).getDefaultMessage();
            throw new InvalidRequestFieldException(errorMessage);
        }

    	Long authId = (Long) request.getAttribute("authenticatedid");

        User editingUser = userRepository.findById(authId).orElse(null);
        if (editingUser == null) {
            throw new RecordNotFoundException("user not found"); // shouldn't happen as this is already checked by auth handler
        }

        UserAuthorizer.getInstance().checkIsTargetUserOrAdmin(request, profileId, userRepository);

        Optional<Activity> activityToEdit = activityRepository.findById(activityId);

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
        if(!updateActivityRequest.isContinuous() && updateActivityRequest.isContinuous()){
            throw new InvalidRequestFieldException("Missing continuous field");
        }

        if(activityToEdit.isEmpty()){
            throw new RecordNotFoundException("Activity does not exist");
        }

        Set<ActivityType> activityTypesToAdd = new HashSet<>();
        for(String activityTypeString : updateActivityRequest.getActivityTypes()){
            Optional<ActivityType> activityType = activityTypeRepository.getActivityTypeByActivityTypeName(activityTypeString);
            if(activityType.isEmpty()){
                throw new RecordNotFoundException("Activity type " + activityTypeString + " does not exist");
            }else{
                activityTypesToAdd.add(activityType.get());
            }
        }


        Activity activity = activityToEdit.get();
        for (ChangeLog change : ActivityChangeLog.getLogsForUpdateActivity(activity, updateActivityRequest, editingUser)) {
            changeLogRepository.save(change);
        }

        activity.setActivityName(updateActivityRequest.getActivityName());
        activity.setDescription(updateActivityRequest.getDescription());
        activity.setIsDuration(updateActivityRequest.isContinuous());
        activity.setLocation(updateActivityRequest.getLocation());
        activity.getActivityTypes().clear();
        activity.getActivityTypes().addAll(activityTypesToAdd);

        Map<String, String> descriptionUnits = new LinkedHashMap<>(); // LinkedHashMap maintains insertion order
        for (ActivityOutcomeRequest outcome : updateActivityRequest.getOutcomes()) {
            if (descriptionUnits.containsKey(outcome.getDescription())) {
                throw new InvalidRequestFieldException("an activity cannot have two outcomes with the same description");
            }
            descriptionUnits.put(outcome.getDescription(), outcome.getUnits());
        }

        List<ActivityOutcome> outcomesToKeep = new ArrayList<>();
        List<ActivityOutcome> deletedOutcomes = new ArrayList<>();
        for (ActivityOutcome outcome : activity.getOutcomes()) {
            String description = outcome.getDescription();
            boolean shouldKeep = descriptionUnits.containsKey(description) && outcome.getUnits().equals(descriptionUnits.get(description));
            descriptionUnits.remove(description); // discard outcome from dictionary as it doesn't need to be added
            if (shouldKeep) {
                outcomesToKeep.add(outcome);
            } else {
                deletedOutcomes.add(outcome);
            }
        }

        for (ActivityOutcome outcome : deletedOutcomes) {
            // TODO when participant results are implemented, an error should be thrown if an outcome with results logged against it is overridden
            ChangeLog deleteOutcomeChangeLog = ActivityChangeLog.getLogForDeleteOutcome(activityId, outcome, editingUser);
            changeLogRepository.save(deleteOutcomeChangeLog);
        }
        activity.setOutcomes(outcomesToKeep);

        for (String key : descriptionUnits.keySet()) { // iterate over the new outcomes
            String description = key;
            String units = descriptionUnits.get(key);
            ActivityOutcome newOutcome = new ActivityOutcome(description, units);
            activity.addOutcome(newOutcome);
            ChangeLog createOutcomeChangeLog = ActivityChangeLog.getLogForCreateOutcome(activityId, newOutcome, editingUser);
            changeLogRepository.save(createOutcomeChangeLog);
        }

        if(!updateActivityRequest.isContinuous()){
            activity.setIsDuration(true);

            activity.setStartTime(updateActivityRequest.getStartTime());
            activity.setEndTime(updateActivityRequest.getEndTime());
        } else {
            activity.setIsDuration(false);
        }
        return new ActivityResponse(activityRepository.save(activity), getActivityFollowerCount(activity), getActivityParticipantCount(activity));

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

        Long authId = (Long) request.getAttribute("authenticatedid");

        Optional<User> editingUser = userRepository.findById(authId);

        UserAuthorizer.getInstance().checkIsTargetUserOrAdmin(request, profileId, userRepository);

        Optional<Activity> activityToDelete = activityRepository.findById(activityId);

        if(activityToDelete.isEmpty()){
            throw new RecordNotFoundException("Activity Does not exist");
        }

        activityRepository.delete(activityToDelete.get());
        changeLogRepository.save(ActivityChangeLog.getLogForDeleteActivity(activityToDelete.get(), editingUser.get()));
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
                                           @Valid @RequestBody CreateActivityRequest createActivityRequest,
                                           Errors errors,
                                           HttpServletRequest httpRequest) throws InvalidRequestFieldException, RecordNotFoundException, UserNotAuthenticatedException, UserNotAuthorizedException {

        if (errors.hasErrors()) {
            String errorMessage = errors.getAllErrors().get(0).getDefaultMessage();
            throw new InvalidRequestFieldException(errorMessage);
        }

        Long authId = (Long) httpRequest.getAttribute("authenticatedid");

        UserAuthorizer.getInstance().checkIsTargetUserOrAdmin(httpRequest, profileId, userRepository);

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

        if (!createActivityRequest.isContinuous()) {
            if (createActivityRequest.getStartTime() == null || createActivityRequest.getEndTime() == null) {
                throw new InvalidRequestFieldException("duration activities must have start_time and end_time values");
            }
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
        activity.setStartTime(createActivityRequest.getStartTime());
        activity.setEndTime(createActivityRequest.getEndTime());
        activity.setLocation(createActivityRequest.getLocation());
        for (ActivityOutcomeRequest outcome : createActivityRequest.getOutcomes()) {
            activity.addOutcome(new ActivityOutcome(outcome.getDescription(), outcome.getUnits()));
        }
        activity = activityRepository.save(activity);
        changeLogRepository.save(ActivityChangeLog.getLogForCreateActivity(activity));
        return new ActivityResponse(activity, 1L, 1L);
    }

    @GetMapping("/profiles/{profileId}/activities/{activityId}")
    @CrossOrigin
    @Deprecated
    public ActivityResponse getActivity(@PathVariable("profileId") long profileId, @PathVariable("activityId") long activityId,
                                        HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException {
        return getSingleActivity(activityId, request);
    }

    /**
     * Returns the information for a single activity as identified by its ID only
     * @param activityId id of the activity to return
     * @param request http request made
     * @return json body giving information about the activity
     * @throws UserNotAuthenticatedException if no authentication information is provided
     * @throws RecordNotFoundException if the activity does not exist
     */
    @GetMapping("/activities/{activityId}")
    @CrossOrigin
    public ActivityResponse getSingleActivity(@PathVariable("activityId") long activityId,
                                              HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException {

        UserAuthorizer.getInstance().checkIsAuthenticated(request);

        Optional<Activity> optionalActivity = activityRepository.findById(activityId);
        if(!optionalActivity.isPresent()){
            throw new RecordNotFoundException("Activity doesn't exist");
        }

        Activity activity = optionalActivity.get();

        return new ActivityResponse(activity, getActivityFollowerCount(activity), getActivityParticipantCount(activity));
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

        UserAuthorizer.getInstance().checkIsAuthenticated(request);

        Optional<Profile> optionalCreator = profileRepository.findById(profileId);
        if (optionalCreator.isEmpty()) {
            throw new RecordNotFoundException("target creator does not exist");
        }

        List<Activity> activities = activityRepository.findActivitiesByCreator(optionalCreator.get());
        List<ActivityResponse> responseActivities = new ArrayList<>();
        for (Activity activity : activities) {
            responseActivities.add(new ActivityResponse(activity, getActivityFollowerCount(activity), getActivityParticipantCount(activity)));
        }

        return responseActivities;
    }

    /**
     * Gets the amount of users following the given activity
     * @param activity Activity to get the follower count of
     */
    private Long getActivityFollowerCount(Activity activity) {
    	return subscriptionRepository.getFollowerCount(activity.getId());
    }

    /**
     * Gets the amount of users participating in the given activity
     * @param activity Activity to get the participant count of
     */
    private Long getActivityParticipantCount(Activity activity) {
    	return this.userActivityRoleRepository.getParticipantCountByActivityId(activity.getId());
    }

}
