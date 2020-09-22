package com.springvuegradle.endpoints;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.responses.HomeFeedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.ActivityPin;
import com.springvuegradle.model.data.ActivityRole;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.UserActivityRole;
import com.springvuegradle.model.responses.ActivityPinResponse;

/**
 * Controller for maps related endpoints.
 */
@RestController
@RequestMapping("/maps")
public class MapsController {

    private static final int MAX_LONGITUDE = 180;
    private static final int MIN_LONGITUDE = -180;


    @Autowired
    ActivityPinRepository activityPinRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    ProfileRepository profileRepository;

    /**
     * endpoint for returning all of the activities within the given bounds.
     * @param request request from an authenticated user containing the parameters
     *              ne_lat, ne_lon, sw_lat, sw_lon
     * @return the pins within the requested bounds
     * @throws InvalidRequestFieldException if a field is missing or not parseable as a lat/lon value
     * @throws UserNotAuthenticatedException if the user is not authorized
     */
    @GetMapping
    @CrossOrigin
    public List<ActivityPinResponse> getActivityPinsWithinBounds(HttpServletRequest request) throws InvalidRequestFieldException, UserNotAuthenticatedException, RecordNotFoundException {

        long userId = UserAuthorizer.getInstance().checkIsAuthenticated(request);

        final Pageable pageable = PageRequest.of(0, 50); // number of pins to return per area

        // The following Strings will simply be null if the associated parameter is not specified
        float northeastLatitude = getNumberFromParameter(request, "ne_lat");
        float northeastLongitude = getNumberFromParameter(request, "ne_lon");
        float southwestLatitude = getNumberFromParameter(request, "sw_lat");
        float southwestLongitude = getNumberFromParameter(request, "sw_lon");

        List<ActivityPin> pinsWithinBounds = new ArrayList<>();
        if (northeastLongitude < southwestLongitude) { // bounding box crosses the longitudinal boundary line
            pinsWithinBounds.addAll(
                    activityPinRepository.findPinsInBounds(northeastLatitude, MAX_LONGITUDE, southwestLatitude, southwestLongitude, pageable)
            );
            pinsWithinBounds.addAll(
                    activityPinRepository.findPinsInBounds(northeastLatitude, northeastLongitude, southwestLatitude, MIN_LONGITUDE, pageable)
            );
        } else {
            pinsWithinBounds.addAll(
                    activityPinRepository.findPinsInBounds(northeastLatitude, northeastLongitude, southwestLatitude, southwestLongitude, pageable)
            );
        }

        Optional<Profile> optionalProfile = profileRepository.findById(userId);
        if (optionalProfile.isEmpty()) {
            throw new RecordNotFoundException("User not found");
        }
        Profile profile = optionalProfile.get();


        List<ActivityPinResponse> responses = new ArrayList<>();
        for(ActivityPin pin : pinsWithinBounds){
            boolean isRecommended = false;
            Activity activity = pin.getActivity();
            UserActivityRole role = userActivityRoleRepository.getRoleEntryByUserId(profile.getUser().getUserId(), activity.getId()).orElse(null);

            if(role == null && profile.getActivityTypes() != null && activity.getActivityTypes() != null &&
                    profile.getActivityTypes().stream().filter(activity.getActivityTypes()::contains).collect(Collectors.toList()).size() > 0 &&
                    !subscriptionRepository.isSubscribedToActivity(activity.getId(), profile)){
                isRecommended = true;
            }
            String userRole = this.getActivityRoleString(userId, pin.getActivity());
            responses.add(new ActivityPinResponse(pin, userRole, isRecommended));
        }
        return responses;
    }

    /**
     * Float value for maximum distance that recommended activities
     * will be shown from users location, in degrees
     */
    private static float BOUNDING_BOX_SIZE = 0.2f;

    /**
     * Finds recommended activities for a user based on profile location and interested activity types
     * @param profile profile of the user to recommend activities to
     * @param activityPinsInBox list of the activities to filter to get those currently recommended
     * @return List of candidate recommended activities
     */
    public List<Activity> findRecommendedActivities(Profile profile, List<ActivityPin> activityPinsInBox){
        //Get the activities within the range of the users profile location

        List<Activity> activityList = activityPinsInBox.stream().map(object -> object.getActivity()).collect(Collectors.toList());
        List<Activity> candidateActivities = new ArrayList<Activity>();
        for(Activity activity : activityList){
            UserActivityRole role = userActivityRoleRepository.getRoleEntryByUserId(profile.getUser().getUserId(), activity.getId()).orElse(null);
            if(role == null && profile.getActivityTypes().stream().filter(activity.getActivityTypes()::contains).collect(Collectors.toList()).size() > 0 && !subscriptionRepository.isSubscribedToActivity(activity.getId(), profile)){
                candidateActivities.add(activity);
            }
        }
        return candidateActivities;
    }
    
    /**
     * Gets the activity role string for the given activity and user
     * @param userId user ID to get the role of
     * @param activity activity we are getting the user's role within
     * @return Output can be any of: "creator", "organiser", "participant", "follower" or null
     */
    private String getActivityRoleString(long userId, Activity activity) {
    	final long activityId = activity.getId();
    	String userRole = null;
    	boolean isCreator = activity.getCreator().getUser().getUserId() == userId;
        if (isCreator) {
            userRole = "creator";
        } else {
            UserActivityRole role = userActivityRoleRepository.getRoleEntryByUserId(userId, activityId).orElse(null);
            boolean isOrganiser = role != null && role.getActivityRole().equals(ActivityRole.ORGANISER);
            boolean isParticipant = role != null && role.getActivityRole().equals(ActivityRole.PARTICIPANT);
            if (isOrganiser) {
                userRole = "organiser";
            } else if (isParticipant) {
                userRole = "participant";
            } else {
                Profile profile = profileRepository.findById(userId).orElse(null);
                boolean isFollower = profile != null && subscriptionRepository.isSubscribedToActivity(activityId, profile);
                if (isFollower) {
                    userRole = "follower";
                }
            }
        }
        return userRole;
    }

    /**
     * resolves a request parameter to a float.
     * @param request the request the parameter is expected to be contained in
     * @param parameter the parameter to resolve
     * @return the parameter value as a float
     * @throws InvalidRequestFieldException if the parameter is not included or cannot be parsed as a float
     */
    private float getNumberFromParameter(HttpServletRequest request, String parameter) throws InvalidRequestFieldException {
        String parameterValue = request.getParameter(parameter);
        if (parameterValue == null) {
            throw new InvalidRequestFieldException(String.format("the request parameter %s must be included", parameter));
        }

        try {
            return Float.parseFloat(parameterValue);
        } catch (NumberFormatException e) {
            throw new InvalidRequestFieldException(String.format("the request parameter %s must be a valid number", parameter));
        }
    }

}
