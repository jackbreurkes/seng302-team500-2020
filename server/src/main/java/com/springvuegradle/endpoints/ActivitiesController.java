package com.springvuegradle.endpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import com.springvuegradle.exceptions.ForbiddenOperationException;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityChangeLog;
import com.springvuegradle.model.data.ActivityOutcome;
import com.springvuegradle.model.data.ActivityParticipantResult;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.data.ChangeLog;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.data.UserActivityRole;
import com.springvuegradle.model.repository.ActivityOutcomeRepository;
import com.springvuegradle.model.repository.ActivityParticipantResultRepository;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.ChangeLogRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.SubscriptionRepository;
import com.springvuegradle.model.repository.UserActivityRoleRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.ActivityOutcomeRequest;
import com.springvuegradle.model.requests.CreateActivityRequest;
import com.springvuegradle.model.requests.RecordActivityResultsRequest;
import com.springvuegradle.model.requests.RecordOneActivityResultsRequest;
import com.springvuegradle.model.responses.ActivityResponse;
import com.springvuegradle.model.responses.ParticipantResultResponse;
import com.springvuegradle.model.responses.UserActivityRoleResponse;
import com.springvuegradle.util.FormValidator;

/**
 * Controller for all endpoints relating to activities
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

        validateActivityRequest(updateActivityRequest);

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

        for (Map.Entry<String, String> entrySet : descriptionUnits.entrySet()) { // iterate over the new outcomes
            String description = entrySet.getKey();
            String units = entrySet.getValue();
            ActivityOutcome newOutcome = new ActivityOutcome(description, units);
            activity.addOutcome(newOutcome);
            ChangeLog createOutcomeChangeLog = ActivityChangeLog.getLogForCreateOutcome(activityId, newOutcome, editingUser);
            changeLogRepository.save(createOutcomeChangeLog);
        }

        if(Boolean.FALSE.equals(updateActivityRequest.isContinuous())){
            activity.setIsDuration(true);

            activity.setStartTime(updateActivityRequest.getStartTime());
            activity.setEndTime(updateActivityRequest.getEndTime());
        } else {
            activity.setIsDuration(false);
        }
        return new ActivityResponse(activityRepository.save(activity), getActivityFollowerCount(activity), getActivityParticipantCount(activity));

    }
    
    /**
     * Validates create/edit activity requests by throwing an InvalidRequestFieldException when the request is invalid
     * @param request Request body to validate
     * @throws InvalidRequestFieldException When the request is invalid. Exception contains a message as to why it failed
     */
    private void validateActivityRequest(CreateActivityRequest request) throws InvalidRequestFieldException {
        if (request.getActivityName() == null) {
            throw new InvalidRequestFieldException("missing activity_name field");
        }
        if (request.getActivityName().length() < 4 || request.getActivityName().length() > 30) {
            throw new InvalidRequestFieldException("activity_name must be between 4 and 30 characters inclusive");
        }
        if (request.getActivityTypes() == null) {
            throw new InvalidRequestFieldException("missing activity_type field");
        }
        if (request.getActivityTypes().isEmpty()) {
            throw new InvalidRequestFieldException("must have at least one activity_type");
        }
        if (request.isContinuous() == null) {
            throw new InvalidRequestFieldException("missing continuous field");
        }
        if (request.getDescription() != null && request.getDescription().length() < 8) {
            throw new InvalidRequestFieldException("activity description must be at least 8 characters");
        }
        if (request.getLocation() == null) {
            throw new InvalidRequestFieldException("missing location field");
        }
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

        validateActivityRequest(createActivityRequest);

        List<ActivityType> activityTypeList = new ArrayList<>();
        for (String activityType : createActivityRequest.getActivityTypes()) {
            Optional<ActivityType> optionalActivityType = activityTypeRepository.getActivityTypeByActivityTypeName(activityType);
            if (optionalActivityType.isEmpty()) {
                throw new RecordNotFoundException("no activity type called " + activityType + " found");
            }
            activityTypeList.add(optionalActivityType.get());
        }

        if (Boolean.FALSE.equals(createActivityRequest.isContinuous()) && (createActivityRequest.getStartTime() == null || createActivityRequest.getEndTime() == null)) {
        	throw new InvalidRequestFieldException("duration activities must have start_time and end_time values");
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

    /**
     * Get an activity with the profile ID of the creator and the activity ID
     * @param profileId Owner of the activity requested
     * @param activityId Activity ID being requested
     * @param request http request made
     * @return Response of the activity that was requested
     * @throws UserNotAuthenticatedException if no authentication information is provided
     * @throws RecordNotFoundException if the activity does not exist
     */
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
        if (profile.getUser().getUserId() != profileId) {
        	throw new RecordNotFoundException("activity id "+activityId+" does not belong to profile id "+profileId);
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
            RecordNotFoundException, UserNotAuthenticatedException, UserNotAuthorizedException {
        if (errors.hasErrors()) {
            String errorMessage = errors.getAllErrors().get(0).getDefaultMessage();
            throw new InvalidRequestFieldException(errorMessage);
        }
        if (!FormValidator.validateTimestamp(updateActivityResultRequest.getCompletedDate())) {
            throw new InvalidRequestFieldException("could not parse timestamp " + updateActivityResultRequest.getCompletedDate());
        }

        long authId = UserAuthorizer.getInstance().checkIsAuthenticated(request);
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new RecordNotFoundException("activity " + activityId + " not found"));
        boolean hasRole = userActivityRoleRepository.getRoleEntryByUserId(authId, activityId).isPresent();
        boolean isCreator = activity.getCreator().getUser().getUserId() == authId;
        if (!hasRole && !isCreator) {
            throw new UserNotAuthorizedException("must be a participant, organiser or creator of the activity to log results");
        }

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

        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new RecordNotFoundException("The activity specified does not exist"));
		boolean hasRole = userActivityRoleRepository.getRoleEntryByUserId(authId, activityId).isPresent();
		boolean isCreator = activity.getCreator().getUser().getUserId() == authId;
		if (!hasRole && !isCreator) {
		    throw new UserNotAuthorizedException("must be a participant, organiser or creator of the activity to log results");
        }

		Map<Long, RecordOneActivityResultsRequest> outcomeIds = new HashMap<>();
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

        if (!activityRepository.existsById(activityId)) {
        	throw new RecordNotFoundException("activity " + activityId + " not found");
        }

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


    /**
     * Gets all participants/organisers of a particular activity
     * @param activityId The activity ID
     * @param request HTTPServletRequest corresponding to the user's request
     * @return an HTTP ResponseEntity with the HTTP response containing all participants/organisers a part of an activity
     * @throws UserNotAuthenticatedException If the user is not authenticated
     * @throws RecordNotFoundException If the activity ID does not exist
     */
    @GetMapping("/activities/{activityId}/involved")
    @CrossOrigin
    public List<UserActivityRoleResponse> getProfilesInvolvedWithActivity(@PathVariable("activityId") long activityId,
                                                         HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException {
        UserAuthorizer.getInstance().checkIsAuthenticated(request);

        Activity activity = null;
        Profile creator = null;
        try {
        	activity = activityRepository.getOne(activityId);
        	if (activity == null) {
        		throw new EntityNotFoundException();
        	}
        	creator = activity.getCreator();
        } catch (EntityNotFoundException e) {
        	throw new RecordNotFoundException("Given activity ID does not exist");
        }
        
        List<User> users = userActivityRoleRepository.getInvolvedUsersByActivityId(activityId); // The result list of participants/organisers

        List<UserActivityRoleResponse> responses = new ArrayList<>();
        responses.add(new UserActivityRoleResponse(creator, "Creator"));
        
        for (User user : users) {
        	if (user.getUserId() != creator.getUser().getUserId()) {
        		Optional<Profile> profile = profileRepository.findById(user.getUserId());
        		if (profile.isEmpty()) continue; //should only happen if the superadmin somehow follows an activity
        		UserActivityRole role = userActivityRoleRepository.getRoleEntryByUserId(user.getUserId(), activityId).get();
        		responses.add(new UserActivityRoleResponse(profile.get(), role.getActivityRole().getFriendlyName()));
        	}
        }

        return responses;
    }

}
