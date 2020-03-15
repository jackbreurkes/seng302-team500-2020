package com.springvuegradle.endpoints;

import com.fasterxml.jackson.annotation.JsonView;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.CreateUserRequest;
import com.springvuegradle.model.requests.ProfileObjectMapper;
import com.springvuegradle.model.responses.AdminLoggedInResponse;
import com.springvuegradle.model.responses.ErrorResponse;
import com.springvuegradle.model.responses.ProfileCreatedResponse;
import com.springvuegradle.model.responses.ProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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
     * handle when user tries to PUT /profiles/{profile_id}
     */
    @PutMapping("/{id}")
    public Object updateProfile(
            @RequestBody ProfileObjectMapper request,
            @PathVariable("id") Long id, HttpServletRequest httpRequest) throws RecordNotFoundException, ParseException {
        Long authId = (Long) httpRequest.getAttribute("authenticatedid");
        if (authId == null || (!authId.equals((long)-1) && !authId.equals(id))) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("cannot edit user unless you are them or an admin"));
        }

        Optional<Profile> optionalProfile = profileRepository.findById(id);
        if (optionalProfile.isPresent()) {
            try {
                Profile profile = optionalProfile.get();
                request.updateExistingProfile(profile, profileRepository);
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
    public Object createprofile(@RequestBody ProfileObjectMapper userRequest) throws NoSuchAlgorithmException {

        User user = null;
        try {
            user = userRequest.createNewProfile(userRepository, emailRepository, profileRepository);
        } catch (InvalidRequestFieldException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        } catch (ParseException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ProfileCreatedResponse(user.getUserId()));
    }



    /**
     * Handles viewing another profile
     *
     * @param profileId
     *            profile id to view
     * @return response entity to be sent to the client
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<?> viewProfile(@PathVariable("profileId") long profileId) {

        return view(profileId);
    }
//
//    /**
//     * Handles viewing your own profile, including when you don't know what your
//     * profile ID is
//     *
//     * @param request HttpServletRequest including attribute of authentication information
//     * @return response entity to be sent to the client
//     */
//    @GetMapping("/profiles")
//    public ResponseEntity<?> viewProfile(HttpServletRequest request) {
//        if (request.getAttribute("authenticatedid") == null) {
//            return ResponseEntity.badRequest()
//                    .body(new ErrorResponse("you must be authenticated"));
//        }
//        long id = (long) request.getAttribute("authenticatedid");
//        if (id == -1) {
//            return ResponseEntity.ok().body(new AdminLoggedInResponse());
//        } else {
//            return view(id);
//        }
//    }

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
            return ResponseEntity.status(HttpStatus.resolve(500))
                    .body(new ErrorResponse("Profile with id " + id + " does not exist"));
        }
    }
}
