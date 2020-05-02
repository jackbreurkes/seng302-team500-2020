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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import com.springvuegradle.auth.ChecksumUtils;
import com.springvuegradle.exceptions.UserNotAuthenticatedException;
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
public class NewEmailController {

	@Autowired
	private EmailRepository emailRepo;
	@Autowired
	private UserRepository userRepo;

	@PostMapping("/profiles/{profileId}/emails")
	@CrossOrigin
	public ResponseEntity<?> updateEmails(@RequestBody String raw, @PathVariable("profileId") long profileId, HttpServletRequest request) throws NoSuchAlgorithmException, UserNotAuthenticatedException {
		User user = userRepo.getOne(profileId);
		
        // check correct authentication
        Long authId = (Long) request.getAttribute("authenticatedid");
        System.out.println("This is the authenticated id: " + authId);
        if (authId == null) {
            throw new UserNotAuthenticatedException("You must be an authenticated user.");
        }
        
        Optional<User> userRequesting = userRepo.findById(authId);
        if (userRequesting.isPresent() && (userRequesting.get().getPermissionLevel() > 120 || authId == profileId)) {
		
			System.out.println("Updating emails through post");
			
			LinkedHashMap<String, Object> json = null;
			try {
				json = getJson(raw);
			} catch (org.apache.tomcat.util.json.ParseException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.resolve(500)).body("Failed to retrieve request data.");
			}
			
			System.out.println(raw);
			
			if (json.containsKey("additional_email")) {
				try {
					ArrayList<String> newEmails = (ArrayList<String>) json.get("additional_email");
					System.out.println("The additional emails JSON is:");
					System.out.println(newEmails);
					
					if (newEmails.size() >= 5) {					// As this list does not include the primary email
						return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("Maximum email addresses is (5)"));
					} else {
						updateAdditionalEmails(user, newEmails);					
						return ResponseEntity.status(HttpStatus.resolve(201)).body("Successfully updated account emails.");					
						}
				} catch (Error e) {
					return ResponseEntity.status(HttpStatus.resolve(500)).body("Failed to update additional user emails.");
				}
			} else {
				return ResponseEntity.status(HttpStatus.resolve(400)).body("Missing additional email list.");
			}
        } else {
        	throw new AccessDeniedException("must be logged in as user or as admin to edit emails");
        }
	}
	
	@PutMapping("/profiles/{profileId}/emails")
	@CrossOrigin
	public ResponseEntity<?> updatePrimaryEmail(@RequestBody String raw, @PathVariable("profileId") long profileId, HttpServletResponse response) throws NoSuchAlgorithmException {
		User user = userRepo.getOne(profileId);
		
		LinkedHashMap<String, Object> json = null;
		try {
			json = getJson(raw);
		} catch (org.apache.tomcat.util.json.ParseException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.resolve(500)).body("Failed to retrieve request data.");
		}
		
		if (json.containsKey("additional_email")) {
			try {
				LinkedHashMap<String, String> emails = (LinkedHashMap<String, String>) json.get("additional_email");
				Collection<String> newEmails = emails.values();
				
				if (emails.size() >= 5) {					// As this list does not include the primary email
					return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("Maximum email addresses is (5)"));
				} else {
					
					if (json.containsKey("primary_email")) {
						String newPrimaryEmailString = (String) json.get("primary_email");
						
						if (emailRepo.existsById(newPrimaryEmailString)) {
							if (emailRepo.findByEmail(newPrimaryEmailString).getUser() == user) {
								System.out.println("It IS your email!");
								emailRepo.deleteById(newPrimaryEmailString);
							} else {
								return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("Email is already registered to another user!"));
							}
						}
						emailRepo.save(new Email(user, newPrimaryEmailString, true));
						updateAdditionalEmails(user, newEmails);
						return ResponseEntity.status(HttpStatus.resolve(201)).body("Successfully updated account emails.");					

					} else {
						return ResponseEntity.status(HttpStatus.resolve(403)).body("Incorrect method for updating additional emails. Use POST.");					
					}
				}
			} catch (Error e) {
				return ResponseEntity.status(HttpStatus.resolve(400)).body("Illformatted additional email list.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.resolve(400)).body("Missing additional email list.");
		}
	}
	
	private LinkedHashMap<String, Object> getJson(String raw) throws org.apache.tomcat.util.json.ParseException {
		JSONParser parser = new JSONParser(raw);
		LinkedHashMap<String, Object> json = null;
		json = (LinkedHashMap<String, Object>) parser.parse();
		return json;
	}
	
	
	private void updateAdditionalEmails(User user, Collection<String> newEmails) {
		System.out.println("Updateadditionalemaisl");
		System.out.println(newEmails);
		List<Email> nonPrimaryEmails = emailRepo.getNonPrimaryEmails(user);
		System.out.println(nonPrimaryEmails);
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
	}

}