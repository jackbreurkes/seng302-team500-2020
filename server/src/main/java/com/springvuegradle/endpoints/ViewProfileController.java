package com.springvuegradle.endpoints;

import javax.servlet.http.HttpServletRequest;

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

///**
// * Class which handles viewing a profile, including other users' profiles
// *
// * @author Alex Hobson
// *
// */
//@RestController
//public class ViewProfileController {
//
//	/**
//	 * Profile repository
//	 */
//	@Autowired
//	private ProfileRepository profileRepository;
//
//}
