package com.springvuegradle.endpoints;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.*;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.requests.ActivityOutcomeRequest;
import com.springvuegradle.model.requests.CreateActivityRequest;
import com.springvuegradle.model.requests.RecordActivityResultsRequest;
import com.springvuegradle.model.requests.RecordOneActivityResultsRequest;
import com.springvuegradle.model.responses.ActivityResponse;
import com.springvuegradle.model.responses.ParticipantResultResponse;
import com.springvuegradle.util.FormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private ActivityOutcomeRepository activityOutcomeRepository;
    
    @Autowired
    private ActivityParticipantResultRepository activityParticipantResultRepository;



    private int ADMIN_USER_MINIMUM_PERMISSION = 120;


    @PutMapping("/profiles/{profileId}/activities/{activityId}")
    @CrossOrigin
    public ActivityResponse putActivity(@PathVariable("profileId") long profileId, @PathVariable("activityId") long activityId,
                                        @Valid @RequestBody CreateActivityRequest updateActivityRequest,
                                        Errors errors,
                                        HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException, InvalidRequestFieldException, UserNotAuthorizedException, ForbiddenOperationException {
        if (errors.hasErrors()) {
            String errorMessage = errors.getAllErrors().get(0).getDefaultMessage();
            throw new InvalidRequestFieldException(errorMessage);
        }

        long authId = UserAuthorizer.getInstance().checkIsRoleAuthenticated(request, profileId, activityId, userRepository, userActivityRoleRepository, activityRepository);

        User editingUser = userRepository.findById(authId).orElse(null);
        if (editingUser == null) {
            throw new RecordNotFoundException("user not found"); // shouldn't happen as this is already checked by auth handler
        }


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
        List<ActivityOutcome> outcomesToDelete = new ArrayList<>();
        for (ActivityOutcome outcome : activity.getOutcomes()) {
            String description = outcome.getDescription();
            boolean shouldKeep = descriptionUnits.containsKey(description) && outcome.getUnits().equals(descriptionUnits.get(description));
            descriptionUnits.remove(description); // discard outcome from dictionary as it doesn't need to be added
            if (shouldKeep) {
                outcomesToKeep.add(outcome);
            } else {
                outcomesToDelete.add(outcome);
            }
        }

        List<ChangeLog> deleteOutcomeChanges = new ArrayList<>();
        for (ActivityOutcome outcome : outcomesToDelete) {
            int loggedResultsCount = activityParticipantResultRepository.countActivityParticipantResultByOutcomeOutcomeId(outcome.getOutcomeId());
            if (loggedResultsCount > 0) {
                throw new ForbiddenOperationException("cannot delete outcome \"" + outcome.getDescription() + "\" as participants have logged results against it");
            }
            ChangeLog deleteOutcomeChangeLog = ActivityChangeLog.getLogForDeleteOutcome(activityId, outcome, editingUser);
            deleteOutcomeChanges.add(deleteOutcomeChangeLog);
        }
        changeLogRepository.saveAll(deleteOutcomeChanges);

        activity.getOutcomes().clear();
        activity.getOutcomes().addAll(outcomesToKeep);

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


    @DeleteMapping("/profiles/{profileId}/activities/{activityId}")
    @CrossOrigin
    public ResponseEntity<Object> deleteActivity(@PathVariable("profileId") long profileId,
                                                 @PathVariable("activityId") long activityId,
                                                 HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException, UserNotAuthorizedException {

        long authId = UserAuthorizer.getInstance().checkIsTargetUserOrAdmin(request, profileId, userRepository);

        Optional<User> editingUser = userRepository.findById(authId);
        assert editingUser.isPresent(); // checked by UserAuthorizer

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
        ActivityResponse response = getSingleActivity(activityId, request);
        Profile profile = profileRepository.findById(profileId).orElse(null);
        if (profile == null) {
            throw new RecordNotFoundException("profile " + profileId + " not found");
        }
        return response;
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
     * Putmapping for updating a participant result
     * @param activityId the activity the result is associated to
     * @param updateActivityResultRequest the new information body
     * @param errors
     * @param request
     * @throws InvalidRequestFieldException if the json is malformed
     * @throws RecordNotFoundException if the result cannot be found
     * @throws UserNotAuthenticatedException if the user is not authenticated
     */
    @PutMapping("/activities/{activityId}/results")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    public ParticipantResultResponse updateActivityResult(@PathVariable("activityId") long activityId,
                                     @Valid @RequestBody RecordOneActivityResultsRequest updateActivityResultRequest,
                                     Errors errors,
                                     HttpServletRequest request) throws InvalidRequestFieldException,
            RecordNotFoundException, UserNotAuthenticatedException {
        if (errors.hasErrors()) {
            String errorMessage = errors.getAllErrors().get(0).getDefaultMessage();
            throw new InvalidRequestFieldException(errorMessage);
        }
        if (!FormValidator.validateTimestamp(updateActivityResultRequest.getCompletedDate())) {
            throw new InvalidRequestFieldException("could not parse timestamp " + updateActivityResultRequest.getCompletedDate());
        }

        long authId = UserAuthorizer.getInstance().checkIsAuthenticated(request);
        activityRepository.findById(activityId).orElseThrow(() -> new RecordNotFoundException("activity " + activityId + " not found"));

        ActivityParticipantResult result = activityParticipantResultRepository.getParticipantResult(authId, updateActivityResultRequest.getOutcomeId()).orElse(null);

        if (result == null){
            throw new RecordNotFoundException("Result does not exist");
        }
        result.setValue(updateActivityResultRequest.getResult());
        result.setCompletedDate(updateActivityResultRequest.getCompletedDate());
        result = activityParticipantResultRepository.save(result);

        //Add a changelog entry
        Profile profile = profileRepository.findById(authId).orElseThrow(() -> new RecordNotFoundException("profile " + authId + " not found"));
        ChangeLog createParticipantResultChangeLog = ActivityChangeLog.getLogForCreateParticipantResult(profile, result);
        changeLogRepository.save(createParticipantResultChangeLog);

        return new ParticipantResultResponse(result);
    }

    /**
     * endpoint function for POST /profiles/{profileId}/activities
     * @param activityId Activity ID the results are for
     * @param createResultsRequest request body information
     * @param errors Anything that went wrong when parsing the body would appear here
     * @param request the HttpRequest object associated with this request
     * @throws InvalidRequestFieldException if a request field is invalid
     * @throws RecordNotFoundException if a required object is not found in the database
     * @throws UserNotAuthenticatedException if the user is not correctly authenticated
     */
    @PostMapping("/activities/{activityId}/results")
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public List<ParticipantResultResponse> createActivityResult(@PathVariable("activityId") long activityId,
    		@Valid @RequestBody RecordActivityResultsRequest createResultsRequest,
    		Errors errors,
    		HttpServletRequest request) throws InvalidRequestFieldException, RecordNotFoundException, UserNotAuthenticatedException, UserNotAuthorizedException {

		if (errors.hasErrors()) {
			String errorMessage = errors.getAllErrors().get(0).getDefaultMessage();
			throw new InvalidRequestFieldException(errorMessage);
		}

		long authId = UserAuthorizer.getInstance().checkIsAuthenticated(request);

		Map<Long, RecordOneActivityResultsRequest> outcomeIds = new HashMap<Long, RecordOneActivityResultsRequest>();
		for (RecordOneActivityResultsRequest outcomeObject : createResultsRequest.getOutcomes()) {
			if (outcomeIds.containsKey(outcomeObject.getOutcomeId())) {
				throw new InvalidRequestFieldException("Duplicate outcome ID");
			}
            if (!FormValidator.validateTimestamp(outcomeObject.getCompletedDate())) {
                throw new InvalidRequestFieldException("could not parse timestamp " + outcomeObject.getCompletedDate());
            }
			outcomeIds.put(outcomeObject.getOutcomeId(), outcomeObject);
		}

		Profile profile = profileRepository.findById(authId).orElse(null);

		if (profile == null) {
			throw new RecordNotFoundException("Profile with id "+authId+" not found");
		}
		
		List<ActivityOutcome> outcomeList = this.activityOutcomeRepository.getOutcomesById(new ArrayList<Long>(outcomeIds.keySet()));
		
		if (outcomeList.size() != outcomeIds.size()) {
			throw new RecordNotFoundException("One or more activity outcome id does not exist");
		}
		
		Optional<Activity> optionalActivity = activityRepository.findById(activityId);
		if (optionalActivity.isEmpty()) {
			throw new RecordNotFoundException("The activity specified does not exist");
		}
		
		Activity activity = optionalActivity.get();
		
		for (ActivityOutcome outcome : outcomeList) {
			if (outcome.getActivity().getId() != activity.getId()) {
				throw new InvalidRequestFieldException("One or more outcome ID is not for the requested activity");
			}
		}
		List<ActivityParticipantResult> results = new ArrayList<>();
		for (ActivityOutcome outcome : outcomeList) {
			RecordOneActivityResultsRequest userResult = outcomeIds.get(outcome.getOutcomeId());
			
			String value = userResult.getResult();
			String completedDate = userResult.getCompletedDate();
			
			Optional<ActivityParticipantResult> potentialResult = activityParticipantResultRepository.getParticipantResult(authId, outcome.getOutcomeId());
			ActivityParticipantResult result;
			if (potentialResult.isPresent()) {
				result = potentialResult.get();
				result.setValue(value);
				result.setCompletedDate(completedDate);
			} else {
				result = new ActivityParticipantResult(profile.getUser(), outcome, value, completedDate);
			}
            activityParticipantResultRepository.save(result);
            results.add(result);

            //Add a changelog entry
            ChangeLog createParticipantResultChangeLog = ActivityChangeLog.getLogForCreateParticipantResult(profile, result);
            changeLogRepository.save(createParticipantResultChangeLog);
		}
		return results.stream().map(ParticipantResultResponse::new).collect(Collectors.toList());
    }

    /**
     * returns a list of outcomes a user has associated with a particular activity.
     * @param activityId the activity the desired results are associated with
     * @return a list of response mapper objects representing the desired outcomes
     * @throws RecordNotFoundException if the activity or the profile requested does not exist
     */
    @GetMapping("/activities/{activityId}/results")
    @CrossOrigin
    public List<ParticipantResultResponse> getActivityResults(@PathVariable("activityId") long activityId,
                                                              HttpServletRequest request) throws UserNotAuthenticatedException, UserNotAuthorizedException, RecordNotFoundException {
        long authId = UserAuthorizer.getInstance().checkIsAuthenticated(request);

        activityRepository.findById(activityId).orElseThrow(() -> new RecordNotFoundException("activity " + activityId + " not found"));

        List<ActivityParticipantResult> results = activityParticipantResultRepository.getParticipantResultsByUserIdAndActivityId(authId, activityId);
        return results.stream().map(ParticipantResultResponse::new).collect(Collectors.toList());
    }


    /**
     * Delete the activityResult that the user has entered. We only allow the user that set the result
     * to delete this, creators/admin/organisers will not be able to see these results and therefore cannot delete the result
     * @param activityId the Id of the activity that the result is associated to
     * @param request the HttpServelet request
     * @throws UserNotAuthorizedException thrown if the user is not logged in
     * @throws UserNotAuthenticatedException thrown if the user does not have permissions to delete this
     * @throws RecordNotFoundException thrown if the result is not found.
     */
    @DeleteMapping("/activities/{activityId}/results/{outcomeId}")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    public void deleteActivityResult(@PathVariable("activityId") long activityId,
                                                         @PathVariable("outcomeId") long outcomeId,
                                                         HttpServletRequest request) throws UserNotAuthorizedException, UserNotAuthenticatedException, RecordNotFoundException {
        long authId = UserAuthorizer.getInstance().checkIsAuthenticated(request);

        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) {
            throw new RecordNotFoundException("activity " + activityId + " not found");
        }
        ActivityOutcome outcome = activityOutcomeRepository.findById(outcomeId).orElse(null);
        if (outcome == null) {
            throw new RecordNotFoundException("outcome " + outcomeId + " not found");
        }
        if (outcome.getActivity().getId() != activityId) {
            throw new RecordNotFoundException("no outcome with id " + outcomeId + " found for activity " + activityId);
        }


        ActivityParticipantResult participantResult = activityParticipantResultRepository.getParticipantResult(authId, outcomeId).orElse(null);
        if (participantResult == null) {
            throw new RecordNotFoundException("Cannot find your result");
        }
        activityParticipantResultRepository.delete(participantResult);
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
