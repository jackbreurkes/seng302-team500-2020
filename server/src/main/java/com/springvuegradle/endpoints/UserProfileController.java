package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.springvuegradle.model.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
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
     * handle when user tries to PUT /profiles/{profile_id}
     */
    @PutMapping("/{id}")
    @CrossOrigin
    public Object updateProfile(
            @RequestBody ProfileObjectMapper request,
            @PathVariable("id") long id, HttpServletRequest httpRequest) throws RecordNotFoundException, ParseException {
        Long authId = (Long) httpRequest.getAttribute("authenticatedid");
        if (authId == null) {
        	return ResponseEntity.status(401).body(new ErrorResponse("You are not logged in"));
        } else if (!authId.equals((long)-1) && !authId.equals(id)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Insufficient permission"));
        }

        Optional<Profile> optionalProfile = profileRepository.findById(id);
        if (optionalProfile.isPresent()) {
            System.out.println("you exist!");

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
    public Object createprofile(@RequestBody ProfileObjectMapper userRequest) throws NoSuchAlgorithmException, RecordNotFoundException {

        User user = null;
        try {
            user = userRequest.createNewProfile(userRepository, emailRepository, profileRepository, countryRepository);
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
    @CrossOrigin
    public ResponseEntity<?> viewProfile(@PathVariable("profileId") long profileId) {

        return view(profileId);
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
