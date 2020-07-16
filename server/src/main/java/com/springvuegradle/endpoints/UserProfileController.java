package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.requests.PutActivityTypesRequest;
import com.springvuegradle.util.FormValidator;

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
            @PathVariable("profileId") long profileId, HttpServletRequest httpRequest) throws RecordNotFoundException, ParseException, UserNotAuthenticatedException, InvalidRequestFieldException {
        // check correct authentication
        Long authId = (Long) httpRequest.getAttribute("authenticatedid");
//        Short permissionLevel = (Short) httpRequest.getAttribute("permissionLevel");
//        boolean isTargetUser = authId != null && authId == profileId;
//        boolean isAdmin = permissionLevel != null && permissionLevel > ADMIN_USER_MINIMUM_PERMISSION;
//        if (!isTargetUser && !isAdmin) {
//            throw new UserNotAuthenticatedException("you must be authenticated as the target user or an admin");
//        }
        Optional<User> editingUser = userRepository.findById(authId);

        if (authId == null || !(authId == profileId) && (editingUser.isPresent() && !(editingUser.get().getPermissionLevel() > ADMIN_USER_MINIMUM_PERMISSION))) {
            //here we check permission level and update the password accordingly
            //assuming failure without admin
            throw new UserNotAuthenticatedException("you must be authenticated as the target user or an admin");
        }
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
     * Handle requests to GET a list of users with optional fullname, nickname and email parameters
     * If multiple parameters are provided, it will search only by one. The priority of parameters is: nickname, full name, email
     * @param request HTTPServletRequest corresponding to the user's request
     * @return an HTTP ResponseEntity with the HTTP response containing all users satisfying the query parameters
     * @throws InvalidRequestFieldException 
     */
    @GetMapping
    @CrossOrigin
    public List<ProfileResponse> searchUsers(HttpServletRequest request) throws InvalidRequestFieldException, RecordNotFoundException {
    	    	
    	// The following Strings will simply be null if the associated parameter is not specified
    	String searchedFullname = request.getParameter("fullname");
    	String searchedNickname = request.getParameter("nickname");
    	String searchedEmail = request.getParameter("email");
        String searchedActivities = request.getParameter("activity");
        String method = request.getParameter("method");

    	List<Profile> profiles = new ArrayList<Profile>();	// would eventually be results from query of database with parameters
    	
		if (searchedNickname != null && searchedNickname != "") {
			profiles = profileRepository.findByNickNameStartingWith(searchedNickname);
		} else if (searchedFullname != null && searchedFullname != "") {
			profiles = getUsersByFullname(searchedFullname);
		} else if (searchedEmail != null && searchedEmail != "") {
			profiles = getUsersByEmail(searchedEmail);
		} else if (searchedActivities != null) {
		    profiles = getProfilesByActivityTypes(searchedActivities, method);
        }

		List<ProfileResponse> responses = new ArrayList<>();
		for (Profile profile : profiles) {
		    responses.add(new ProfileResponse(profile, emailRepository));
        }
		return responses;
    }
    
    /**
     * Get all profiles of users with a full name partially or fully matching the full name given according to specified search rules
     * @param fullname the substring of the full name to search for
     * @return list of profiles of users with a full name matching that given
     * @throws InvalidRequestFieldException when full name does not have at least a first and a last name separated by a ' '
     */
    private List<Profile> getUsersByFullname(String fullname) throws InvalidRequestFieldException {
    	String[] names = fullname.strip().split(" ");
    	if (names.length < 2) {
    		throw new InvalidRequestFieldException("Has not provided a valid full name (made up of at least a first and last name)");
    	}
    	
    	// TODO: Make able to handle names with spaces (e.g. lastname: "van Beethoven")
    	String firstname = "";
    	String middlename = "";
    	String lastname = "";
    	if (names.length == 3) {
	    	firstname = names[0];
	    	middlename = names[1];
	    	lastname = names[2];
    	} else if (names.length == 2) {
    		firstname = names[0];
	    	lastname = names[1];
    	}
    	if (firstname.length() == 0 || lastname.length() == 0) {
    		throw new InvalidRequestFieldException("Has not provided a valid full name (made up of at least a first and last name)");
    	}
    	
    	List<Profile> profiles = new ArrayList<Profile>();
    	if (middlename.length() == 0) {
    		profiles = profileRepository.findByFirstNameStartingWithAndLastNameStartingWith(firstname, lastname);
    	} else {
    		profiles = profileRepository.findByFirstNameStartingWithAndMiddleNameStartingWithAndLastNameStartingWith(firstname, middlename, lastname);
    	}

		return profiles;
    }
    
    /**
     * Get all users with the email matching that given according to the search match rules of the system
     * @param email the email (potentially a partial email) to search for
     * @return list of profiles which have associated emails matching that given
     * @throws InvalidRequestFieldException when email given has more than one '@' symbol
     */
    private List<Profile> getUsersByEmail(String email) throws InvalidRequestFieldException {
    	/* EMAIL SEARCH
    	  # must match full text before '@' symbol if there is no @ in the search query
    	  # if there is an @ in the query, match the query string then anything after
    	  # e.g. test@gmail.co(.*) */
    	
    	List<Email> emails = new ArrayList<Email>();
    	Set<Profile> profileSet = new HashSet<Profile>();
    	List<Profile> profiles = new ArrayList<Profile>();

    	String[] emailPortions = email.split("@");
    	if  (!email.contains("@")) {
    		// Is not an '@' in the search so only try to match up to the '@' symbol in the database
    		email = email + "@"; 		// Append '@' to the end so the email is forced to match the entire first portion
    	} else if (emailPortions.length > 2) {
    		// Will return empty profile list if has more than 1 '@' symbol - is correct outcome as email would violate system rules
    		throw new InvalidRequestFieldException("Has not provided a valid email (too many '@' symbols).");
    	}
    	
    	emails = emailRepository.findByEmailStartingWith(email);
    	
    	for (Email foundEmail: emails) {
    		User foundUser = foundEmail.getUser();
    		Profile foundProfile = profileRepository.getOne(foundUser.getUserId());
    		profileSet.add(foundProfile);
    	}
    	
    	profileSet.forEach((Profile profile) -> {profiles.add(profile);});

    	return profiles;
    }

    /**
     * retrieves profiles based on a search by activity types they are interested in.
     * @param spaceSeparatedActivityTypeNames the space separated search string of activity type names. case sensitive
     * @param method if multiple activity types are provided, should specify "and" or "or" for how the search should treat them
     * @return the list of profiles matching the search
     */
    private List<Profile> getProfilesByActivityTypes(String spaceSeparatedActivityTypeNames, String method) throws InvalidRequestFieldException, RecordNotFoundException {
        List<Profile> profiles = new ArrayList<>();

        if (spaceSeparatedActivityTypeNames.isBlank()) {
            throw new InvalidRequestFieldException("activity search string cannot be empty");
        }

        List<String> activityTypeNames = Arrays.asList(spaceSeparatedActivityTypeNames.split(" "));

        if (!Arrays.asList("or", "and").contains(method)) {
            if (activityTypeNames.size() > 1) {
                if (method == null) {
                    throw new InvalidRequestFieldException("a 'method' param must be supplied when searching by multiple activity types");
                } else {
                    throw new InvalidRequestFieldException("the method provided for activity type search must be either 'and' or 'or'");
                }
            } else {
                method = "or";
            }
        }

        for (String name : activityTypeNames) {
            if (activityTypeRepository.getActivityTypeByActivityTypeName(name).isEmpty()) {
                throw new RecordNotFoundException(String.format("an activity type named '%s' does not exist", name));
            }
        }

        if (method.toLowerCase().equals("or")) {
            profiles = profileRepository.findByActivityTypesContainsAnyOf(activityTypeNames);
        } else { // method equals "and"
            profiles = profileRepository.findByActivityTypesContainsAllOf(activityTypeNames);
        }
        return profiles;
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
//        if (authId == null || !(authId == profileId) && (editingUser.isPresent() && !(editingUser.get().getPermissionLevel() > ADMIN_USER_MINIMUM_PERMISSION))) {
//            //here we check permission level and update the password accordingly
//            //assuming failure without admin
//            throw new UserNotAuthenticatedException("you must be authenticated as the target user or an admin");
//        }
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
                                                          HttpServletRequest httpRequest) throws RecordNotFoundException, UserNotAuthenticatedException, InvalidRequestFieldException {
        // authentication
        Long authId = (Long) httpRequest.getAttribute("authenticatedid");

        Optional<User> editingUser = userRepository.findById(authId);

        if (authId == null || !(authId == profileId) && (editingUser.isPresent() && !(editingUser.get().getPermissionLevel() > ADMIN_USER_MINIMUM_PERMISSION))) {
            throw new UserNotAuthenticatedException("you must be authenticated as the target user or an admin");
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
