package com.springvuegradle.endpoints;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.springvuegradle.exceptions.UserNotAuthorizedException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.auth.UserAuthorizer;
import com.springvuegradle.model.data.*;
import com.springvuegradle.model.repository.*;
import com.springvuegradle.model.requests.PutActivityTypesRequest;
import com.springvuegradle.model.requests.UpdateRoleRequest;
import com.springvuegradle.util.FormValidator;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import com.springvuegradle.exceptions.ForbiddenOperationException;
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
     * Repository used for accessing activities
     */
    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Repository used for accessing activity types
     */
    @Autowired
    private ActivityTypeRepository activityTypeRepository;
    
    /**
     * Repository used to hold mappings of role names to the role's id
     */
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ChangeLogRepository changeLogRepository;

    /**
     * Repository used for accessing locations
     */
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
            @Valid @RequestBody ProfileObjectMapper request,
            @PathVariable("profileId") long profileId, HttpServletRequest httpRequest) throws RecordNotFoundException, ParseException, UserNotAuthenticatedException, InvalidRequestFieldException, UserNotAuthorizedException {
        // check correct authentication
        UserAuthorizer.getInstance().checkIsTargetUserOrAdmin(httpRequest, profileId, userRepository);
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
    	String searchedFirstname = request.getParameter("firstname");	// First-, middle-, last-name parameters can be used instead of fullname to aid in dealing with names with spaces
    	String searchedMiddlename = request.getParameter("middlename");
    	String searchedLastname = request.getParameter("lastname");
    	String searchedNickname = request.getParameter("nickname");
    	String searchedEmail = request.getParameter("email");
        String searchedActivities = request.getParameter("activity");
        String method = request.getParameter("method");
        String useExactEmail = request.getParameter("exactEmail"); // if the email should be found exactly

    	List<Profile> profiles = new ArrayList<Profile>();	// would eventually be results from query of database with parameters
    	
		if (searchedNickname != null && !searchedNickname.equals("")) {
			profiles = profileRepository.findByNickNameStartingWith(searchedNickname);
		} else if (searchedFirstname != null && !searchedFirstname.equals("")) {
			profiles = getUsersByNamePieces(searchedFirstname, searchedMiddlename, searchedLastname);
		} else if (searchedFullname != null && !searchedFullname.equals("")) {
			profiles = getUsersByFullname(searchedFullname);
		} else if (searchedEmail != null && !searchedEmail.equals("")) {
		    boolean exact = useExactEmail != null && useExactEmail.equals("true");
			profiles = getUsersByEmail(searchedEmail, exact);
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
     * Function for finding users by the initial portion of their first, middle and last names, each given separately
     * @param firstname initial (or full) part of the firstname to find
     * @param middlename initial (or full) part of the middlename to find
     * @param lastname initial (or full) part of the lastname to find
     * @return list of profiles whose first, middle and last names match the portions specified
     * @throws InvalidRequestFieldException if there is not at least one character in both of the first and last names
     */
    private List<Profile> getUsersByNamePieces(String firstname, String middlename, String lastname) throws InvalidRequestFieldException {

    	if (lastname == null || lastname.length() == 0) {
    		throw new InvalidRequestFieldException("Has not provided a valid full name (made up of at least a first and last name)");
    	}
    	
    	List<Profile> profiles = new ArrayList<Profile>();
    	if (middlename == null || middlename.length() == 0) {
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
    private List<Profile> getUsersByEmail(String email, boolean exact) throws InvalidRequestFieldException, RecordNotFoundException {
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

    	if (!exact) {
            emails = emailRepository.findByEmailStartingWith(email);
        } else {
    	    Email exactEmail = emailRepository.findByEmail(email);
    	    if (exactEmail == null) {
    	        throw new RecordNotFoundException("cannot find email " + email);
            }
    	    emails.add(exactEmail);
        }
    	
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
    public Object createprofile(@Valid @RequestBody ProfileObjectMapper userRequest) throws NoSuchAlgorithmException, RecordNotFoundException, InvalidRequestFieldException {

        User user = null;
        try {
            user = userRequest.createNewProfile(userRepository, emailRepository, profileRepository, countryRepository, activityTypeRepository, locationRepository);
        } catch (ParseException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
        // an InvalidRequestFieldException will be caught by ExceptionHandlerController

        return ResponseEntity.status(HttpStatus.CREATED).body(new ProfileCreatedResponse(user.getUserId()));
    }


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
        UserAuthorizer.getInstance().checkIsTargetUserOrAdmin(httpRequest, profileId, userRepository);

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
            ProfileResponse response = new ProfileResponse(profile, emailRepository);

            // Calling the Open Street Map API to get the latitude and longitude of the user location and saving it
            try {
                URL url = new URL("https://nominatim.openstreetmap.org/search?city=" + profile.getLocation().getCity().replace(" ", "+") +
                        "&country=" + profile.getLocation().getCountry().replace(" ", "+") + "&format=json&limit=1");

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                // Reading the response
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                con.disconnect();

                JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
                JSONArray result = (JSONArray) parser.parse(content.toString()); // It always returns a JSONArray even though there's only ever one

                JSONObject single = (JSONObject) result.get(0);
                String lat = (String) single.get("lat");
                String lon = (String) single.get("lon");

                response.setLat(Float.parseFloat(lat));
                response.setLon(Float.parseFloat(lon));
            } catch (Exception e) {
                // Exception is caught when profile doesn't have a location associated with it
            }
            return response;
        } else {
            throw new RecordNotFoundException("Profile with id " + id + " not found");
        }
    }
    
    /**
     * Update the role 
     * @param profileId id of the user whose permission level should be updated
     * @param updateRoleRequest the body of the request
     * @param validationErrors errors with the request
     * @param httpRequest the HttpServletRequest associated with this request
     * @return response entity with informative statement about updated role
     * @throws RecordNotFoundException
     * @throws UserNotAuthenticatedException
     * @throws InvalidRequestFieldException
     * @throws ForbiddenOperationException
     */
    @PutMapping("/{profileId}/role")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    public void updateUserRole(@PathVariable("profileId") long profileId,
                                                          @Valid @RequestBody UpdateRoleRequest updateRoleRequest,
                                                          Errors validationErrors,
                                                          HttpServletRequest httpRequest) throws RecordNotFoundException, UserNotAuthenticatedException, InvalidRequestFieldException, UserNotAuthorizedException, ForbiddenOperationException {
        // authentication
        Long authId = UserAuthorizer.getInstance().checkIsAdmin(httpRequest, userRepository);

        // validate request body
        if (validationErrors.hasErrors()) {
            throw new InvalidRequestFieldException(validationErrors.getAllErrors().get(0).getDefaultMessage());
        }

        // get relevant profile
        Optional<User> optionalUser = userRepository.findById(profileId);
        if (optionalUser.isEmpty()) {
            throw new RecordNotFoundException("user not found");
        }
        User user = optionalUser.get();
   
        String roleName = updateRoleRequest.getRole();

        // validate role
        long permissionLevel = getRolePermissionLevel(roleName);
        
        // do not allow admins to increase their privileges
        if (profileId == authId && permissionLevel > user.getPermissionLevel()) {	// if they are trying to promote themselves
        	throw new ForbiddenOperationException("cannot promote self");
        }
        
        // do not allow admins to create new superadmins
        if (permissionLevel == 127) {
        	throw new ForbiddenOperationException("cannot promote a user to superadmin role");
        }
 
        // save profile with newly added activity types and return
        user.setPermissionLevel((int) permissionLevel);
        userRepository.save(user);
    }

    /**
     * Get the permission level associated with a given role name
     * @param roleName the string role name to find in the system
     * @return permission level associated with the provided role name or -1 if no role with the given name exists
     * @throws RecordNotFoundException there if no role with the supplied rolename
     */
    private long getRolePermissionLevel(String roleName) throws RecordNotFoundException {

    	Role role = roleRepository.findByRolename(roleName);

    	if (role == null) {
    		throw new RecordNotFoundException("Could not find role with name: " + roleName);
    	}

		return role.getRole_id();

    }


    /**
     * Deletes a user and its associated data with a user id by deleting all the profile data
     * and then deleting the user itself.
     * @param profileId of the user to be deleted
     * @throws RecordNotFoundException if the user doesn't exist
     * @throws UserNotAuthenticatedException if the user isn't the original profile or not an admin
     */
    @DeleteMapping("/{profileId}")
    @CrossOrigin
    public String deleteUser(@PathVariable("profileId") long profileId,
                                             HttpServletRequest httpRequest) throws UserNotAuthenticatedException, RecordNotFoundException, UserNotAuthorizedException {
        // Authenticating the logged in user
        UserAuthorizer.getInstance().checkIsTargetUserOrAdmin(httpRequest, profileId, userRepository);

        // Get relevant profile
        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        if (optionalProfile.isEmpty()) {
            throw new RecordNotFoundException("Profile not found.");
        }
        Profile profile = optionalProfile.get();
        User user = profile.getUser();

        List<Activity> createdActivities = activityRepository.findActivitiesByCreator(profile);
        for (Activity activity : createdActivities) {
            activityRepository.delete(activity);
        }
        changeLogRepository.clearEditorInformation(profileId);

        profileRepository.delete(profile);
        return "Deleted profile with id " + user.getUserId();
    }
}
