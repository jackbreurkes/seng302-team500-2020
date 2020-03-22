package com.springvuegradle.endpoints;

import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springvuegradle.auth.ChecksumUtils;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.LoginRequest;
import com.springvuegradle.model.requests.NewEmailRequest;
import com.springvuegradle.model.responses.ErrorResponse;
import com.springvuegradle.model.responses.LoginSuccessResponse;

import net.minidev.json.JSONObject;

@RestController
public class NewEmailController {

	@Autowired
	private EmailRepository emailRepo;
	@Autowired
	private UserRepository userRepo;

	@PostMapping("/profiles/{profileId}/emails")
	@CrossOrigin
	public ResponseEntity<?> updateEmails(@RequestBody String raw, @PathVariable("profileId") long profileId, HttpServletResponse response) throws NoSuchAlgorithmException {
		System.out.println("========================================================================");
		System.out.println(raw);
		System.out.println(profileId);
		User user = userRepo.getOne(profileId);
		
		JSONParser parser = new JSONParser(raw);
		//JSONObject json;
		LinkedHashMap<String, Object> json = null;
		try {
			json = (LinkedHashMap<String, Object>) parser.parse();
			System.out.println("json");
			System.out.println(json);
		} catch (org.apache.tomcat.util.json.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.resolve(500)).body("Failed to retrieve request data.");
		}
		
		if (json.containsKey("additional_email")) {
			System.out.println("He has emails! <3");
			try {
				LinkedHashMap<String, String> emails = (LinkedHashMap<String, String>) json.get("additional_email");
				System.out.println(emails.get(0));
				System.out.println(emails.size());
				System.out.println(emails.toString());
				boolean addedEmail = false;
				for (Entry<String, String> entry : emails.entrySet()) {
					String emailString = entry.getValue();
					System.out.println(entry.getKey());
					System.out.println(entry.getValue());
					if (!emailRepo.existsById(entry.getValue())) {
						Email newEmail = new Email(user, emailString, false);
						emailRepo.save(newEmail);
						addedEmail = true;
					} else if (emailRepo.findByEmail(emailString).getUser().getUserId() != profileId) {
						return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("Email address already registered to another user."));
					}
				}
				if (addedEmail) {
					return ResponseEntity.status(HttpStatus.resolve(201)).body("Successfully added email(s) to account.");
				} else {
					return ResponseEntity.status(HttpStatus.resolve(200)).body("No email(s) to add to account.");
				}
			} catch (Error e) {
				return ResponseEntity.status(HttpStatus.resolve(400)).body("Illformatted additional email list.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.resolve(400)).body("Missing additional email list.");
		}
		
		/*User user = userRepository.findById(profileId).get();
		int numEmails = emailRepo.getNumberOfEmails(user);
		if (numEmails < 5) {
			Email email = new Email(user, emailRequest.getEmail(), false);
			System.out.println("The email is:");
			System.out.println(emailRequest);
			//System.out.println(emailRequest.getAdditionalEmails());
			System.out.println(emailRequest.getEmail());
			System.out.println(emailRequest.getNumEmails());
			//return emailRequest.getAdditionalEmails();
			//emailRepo.save(email);
			return email;
		} else {
			return new ErrorResponse("Maximum email addresses reached (5)");
		}	*/
		

		//TODO Unreachable statement
		//return ResponseEntity.status(HttpStatus.resolve(201)).body("HI");

	}
	
	
	
	/*public ResponseEntity<?> updateEmails(@RequestBody NewEmailRequest credentials, HttpServletResponse response) throws NoSuchAlgorithmException {
		if (credentials.getEmail() == null) {
			System.out.println("=======================================================================================================================================================================================");
			return ResponseEntity.status(HttpStatus.resolve(400)).body(new ErrorResponse("Missing email and/or password field"));
		}
		System.out.println(credentials.getEmail());
		//System.out.println(credentials.getAdditional_Email());
		System.out.println("32143243=======================================================================================================================================================================================");

		if (emailRepo.existsById(credentials.getEmail())) {
			return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("Email address already registered"));
		}
		
		Email email = emailRepo.findByEmail(credentials.getEmail());
		//User user = email.getUser();
		/*String hashedRequestPassword = ChecksumUtils.hashPassword(user.getUserId(), credentials.getPassword());
		
		if (hashedRequestPassword.equals(user.getPassword())) {
			String token = ChecksumUtils.generateToken(user.getUserId());

			Session session = new Session(user, token, 
					Instant.now().plus(loginSeconds, ChronoUnit.SECONDS).atOffset(ZoneOffset.UTC));
			sessionRepo.save(session);
			
			return ResponseEntity.status(HttpStatus.resolve(201)).body(new LoginSuccessResponse(token, user.getUserId()));
		} else {
			return ResponseEntity.status(HttpStatus.resolve(401)).body(new ErrorResponse("Password is not correct"));
		}
		return ResponseEntity.status(HttpStatus.resolve(201)).body("HI");
	}*/
	/*public Object newEmailRequest(@RequestBody NewEmailRequest emailRequest, @PathVariable("profileId") long profileId,
			HttpServletRequest request) {
		System.out.println("The properties");
		System.out.println(request.getParameterMap());
		System.out.println(request.getParameterMap().size());
		System.out.println(request.getAttribute("additional_emails"));
		//System.out.println(emailRequest.getAdditionalEmails());
		System.out.println(emailRequest.getNumEmails());
		
		
		if (request.getAttribute("authenticatedid") == null) {
			return ResponseEntity.status(401).body(new ErrorResponse("You are not logged in"));
		}

		long id = (long) request.getAttribute("authenticatedid");
		
		if (id != profileId && id != -1) {
			return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("You do not have permission to edit this user"));
		}

		User user = userRepository.findById(profileId).get();
		int numEmails = emailRepo.getNumberOfEmails(user);
		if (numEmails < 5) {
			Email email = new Email(user, emailRequest.getEmail(), false);
			System.out.println("The email is:");
			System.out.println(emailRequest);
			//System.out.println(emailRequest.getAdditionalEmails());
			System.out.println(emailRequest.getEmail());
			System.out.println(emailRequest.getNumEmails());
			//return emailRequest.getAdditionalEmails();
			//emailRepo.save(email);
			return email;
		} else {
			return new ErrorResponse("Maximum email addresses reached (5)");
		}
	}*/
}