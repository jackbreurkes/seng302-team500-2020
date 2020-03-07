package com.springvuegradle.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.responses.AdminLoggedInResponse;
import com.springvuegradle.model.responses.ErrorResponse;

@RestController
public class ViewProfileController {
	
	@Autowired
	private ProfileRepository profileRepo;

	@GetMapping("/viewprofile")
	public ResponseEntity<?> viewProfile(HttpServletRequest request, HttpServletResponse response) {
		if (request.getAttribute("authenticatedid") != null) {
			long id = (long) request.getAttribute("authenticatedid");
			if (id == -1) {
				return ResponseEntity.ok().body(new AdminLoggedInResponse());
			} else {
				if (profileRepo.existsById(id)) {
					Profile profile = profileRepo.findById(id).get();
					return ResponseEntity.ok().body(profile);
				} else {
					return ResponseEntity.status(HttpStatus.resolve(500)).body(new ErrorResponse("Profile with id "+id+" does not exist"));
				}
			}
		} else {
			return ResponseEntity.badRequest().body(new ErrorResponse("Somehow you bypassed authentication. This shouldn't be possible"));
		}
	}
}
