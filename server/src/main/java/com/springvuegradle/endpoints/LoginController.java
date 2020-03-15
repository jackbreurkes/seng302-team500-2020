package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.auth.ChecksumUtils;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.SessionRepository;
import com.springvuegradle.model.requests.LoginRequest;
import com.springvuegradle.model.responses.ErrorResponse;
import com.springvuegradle.model.responses.LoginSuccessResponse;

/**
 * REST endpoint for logging in a user
 * 
 * @author Alex Hobson
 *
 */
@RestController
public class LoginController {
	
	/**
	 * How long a user should stay logged in for
	 */
	private static final int loginSeconds = 24 * 60 * 60;

	/**
	 * Repository of email addresses stored in the database
	 */
	@Autowired
	private EmailRepository emailRepo;
	
	@Autowired
	private SessionRepository sessionRepo;

	/**
	 * Handle when the user tries to log in POST -> (CRUD) Creating a session
	 * 
	 * @param credentials LoginRequest containing the email/password sent by the
	 *                    client
	 * @return Responsebody for spring to send to client
	 * @throws NoSuchAlgorithmException
	 */

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody(required = true) LoginRequest credentials, HttpServletResponse response) throws NoSuchAlgorithmException {
		if (credentials.getEmail() == null || credentials.getPassword() == null) {
			return ResponseEntity.status(HttpStatus.resolve(400)).body(new ErrorResponse("Missing email and/or password field"));
		}
		
		if (!emailRepo.existsById(credentials.getEmail())) {
			return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("Email address is not registered"));
		}
		
		Email email = emailRepo.findByEmail(credentials.getEmail());
		User user = email.getUser();
		String hashedRequestPassword = ChecksumUtils.hashPassword(user.getUserId(), credentials.getPassword());
		
		if (hashedRequestPassword.equals(user.getPassword())) {
			String token = ChecksumUtils.generateToken(user.getUserId());

			Session session = new Session(user, token, 
					Instant.now().plus(loginSeconds, ChronoUnit.SECONDS).atOffset(ZoneOffset.UTC));
			sessionRepo.save(session);
			
			return ResponseEntity.status(HttpStatus.resolve(201)).body(new LoginSuccessResponse(token, user.getUserId()));
		} else {
			return ResponseEntity.status(HttpStatus.resolve(401)).body(new ErrorResponse("Password is not correct"));
		}
	}
}
