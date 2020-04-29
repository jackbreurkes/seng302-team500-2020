package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.ActivityType;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.requests.PutActivityTypesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.requests.ProfileObjectMapper;
import com.springvuegradle.model.responses.ErrorResponse;
import com.springvuegradle.model.responses.ProfileCreatedResponse;
import com.springvuegradle.model.responses.ProfileResponse;


/**
 * Endpoint for the PUT /profiles/{profile_id} request
 * @author Jack van Heugten Breurkes
 */
@RestController
@RequestMapping("/profiles")
public class UserProfileController {

    /**
     * Repository (database) of user credentials
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository (database) of email addresses
     */
    @Autowired
    private EmailRepository emailRepository;

    /**
     * Repository (database) of profiles
     */
    @Autowired
    private ProfileRepository profileRepository;

    /**
     * Repository (database) of countries
     */
    @Autowired
    private CountryRepository countryRepository;

    /**
     * Repository used for accessing activity types
     */
    @Autowired
    private ActivityTypeRepository activityTypeRepository;


    /**
     * handle when user tries to PUT /profiles/{profile_id}
     */
    @PutMapping("/{id}")
    @CrossOrigin
    public Object updateProfile(
            @RequestBody ProfileObjectMapper request,
            @PathVariable("id") long id, HttpServletRequest httpRequest) throws RecordNotFoundException, ParseException, UserNotAuthenticatedException {
        Long authId = (Long) httpRequest.getAttribute("authenticatedid");
        if (authId == null) {
        	throw new UserNotAuthenticatedException("You are not logged in");
        } else if (!authId.equals((long)-1) && !authId.equals(id)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Insufficient permission"));
        }

        Optional<Profile> optionalProfile = profileRepository.findById(id);
        if (optionalProfile.isPresent()) {

            try {
                Profile profile = optionalProfile.get();
                request.updateExistingProfile(profile, profileRepository, countryRepository);
                return ResponseEntity.status(HttpStatus.OK).body(new ProfileResponse(profile, emailRepository));
            } catch (InvalidRequestFieldException ex) {
                return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
            }
        }
        return ResponseEntity.notFound();
    }


    /**
     * Handle when user tries to POST to /profiles
     */
    @PostMapping
    @CrossOrigin
    public Object createprofile(@RequestBody ProfileObjectMapper userRequest) throws NoSuchAlgorithmException, RecordNotFoundException, InvalidRequestFieldException {

        User user = null;
        try {
            user = userRequest.createNewProfile(userRepository, emailRepository, profileRepository, countryRepository, activityTypeRepository);
        } catch (ParseException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
        // an InvalidRequestFieldException will be caught by ExceptionHandlerController

        return ResponseEntity.status(HttpStatus.CREATED).body(new ProfileCreatedResponse(user.getUserId()));
    }



    /**
     * Handles viewing another profile
     * @param profileId profile id to view
     * @return response entity to be sent to the client
     */
    @GetMapping("/{profileId}")
    @CrossOrigin
    public ResponseEntity<?> viewProfile(@PathVariable("profileId") long profileId) {

        return view(profileId);
    }

    /**
     * put mapping for updating a profile's activity types interest list
     * @param profileId the profile whose activity types should be updated
     * @param putActivityTypesRequest the body of the request
     * @param httpRequest the HttpServletRequest associated with this request
     * @return the information of the user after being updated
     * @throws RecordNotFoundException if one of the desired activity type names does not exist
     * @throws UserNotAuthenticatedException if the user is not logged in
     */
    @PutMapping("/{profileId}/activity-types")
    public ProfileResponse updateUserActivityTypes(@PathVariable("profileId") long profileId,
                                                          @Valid @RequestBody PutActivityTypesRequest putActivityTypesRequest,
                                                          Errors validationErrors,
                                                          HttpServletRequest httpRequest) throws RecordNotFoundException, UserNotAuthenticatedException, InvalidRequestFieldException {
        // authentication
        Long authId = (Long) httpRequest.getAttribute("authenticatedid");
        if (authId == null) {
            throw new UserNotAuthenticatedException("you are not logged in");
        } else if (!authId.equals((long)-1) && !authId.equals(profileId)) {
            throw new AccessDeniedException("Insufficient permission");
        }

        // validate request body
        if (validationErrors.hasErrors()) {
            throw new InvalidRequestFieldException(validationErrors.getAllErrors().get(0).getDefaultMessage());
        }

        // get relevant profile
        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        if (optionalProfile.isEmpty()) {
            throw new RecordNotFoundException("profile not found");
        }
        Profile profile = optionalProfile.get();

        // validate activity types
        List<ActivityType> activityTypes = new ArrayList<>();
        for (String activityTypeName : putActivityTypesRequest.getActivityTypes()) {
            Optional<ActivityType> optionalActivityType = activityTypeRepository.getActivityTypeByActivityTypeName(activityTypeName);
            System.out.println(activityTypeName);
            if (optionalActivityType.isPresent()) {
                activityTypes.add(optionalActivityType.get());
            } else {
                throw new RecordNotFoundException("no activity type with name " + activityTypeName + " found");
            }
        }

        // save profile with newly added activity types and return
        profile.setActivityTypes(activityTypes);
        profile = profileRepository.save(profile);
        return new ProfileResponse(profile, emailRepository);
    }

    /**
     * Gets information about a certain profile or returns an error object for the client
     * @param id Profile ID of the profile to view
     * @return response entity to be sent to the client
     */
    private ResponseEntity<?> view(long id) {
        if (profileRepository.existsById(id)) {
            Profile profile = profileRepository.findById(id).get();
            return ResponseEntity.ok().body(new ProfileResponse(profile, emailRepository));
        } else {
            return ResponseEntity.status(HttpStatus.resolve(404))
                    .body(new ErrorResponse("Profile with id " + id + " does not exist"));
        }
    }
}
