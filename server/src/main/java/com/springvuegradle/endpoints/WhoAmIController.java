package com.springvuegradle.endpoints;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.responses.ErrorResponse;
import com.springvuegradle.model.responses.UserResponse;

/**
 * Process GET /whoami
 * @author Alex Hobson
 *
 */
@RestController
public class WhoAmIController {

	/**
	 * Processes a /whoami GET request
	 * @param httpRequest Contains authentication information
	 * @return ResponseEntity depending on whether the user is logged in or not
	 */
	@GetMapping("/whoami")
	@CrossOrigin
	public Object whoAmI(HttpServletRequest httpRequest) {
		Long authId = (Long) httpRequest.getAttribute("authenticatedid");
		
		if (authId == null) {
			return ResponseEntity.status(401).body(new ErrorResponse("You are not logged in"));
		} else {
			return ResponseEntity.status(200).body(new UserResponse(authId));
		}
	}
}
