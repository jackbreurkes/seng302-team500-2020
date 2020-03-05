package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;

import com.springvuegradle.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.auth.ChecksumUtils;
import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.requests.NewEmailRequest;
import com.springvuegradle.model.responses.ErrorResponse;

@RestController
public class NewEmailController {
	
	@Autowired
	private EmailRepository emailRepo;
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/editemail")
	public Object newEmailRequest(@RequestBody NewEmailRequest credentials) {
		int numEmails = emailRepo.getNumberOfEmails(credentials.getUser());
		if (numEmails < 5) {
			Email email = new Email(userRepository.getOne(credentials.getUser()), credentials.getEmail(), false);
			emailRepo.save(email);
			return email;
		} else {
			return new ErrorResponse("Maximum email addresses reached (5)");
		}
	}
}