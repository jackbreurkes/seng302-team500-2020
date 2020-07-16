package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.requests.PutActivityTypesRequest;
import com.springvuegradle.util.FormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private LocationRepository locationRepository;

    private final short ADMIN_USER_MINIMUM_PERMISSION = 120;
    private final short STD_ADMIN_USER_PERMISSION = 126;
    private final short SUPER_ADMIN_USER_PERMISSION = 127;


    /**
     * handle when user tries to PUT /profiles/{profile_id}
     */
    @PutMapping("/{profileId}")
    @CrossOrigin
    public ProfileResponse updateProfile(
            @RequestBody ProfileObjectMapper request,
            @PathVariable("profileId") long profileId, HttpServletRequest httpRequest) throws RecordNotFoundException, ParseException, UserNotAuthenticatedException, InvalidRequestFieldException, UserNotAuthorizedException {
        // check correct authentication
        UserAuthorizer.getInstance().checkIsAuthenticated(httpRequest, profileId, userRepository);
        request.checkParseErrors(); // throws an error if an invalid profile field was sent as part of the request

        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        if (optionalProfile.isEmpty()) {
            throw new RecordNotFoundException("Profile not found");
        }

        Profile profile = optionalProfile.get();

        if (request.getFirstname() != null) {
            profile.setFirstName(request.getFirstname());
        }
        if (request.getLastname() != null) {
            profile.setLastName(request.getLastname());
        }

        profile.setMiddleName(request.getMiddlename());
        profile.setNickName(request.getNickname());
        profile.setBio(request.getBio());

        if (request.getDateOfBirth() != null) {
            LocalDate validDob = FormValidator.getValidDateOfBirth(request.getDateOfBirth());
            if (validDob != null) {
                profile.setDob(validDob);
            }
        }
        if (request.getGender() != null) {
            Gender gender = Gender.matchGender(request.getGender());
            if (gender != null) {
                profile.setGender(gender);
            }
        }
        if (request.getFitness() != null) {
            profile.setFitness(request.getFitness());
        }
        if (request.getPassports() != null) {
            List<Country> countries = getCountriesByNames(request.getPassports()); // throws a RecordNotFoundException if country doesn't exist
            profile.setCountries(countries);
        }
        if (request.getActivities() != null) {
            List<ActivityType> activityTypes = getActivityTypesByNames(request.getActivities()); // throws a RecordNotFoundException if activity type doesn't exist
            profile.setActivityTypes(activityTypes);
        }
        Location location = null;
        if (request.getLocation() != null && request.getLocation().getCity()!=null && request.getLocation().getCity().length()
                > 0 && request.getLocation().getCountry() !=null && request.getLocation().getCountry().length() >0) {
            location = addLocationIfNotExisting(request.getLocation().lookupAndValidate());
        }
        profile.setLocation(location); // setting location to null will remove it

        Profile savedProfile = profileRepository.save(profile);
        return new ProfileResponse(savedProfile, emailRepository);
    }

    private List<Country> getCountriesByNames(String[] countryNames) throws RecordNotFoundException {
        List<Country> countries = new ArrayList<>();
        for (String countryName : countryNames) {
            Optional<Country> country = countryRepository.findByName(countryName);
            if (country.isEmpty()) {
                throw new RecordNotFoundException("country " + countryName + " not found");
            }
            countries.add(country.get());
        }
        return countries;
    }

    private List<ActivityType> getActivityTypesByNames(List<String> activityTypeNames) throws RecordNotFoundException {
        List<ActivityType> activityTypes = new ArrayList<>();
        for (String activityTypeName : activityTypeNames) {
            Optional<ActivityType> optionalActivityType = activityTypeRepository.getActivityTypeByActivityTypeName(activityTypeName);
            System.out.println(activityTypeName);
            if (optionalActivityType.isPresent()) {
                activityTypes.add(optionalActivityType.get());
            } else {
                throw new RecordNotFoundException("no activity type with name " + activityTypeName + " found");
            }
        }
        return activityTypes;
    }


    /**
     * Handle when user tries to POST to /profiles
     */
    @PostMapping
    @CrossOrigin
    public Object createprofile(@RequestBody ProfileObjectMapper userRequest) throws NoSuchAlgorithmException, RecordNotFoundException, InvalidRequestFieldException {

        User user = null;
        try {
            user = userRequest.createNewProfile(userRepository, emailRepository, profileRepository, countryRepository, activityTypeRepository, locationRepository);
        } catch (ParseException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
        // an InvalidRequestFieldException will be caught by ExceptionHandlerController

        return ResponseEntity.status(HttpStatus.CREATED).body(new ProfileCreatedResponse(user.getUserId()));
    }


//    @PutMapping("/profiles/{profileId}/location")
//    @CrossOrigin
//    public void updateLocation(@PathVariable("profileId") Long profileId, @RequestBody LocationRequest locationRequest, HttpServletRequest request) throws UserNotAuthenticatedException, InvalidRequestFieldException, RecordNotFoundException {
//        // check correct authentication
//        Long authId = (Long) request.getAttribute("authenticatedid");
//
//        Optional<User> editingUser = userRepository.findById(authId);
//
//
//        if (locationRequest.getLocation().getCity() == null || locationRequest.getLocation().getCountry() == null) {
//            throw new InvalidRequestFieldException("location must have a city and a country");
//        }
//
//        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
//        if (optionalProfile.isEmpty()) {
//            throw new RecordNotFoundException("no profile with given ID found");
//        }
//        Profile profile = optionalProfile.get();
//
//        Location location = addLocationIfNotExisting(locationRequest.getLocation());
//        profile.setLocation(location);
//        profileRepository.save(profile);
//    }


    /**
     * adds a location to the database if it doesn't exist, otherwise returns the existing value.
     * @param location the location to find a match for
     * @return a location from the database
     */
    private Location addLocationIfNotExisting(Location location) {
        Optional<Location> existing = locationRepository.findLocationByCityAndCountry(location.getCity(), location.getCountry());
        if (existing != null && existing.isPresent()) {
            return existing.get();
        }

        return locationRepository.save(location);
    }



    /**
     * Handles viewing another profile
     * @param profileId profile id to view
     * @return response entity to be sent to the client
     */
    @GetMapping("/{profileId}")
    @CrossOrigin
    public ProfileResponse viewProfile(@PathVariable("profileId") long profileId, HttpServletRequest request) throws UserNotAuthenticatedException, RecordNotFoundException {
        if (request.getAttribute("authenticatedid") != null){
            return view(profileId); // throws record not found exception if user does not exist
        } else{
            throw new UserNotAuthenticatedException("User not authenticated");
        }
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
    @CrossOrigin
    public ProfileResponse updateUserActivityTypes(@PathVariable("profileId") long profileId,
                                                          @Valid @RequestBody PutActivityTypesRequest putActivityTypesRequest,
                                                          Errors validationErrors,
                                                          HttpServletRequest httpRequest) throws RecordNotFoundException, UserNotAuthenticatedException, InvalidRequestFieldException, UserNotAuthorizedException {
        // authentication
        Long authId = (Long) httpRequest.getAttribute("authenticatedid");

        UserAuthorizer.getInstance().checkIsAuthenticated(httpRequest, profileId, userRepository);

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
        List<ActivityType> activityTypes = getActivityTypesByNames(putActivityTypesRequest.getActivityTypes());


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
    private ProfileResponse view(long id) throws RecordNotFoundException {
        if (profileRepository.existsById(id)) {
            Profile profile = profileRepository.findById(id).get();
            return new ProfileResponse(profile, emailRepository);
        } else {
            throw new RecordNotFoundException("Profile with id " + id + " not found");
        }
    }
}
