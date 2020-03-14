package com.springvuegradle.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.springvuegradle.model.responses.ProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.responses.AdminLoggedInResponse;
import com.springvuegradle.model.responses.ErrorResponse;

/**
 * Class which handles viewing a profile, including other users' profiles
 * 
 * @author Alex Hobson
 *
 */
@RestController
public class ViewProfileController {

	/**
	 * Profile repository
	 */
	@Autowired
	private ProfileRepository profileRepo;

	/**
	 * Handles viewing another profile
	 * 
	 * @param profileId
	 *            profile id to view
	 * @return response entity to be sent to the client
	 */
	@GetMapping("/profiles/{profileId}")
	public ResponseEntity<?> viewProfile(@PathVariable("profileId") long profileId) {

		return view(profileId);
	}

	/**
	 * Handles viewing your own profile, including when you don't know what your
	 * profile ID is
	 * 
	 * @param request HttpServletRequest including attribute of authentication information
	 * @return response entity to be sent to the client
	 */
	@GetMapping("/profiles")
	public ResponseEntity<?> viewProfile(HttpServletRequest request) {
		if (request.getAttribute("authenticatedid") == null) {
			return ResponseEntity.badRequest()
					.body(new ErrorResponse("you must be authenticated"));
		}
		long id = (long) request.getAttribute("authenticatedid");
		if (id == -1) {
			return ResponseEntity.ok().body(new AdminLoggedInResponse());
		} else {
			return view(id);
		}
	}

	/**
	 * Gets information about a certain profile or returns an error object for the client
	 * @param id Profile ID of the profile to view
	 * @return response entity to be sent to the client
	 */
	private ResponseEntity<?> view(long id) {
		if (profileRepo.existsById(id)) {
			Profile profile = profileRepo.findById(id).get();
			return ResponseEntity.ok().body(new ProfileResponse(profile));
		} else {
			return ResponseEntity.status(HttpStatus.resolve(500))
					.body(new ErrorResponse("Profile with id " + id + " does not exist"));
		}
	}
}
