package com.springvuegradle.endpoints;

import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
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

/**
 * Endpoint for the /profiles/{profile_id}/emails requests
 * @author Olivia Mackintosh
 */
@RestController
//@RequestMapping("/profiles/{profile_id}/emails")
public class NewEmailController {

	@Autowired
	private EmailRepository emailRepo;
	@Autowired
	private UserRepository userRepo;

	@PostMapping("/profiles/{profileId}/emails")
	@CrossOrigin
	public ResponseEntity<?> updateEmails(@RequestBody String raw, @PathVariable("profileId") long profileId, HttpServletResponse response) throws NoSuchAlgorithmException {
		System.out.println("========================================================================");				
		JSONParser parser = new JSONParser(raw);
		LinkedHashMap<String, Object> json = null;
		try {
			json = (LinkedHashMap<String, Object>) parser.parse();
		} catch (org.apache.tomcat.util.json.ParseException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.resolve(500)).body("Failed to retrieve request data.");
		}
		
		if (json.containsKey("additional_email")) {
			try {
				LinkedHashMap<String, String> emails = (LinkedHashMap<String, String>) json.get("additional_email");
				System.out.println(emails.size());
				Collection<String> newEmails = emails.values();
				if (emails.size() >= 5) {					// As this list does not include the primary email
					return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("Maximum email addresses is (5)"));
				} else {
					User user = userRepo.getOne(profileId);
					List<Email> nonPrimaryEmails = emailRepo.getNonPrimaryEmails(user);
					List<Email> deletedEmails = new ArrayList<Email>();
					for (Email oldEmail: nonPrimaryEmails) {
						if (newEmails.contains(oldEmail.getEmail())) {
							newEmails.remove(oldEmail);
							System.out.println("Kept in:" + oldEmail.getEmail());
						} else {
							emailRepo.delete(oldEmail);
							deletedEmails.add(oldEmail);
							System.out.println("Removed:" + oldEmail.getEmail());
						}
					}
					for (String newEmailString: newEmails) {
						emailRepo.save(new Email(user, newEmailString, false));
						System.out.println("Added:" + newEmailString);
					}
					
					return ResponseEntity.status(HttpStatus.resolve(201)).body("Successfully updated account emails.");					
					
					
					/*for (Entry<String, String> entry : emails.entrySet()) {
						String emailString = entry.getValue();
						System.out.println(entry.getValue());
						if (!emailRepo.existsById(entry.getValue())) {
							Email newEmail = new Email(user, emailString, false);
							emailRepo.save(newEmail);
						} else if (emailRepo.findByEmail(emailString).getUser().getUserId() != profileId) {
							return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("Email address " + emailString + " already registered to another user."));
						}*/
					}
				
				
				/*if (emailsAdded > 0) {
					return ResponseEntity.status(HttpStatus.resolve(201)).body("Successfully added " + emailsAdded + " email(s) to account.");
				} else if (emailsAdded == 0) {
					return ResponseEntity.status(HttpStatus.resolve(200)).body("No email(s) to add to account.");
				} else {
					return ResponseEntity.status(HttpStatus.resolve(200)).body("Successfully removed " + emailsAdded + " email(s) from account.");
				}*/
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