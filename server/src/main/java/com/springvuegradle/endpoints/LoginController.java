package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.auth.ChecksumUtils;
import com.springvuegradle.auth.JwtUtil;
import com.springvuegradle.auth.MyUserDetailsService;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.LoginRequest;
import com.springvuegradle.model.responses.AuthenticationResponse;
import com.springvuegradle.model.responses.ErrorResponse;

/**
 * REST endpoint for logging in a user
 * 
 * @author Alex Hobson
 *
 */
@RestController
public class LoginController {

	/**
	 * Repository of email addresses stored in the database
	 */
	@Autowired
	private EmailRepository emailRepo;
	
	@Autowired
	private UserRepository userRepo;

	/**
	 * Authentication manager instance
	 */
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private MyUserDetailsService userDetailsService;

	/**
	 * Handle when the user tries to log in POST -> (CRUD) Creating a session
	 * 
	 * @param credentials LoginRequest containing the email/password sent by the
	 *                    client
	 * @return Responsebody for spring to send to client
	 * @throws NoSuchAlgorithmException
	 */

	@PostMapping("/login")
	public Object login(@RequestBody(required = true) LoginRequest credentials) throws NoSuchAlgorithmException {
		if (credentials.getEmail() == null || credentials.getPassword() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Missing email and/or password field"));
		}
		
		if (!emailRepo.existsById(credentials.getEmail())) {
			return new ErrorResponse("Email address is not registered");
		}
		
		Email email = emailRepo.findByEmail(credentials.getEmail());
		User user = email.getUser();
		String hashedRequestPassword = ChecksumUtils.hashPassword(user.getUserId(), credentials.getPassword());

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(email.getEmail(), hashedRequestPassword));
		} catch (BadCredentialsException e) {
			// authentication failed
			new ErrorResponse("Authentication Failed");
		}
		// now we need to return the JWT

		final UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.getEmail());
		final String jwt = JwtUtil.generateToken(userDetails);

		// Need to create a new session object
		Session s = new Session(emailRepo.findByEmail(credentials.getEmail()).getUser(), jwt);
		// need to add the session to the correct repo
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
}
