package com.springvuegradle.endpoints;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.*;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.ChangeLogRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.responses.HomeFeedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Endpoint for the GET /homefeed/{profile_id} request
 */
@RestController
@RequestMapping("/homefeed")
public class HomeFeedController {

    @Autowired
    ChangeLogRepository changeLogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ProfileRepository profileRepository;

    /**
     * Retrieve and respond to a request to get a user's home feed updates
     * @param profileId id of the user whose home feed it should return
     * @param httpRequest http request made
     * @return list of HomeFeedResponse objects which give appropriately formatted information about changes
     * @throws ForbiddenOperationException if trying to retrieve another user's home feed
     * @throws UserNotAuthorizedException if the user is not allowed to view this feed (i.e. they are a different user and not an admin)
     * @throws UserNotAuthenticatedException if there is no authentication information in the request
     * @throws InvalidRequestFieldException if one of the changes retrieved is for an unsupported entity type
     * @throws RecordNotFoundException if the user's profile cannot be found
     */
    @GetMapping("/{profileId}")
    @CrossOrigin
    public List<HomeFeedResponse> getUserHomeFeed(@PathVariable("profileId") long profileId,
                                                  HttpServletRequest httpRequest)
            throws ForbiddenOperationException, UserNotAuthorizedException, UserNotAuthenticatedException,
                InvalidRequestFieldException, RecordNotFoundException {

        Long authId = (Long) httpRequest.getAttribute("authenticatedid");
        UserAuthorizer.getInstance().checkIsAuthenticated(httpRequest);
        if (!authId.equals(profileId)) {
            throw new ForbiddenOperationException("cannot retrieve another user's home feed");
        }

        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()) {
            throw new RecordNotFoundException("user profile does not exist");
        }

        List<ChangeLog> changeLogList = changeLogRepository.retrieveUserHomeFeedUpdates(profile.get());
        List<HomeFeedResponse> homeFeedResponses = new ArrayList<>();
        for (ChangeLog change : changeLogList) {
            if (change.getEntity().equals(ChangeLogEntity.ACTIVITY)) {
                Activity activity = getActivityIfExists(change.getEntityId());
                Profile editingProfile = getProfileIfExists(change.getEditingUser().getUserId());
                HomeFeedResponse response = new HomeFeedResponse(change, activity, editingProfile);
                homeFeedResponses.add(response);
            } else {
                throw new InvalidRequestFieldException("changes to this type of entity are not supported in the home feed");
            }
        }
        return homeFeedResponses;
    }

    /**
     * Get the activity by its id if it exists
     * @param activityId id of the activity
     * @return Activity with the given id
     * @throws RecordNotFoundException if no activity with the id given exists
     */
    private Activity getActivityIfExists(Long activityId) throws RecordNotFoundException {
        Optional<Activity> optionalActivity = activityRepository.findById(activityId);
        if (optionalActivity.isEmpty()) {
            throw new RecordNotFoundException("activity in change log does not exist"); // Would cause issues if an activity could be deleted without deleting its change log entries
        }
        return optionalActivity.get();
    }

    /**
     * Get the profile with the given id if one exists
     * @param profileId the id of the profile to retrieve
     * @return the profile with the given id
     * @throws RecordNotFoundException if no profile exists
     */
    private Profile getProfileIfExists(Long profileId) throws RecordNotFoundException {
        Optional<Profile> optionalEditor = profileRepository.findById(profileId);
        if (optionalEditor.isEmpty()) {
            throw new RecordNotFoundException("editor profile does not exist"); //Cause similar issue as described above
        }
        return optionalEditor.get();
    }

}
