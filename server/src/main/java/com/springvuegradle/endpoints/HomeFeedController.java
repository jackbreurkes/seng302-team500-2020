package com.springvuegradle.endpoints;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.*;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.responses.HomeFeedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Endpoint for the GET /homefeed/{profile_id} request
 */
@RestController
@RequestMapping("/homefeed")
public class HomeFeedController {

    @Autowired
    ChangeLogRepository changeLogRepository;

    @Autowired
    ActivityPinRepository activityPinRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserActivityRoleRepository userActivityRoleRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    /**
     * Retrieve and respond to a request to get a user's home feed updates
     * @param profileId id of the user whose home feed it should return
     * @param lastId the ID of the last changelog ID the frontend received in the last query (used for pagination)
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
                                                  @RequestParam(name = "lastId", required = false) String lastId,
                                                  HttpServletRequest httpRequest)
            throws UserNotAuthorizedException, UserNotAuthenticatedException,
                InvalidRequestFieldException, RecordNotFoundException {

        UserAuthorizer.getInstance().checkIsTargetUser(httpRequest, profileId);
        Long paginatedChangeId = null;
        try {
            if (lastId != null) {
                paginatedChangeId = Long.parseLong(lastId);
            }
        } catch (NumberFormatException e) {
            throw new InvalidRequestFieldException(lastId + " is not a valid changelog id");
        }

        Profile profile = profileRepository.findById(profileId).orElse(null);
        if (profile == null) {
            throw new RecordNotFoundException("user profile does not exist");
        }

        List<ChangeLog> changeLogList;
        final Pageable pageable = PageRequest.of(0, 15); // return 15 results
        if (paginatedChangeId == null) {
            changeLogList = changeLogRepository.retrieveUserHomeFeedUpdates(profile, pageable);
        } else {

            ChangeLog lastChangeReceived = changeLogRepository.findById(paginatedChangeId).orElse(null);
            if (lastChangeReceived == null) {
                // this should never happen since changelogs shouldn't be deleted, and definitely not between pagination requests
                throw new NullPointerException("error: a changelog has been deleted while paginating. please reload the homefeed page");
            }
            OffsetDateTime latestTimestamp = lastChangeReceived.getTimestamp();
            changeLogList = changeLogRepository.retrieveUserHomeFeedUpdatesUpToTime(profile, latestTimestamp, pageable);

            final List<ChangeLog> finalChangeLogList = changeLogList; // required for use in filter
            final Long finalPaginatedChangeId = paginatedChangeId; // required for use in filter
            int paginateChangeListIndex = IntStream.range(0, changeLogList.size())
                    .filter(i -> finalChangeLogList.get(i).getChangeId() == finalPaginatedChangeId)
                    .findFirst().orElse(-1);

            changeLogList = changeLogList.subList(paginateChangeListIndex + 1, changeLogList.size());
        }
        return getHomeFeedResponsesFromChanges(changeLogList);
    }

    /**
     * Get a list of recommended activities to display on the user's home feed.
     * @param profileId id of the profile to retrieve recommendations for
     * @param httpRequest http request made
     * @return list of HomeFeedResponse objects which represent the recommended activities for the user
     * @throws UserNotAuthorizedException if the user is not allowed to view this feed (i.e. they are a different user and not an admin)
     * @throws UserNotAuthenticatedException if there is no authentication information in the request
     * @throws RecordNotFoundException
     */
    @GetMapping("/{profileId}/recommendations")
    @CrossOrigin
    public List<HomeFeedResponse> getActivitySuggestions(@PathVariable("profileId") long profileId, HttpServletRequest httpRequest)
            throws UserNotAuthorizedException, UserNotAuthenticatedException, RecordNotFoundException {

        UserAuthorizer.getInstance().checkIsTargetUser(httpRequest, profileId);

        Profile profile = profileRepository.findById(profileId).orElse(null);
        if (profile == null) {
            throw new RecordNotFoundException("user profile does not exist");
        }

        List<HomeFeedResponse> recommendationResponses = new ArrayList<>();

        if (profile.getLocation() != null &&    // Only recommend activities if the user has a location to get activities around
                profile.getLocation().getLatitude() != null && profile.getLocation().getLongitude() != null) {

            for (Activity activity : activityRepository.findRecommendedActivitiesByProfile(profile)) {
                recommendationResponses.add(new HomeFeedResponse(activity,
                        subscriptionRepository.getFollowerCount(activity.getId()),
                        ChangedAttribute.RECOMMENDED_ACTIVITY));
            }
        }

        return recommendationResponses;
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
        Profile editor = null;
        if (change.getEditingUser() != null) {
            editor = profileRepository.findById(change.getEditingUser().getUserId()).orElse(null);
        }
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
     * @deprecated since sprint 6
     */
    @Deprecated(since="sprint 6")
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
     * @deprecated since sprint 6
     */
    @Deprecated(since="sprint 6")
    private Profile getProfileIfExists(Long profileId) throws RecordNotFoundException {
        Optional<Profile> optionalEditor = profileRepository.findById(profileId);
        if (optionalEditor.isEmpty()) {
            throw new RecordNotFoundException("editor profile does not exist"); //Cause similar issue as described above
        }
        return optionalEditor.get();
    }

}
