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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

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
     * @param timestamp the timestamp to search for changelog entries from before. should be in ISO8601 format without final colon
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
                                                  @RequestParam(required = false) String timestamp,
                                                  HttpServletRequest httpRequest)
            throws UserNotAuthorizedException, UserNotAuthenticatedException,
                InvalidRequestFieldException, RecordNotFoundException {

        UserAuthorizer.getInstance().checkIsTargetUser(httpRequest, profileId);

        Profile profile = profileRepository.findById(profileId).orElse(null);
        if (profile == null) {
            throw new RecordNotFoundException("user profile does not exist");
        }

        List<ChangeLog> changeLogList;
        if (timestamp == null) {
            changeLogList = changeLogRepository.retrieveUserHomeFeedUpdates(profile);
        } else {
            ZonedDateTime latestTimestamp;
            try {
                TemporalAccessor timestampAccessor = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ").parse(timestamp);
                latestTimestamp = ZonedDateTime.from(timestampAccessor);
            } catch (DateTimeParseException e) {
                throw new InvalidRequestFieldException("timestamp " + timestamp + " could not be parsed");
            }
            changeLogList = changeLogRepository.retrieveUserHomeFeedUpdatesBeforeTime(profile, latestTimestamp);
        }
        return getHomeFeedResponsesFromChanges(changeLogList);
    }

    /**
     * returns a list of all the home feed responses that should be generated from a list of changes.
     * @param changes the list of ChangeLogs to generate HomeFeedReseponses for
     * @return a list of HomeFeedResponses suited to the given changes
     */
    List<HomeFeedResponse> getHomeFeedResponsesFromChanges(List<ChangeLog> changes) {
        Set<Long> activityIds = changes.stream()
                .filter(changeLog -> changeLog.getEntity() == ChangeLogEntity.ACTIVITY)
                .map(ChangeLog::getEntityId).collect(Collectors.toSet());
        HashMap<Long, Activity> activities = new HashMap<>();
        for (long activityId : activityIds) {
            activities.put(activityId, activityRepository.findById(activityId).orElse(null));
        }

        List<HomeFeedResponse> homeFeedResponses = new ArrayList<>();
        for (ChangeLog change : changes) {
            if (change.getEntity().equals(ChangeLogEntity.ACTIVITY)) {
                HomeFeedResponse response = getActivityChangeLogResponse(change, activities);
                if (response != null) {
                    homeFeedResponses.add(response);
                }
            }
            // do nothing if change has an unsupported entity type
        }
        return homeFeedResponses;
    }

    /**
     * returns a suitable HomeFeedResponse for an activity changelog
     * @param change a changelog with an entity type of Activity
     * @param activities a hashmap mapping changelog entity IDs to activities, or null objects if the activities are deleted
     * @return a suitable HomeFeedResponse, or null if no home feed entry should be generated for this change
     */
    HomeFeedResponse getActivityChangeLogResponse(ChangeLog change, HashMap<Long, Activity> activities) {
        Activity activity = activities.get(change.getEntityId());
        Profile editor = profileRepository.findById(change.getEditingUser().getUserId()).orElse(null);
        if (activity == null) {
            boolean isDeleteChangeLog = change.getActionType() == ActionType.DELETED
                    && change.getChangedAttribute() == ChangedAttribute.ACTIVITY_EXISTENCE;
            return isDeleteChangeLog ? new HomeFeedResponse(change, change.getOldValue(), editor) : null;
        }

        // activity still exists
        return new HomeFeedResponse(change, activity, editor);
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
