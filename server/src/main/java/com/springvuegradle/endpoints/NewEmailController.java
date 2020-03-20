package com.springvuegradle.endpoints;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.NewEmailRequest;
import com.springvuegradle.model.responses.ErrorResponse;

@RestController
public class NewEmailController {

	@Autowired
	private EmailRepository emailRepo;
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/profiles/{profileId}/emails")
	@CrossOrigin
	public ResponseEntity<?> newEmailRequest(@RequestBody NewEmailRequest emailRequest, @PathVariable("profileId") long profileId,
			HttpServletRequest request) {
		System.out.println("The properties");
		System.out.println(request.getParameterMap());
		System.out.println(request.getParameterMap().size());
		System.out.println(request.getAttribute("additional_emails"));
		System.out.println(emailRequest.getAdditionalEmails());
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
			System.out.println(emailRequest.getAdditionalEmails());
			System.out.println(emailRequest.getEmail());
			System.out.println(emailRequest.getNumEmails());
			return emailRequest.getAdditionalEmails();
			//emailRepo.save(email);
			//return email;
		} else {
			return new ErrorResponse("Maximum email addresses reached (5)");
		}
	}
}