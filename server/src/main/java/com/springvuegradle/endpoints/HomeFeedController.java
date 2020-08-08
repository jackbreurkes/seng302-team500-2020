package com.springvuegradle.endpoints;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.ForbiddenOperationException;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.data.ChangeLog;
import com.springvuegradle.model.data.User;
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
     * @throws UserNotAuthorizedException
     * @throws UserNotAuthenticatedException
     * @throws InvalidRequestFieldException
     */
    @GetMapping("/{profileId}")
    @CrossOrigin
    public List<HomeFeedResponse> getUserHomeFeed(@PathVariable("profileId") long profileId, HttpServletRequest httpRequest)
            throws ForbiddenOperationException, UserNotAuthorizedException, UserNotAuthenticatedException, InvalidRequestFieldException {

        Long authId = (Long) httpRequest.getAttribute("authenticatedid");
        UserAuthorizer.getInstance().checkIsAuthenticated(httpRequest, profileId, userRepository);
        if (!authId.equals(profileId)) {
            throw new ForbiddenOperationException("cannot retrieve another user's home feed");
        }

        List<ChangeLog> changeLogList = changeLogRepository.retrieveUserHomeFeedUpdates(profileRepository.getOne(profileId));
        List<HomeFeedResponse> homeFeedResponses = new ArrayList<>();
        for (ChangeLog change : changeLogList) {
            HomeFeedResponse response = new HomeFeedResponse(change, activityRepository, profileRepository);
            homeFeedResponses.add(response);
        }
        return homeFeedResponses;
    }


    }
