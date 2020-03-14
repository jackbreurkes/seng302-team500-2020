package com.springvuegradle.endpoints;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.data.Email;
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
	public Object newEmailRequest(@RequestBody NewEmailRequest credentials, @PathVariable("profileId") long profileId,
			HttpServletRequest request) {
		if (request.getAttribute("authenticatedid") == null) {
			return ResponseEntity.badRequest()
					.body(new ErrorResponse("you must be authenticated"));
		}

		long id = (long) request.getAttribute("authenticatedid");
		
		if (id != profileId && id != -1) {
			return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("You do not have permission to edit this user"));
		}
		
		int numEmails = emailRepo.getNumberOfEmails(profileId);
		if (numEmails < 5) {
			Email email = new Email(userRepository.getOne(credentials.getUser()), credentials.getEmail(), false);
			emailRepo.save(email);
			return email;
		} else {
			return new ErrorResponse("Maximum email addresses reached (5)");
		}
	}
}