package com.springvuegradle.endpoints;

import java.util.Optional;

import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.HomefeedEntityType;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.Subscription;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.responses.ActivitySubscribedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springvuegradle.model.repository.ActivityRepository;
import com.springvuegradle.model.repository.SubscriptionRepository;
import com.springvuegradle.model.repository.ProfileRepository;

import javax.servlet.http.HttpServletRequest;


/**
 * REST controller for subscription status
 *
 */
@RestController
public class SubscriptionController {

    /**
     * Repository of activity in the database
     */
    @Autowired
    private ActivityRepository activityRepo;

    /**
     * Repositoy of subscription in the database
     */
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    /**
     * Repository of profiles in the database
     */
    @Autowired
    private ProfileRepository profileRepository;

    /**
     * Repository of the users in the database
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * REST Endpoint for GET /profiles/{profileId}/subscriptions/activities/{activityId}
     * Returns a boolean of whether user is subscribed
     *
     * @return Boolean of subscription status
     * @throws UserNotAuthenticatedException
     * @throws RecordNotFoundException
     */
    @GetMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    @CrossOrigin
    public ResponseEntity<String> getIsSubscribed(@PathVariable("profileId") long profileId, @PathVariable("activityId") long activityId,
                                                  HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException {
        Long authId = (Long) request.getAttribute("authenticatedid");
        if(authId == null){
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        Optional<Activity> optionalActivity = activityRepo.findById(activityId);
        if(!optionalActivity.isPresent()){
            throw new RecordNotFoundException("Activity doesn't exist");
        }
        Optional<Profile> optionalUser = profileRepository.findById(authId);
        if (optionalUser.isEmpty()) {
            throw new RecordNotFoundException("target user does not exist");
        }

        boolean isSubscribed = subscriptionRepository.isSubscribedToActivity(optionalActivity.get().getId(), optionalUser.get());

        return ResponseEntity.status(HttpStatus.OK).body("{\"subscribed\": "+isSubscribed+"}");
    }

    /**
     * Method for posting to subscribe to an activity
     * @param profileId profile id of subscribing user
     * @param activityId activity id of activity to subscribe to
     * @param request the request object
     * @return HTTPS status 201 created
     * @throws UserNotAuthenticatedException
     * @throws RecordNotFoundException
     * @throws UserNotAuthorizedException
     */
    @PostMapping("/profiles/{profileId}/subscriptions/activities/{activityId}")
    @CrossOrigin
    public ResponseEntity<String> postSubscribed(@PathVariable("profileId") long profileId, @PathVariable("activityId") long activityId, HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException, UserNotAuthorizedException {

        Optional<Activity> optionalActivity = activityRepo.findById(activityId);
        if(!optionalActivity.isPresent()){
            throw new RecordNotFoundException("Activity doesn't exist");
        }

        UserAuthorizer.getInstance().checkIsAuthenticated(request, profileId, userRepository);

        subscriptionRepository.save(new Subscription(profileRepository.getOne(profileId), HomefeedEntityType.ACTIVITY, activityId));
        return ResponseEntity.status(HttpStatus.CREATED).body("{\"subscribed\": true}");
    }

}
