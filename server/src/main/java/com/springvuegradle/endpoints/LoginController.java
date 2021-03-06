package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.auth.ChecksumUtils;
import com.springvuegradle.exceptions.InvalidRequestFieldException;
import com.springvuegradle.exceptions.RecordNotFoundException;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.SessionRepository;
import com.springvuegradle.model.requests.LoginRequest;
import com.springvuegradle.model.responses.LoginSuccessResponse;

/**
 * REST endpoint for logging in a user
 */
@RestController
public class LoginController {
	
	/**
	 * How long a user should stay logged in for
	 */
	private static final int LOGIN_TOKEN_LIFETIME = 24 * 60 * 60;

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
	@CrossOrigin
	public LoginSuccessResponse login(@Valid @RequestBody LoginRequest credentials, HttpServletResponse response) throws InvalidRequestFieldException, RecordNotFoundException, NoSuchAlgorithmException, UserNotAuthenticatedException {
		if (credentials.getEmail() == null || credentials.getPassword() == null) {
			throw new InvalidRequestFieldException("Missing email and/or password field");
		}
		
		if (!emailRepo.existsById(credentials.getEmail())) {
			throw new RecordNotFoundException("Email address is not registered");
		}
		
		Email email = emailRepo.findByEmail(credentials.getEmail());
		User user = email.getUser();
		String hashedRequestPassword = ChecksumUtils.hashPassword(user.getUserId(), credentials.getPassword());
		
		if (hashedRequestPassword.equals(user.getPassword())) {
			String token = ChecksumUtils.generateToken(user.getUserId());

			Session session = new Session(user, token, 
					Instant.now().plus(LOGIN_TOKEN_LIFETIME, ChronoUnit.SECONDS).atOffset(ZoneOffset.UTC));
			sessionRepo.save(session);
			
			return new LoginSuccessResponse(token, user.getUserId(), user.getPermissionLevel());
		} else {
			throw new UserNotAuthenticatedException("Password is not correct");
		}
	}
}
