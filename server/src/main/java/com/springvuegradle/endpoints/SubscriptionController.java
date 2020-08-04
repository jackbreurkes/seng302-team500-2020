package com.springvuegradle.endpoints;

import java.util.Optional;

import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Activity;
import com.springvuegradle.model.data.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
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
            throw new RecordNotFoundException("Activity doesnt exist");
        }
        Optional<Profile> optionalUser = profileRepository.findById(authId);
        if (optionalUser.isEmpty()) {
            throw new RecordNotFoundException("target user does not exist");
        }

        boolean isSubscribed = subscriptionRepository.isSubscribedToActivity(optionalActivity.get().getId(), optionalUser.get());

        return ResponseEntity.status(HttpStatus.OK).body("{\"subscribed\": "+isSubscribed+"}");
    }

}
