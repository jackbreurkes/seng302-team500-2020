package com.springvuegradle.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.repository.SessionRepository;
import com.springvuegradle.model.responses.ErrorResponse;

/**
 * Rest controller for logging out a user
 */
@RestController
public class LogoutController {

	@Autowired
	private SessionRepository sessionRepo;

	@DeleteMapping("/logmeout")
	@CrossOrigin
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		if (request.getAttribute("authenticatedid") == null) {
			return ResponseEntity.status(401).body(new ErrorResponse("You are not logged in"));
		}

		String token = request.getHeader("X-Auth-Token");
		if (token == null) {
			return ResponseEntity.status(HttpStatus.resolve(401)).body(new ErrorResponse("user not logged in"));
		}

		if (!sessionRepo.existsById(token)) {
			return ResponseEntity.status(HttpStatus.resolve(404)).body(new ErrorResponse("session not found"));
		}

		sessionRepo.deleteById(token);
		return ResponseEntity.noContent().build();
	}
}
