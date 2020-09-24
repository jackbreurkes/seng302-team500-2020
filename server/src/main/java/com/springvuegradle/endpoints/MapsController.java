package com.springvuegradle.endpoints;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.responses.ActivityPinResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     * endpoint for returning all of the activities within the given bounds that have not already finished
     * @param request request from an authenticated user containing the parameters
     *              ne_lat, ne_lon, sw_lat, sw_lon
     * @return the pins within the requested bounds
     * @throws InvalidRequestFieldException if a field is missing or not parseable as a lat/lon value
     * @throws UserNotAuthenticatedException if the user is not authorized
     * @throws RecordNotFoundException if the user is not found
     */
    @GetMapping
    @CrossOrigin
    public List<ActivityPinResponse> getActivityPinsWithinBounds(HttpServletRequest request)
            throws InvalidRequestFieldException, UserNotAuthenticatedException, RecordNotFoundException {

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

        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User not found"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        List<ActivityPinResponse> responses = new ArrayList<>();

        Set<Long> recommendedActivityIds = activityRepository
                .findRecommendedActivitiesByProfile(profile)
                .stream()
                .map(Activity::getId)
                .collect(Collectors.toSet());

        for(ActivityPin pin : pinsWithinBounds){
            if (!pin.getActivity().isDuration() || LocalDateTime.parse(pin.getActivity().getEndTime(), formatter).isAfter(LocalDateTime.now())) {
                boolean isRecommended = recommendedActivityIds.contains(pin.getActivity().getId());
                String userRole = this.getActivityRoleString(userId, pin.getActivity());
                responses.add(new ActivityPinResponse(pin, userRole, isRecommended));
            }
        }
        return responses;
    }

    /**
     * Float value for maximum distance that recommended activities
     * will be shown from users location, in degrees
     */
    private static float BOUNDING_BOX_SIZE = 0.2f;
    
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
