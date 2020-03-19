package com.springvuegradle.endpoints;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdatePrimaryEmailRequest;
import com.springvuegradle.model.responses.ErrorResponse;

@RestController
public class UpdatePrimaryEmailController {
	
	@Autowired
	private EmailRepository emailRepo;
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/editprimaryemail")
	@CrossOrigin
	public Object UpdatePrimaryEmail(@RequestBody UpdatePrimaryEmailRequest credentials, String newAddress, HttpServletRequest request) {
		if (request.getAttribute("authenticatedid") == null) {
			return ResponseEntity.status(401).body(new ErrorResponse("You are not logged in"));
		}
		int numEmails = emailRepo.getNumberOfEmails(credentials.getUser());
		String oldAddress = emailRepo.getPrimaryEmail(userRepository.getOne(credentials.getUser()));
		if(oldAddress.equals(credentials.getNewPrimaryEmail())){
			//Attempting to make the current primary email the new primary email
			return new ErrorResponse("Old email address is same as new email address.");
		} else if (numEmails == 1) {
			//if there is only one email associated with the account then it must be primary
			return new ErrorResponse("Only one email address associated with this account. This address must be primary.");
		} else {
			//Update new primary and make old primary false
			try {
				emailRepo.findByEmail(oldAddress).setIsPrimary(false);
				emailRepo.findByEmail(credentials.getNewPrimaryEmail()).setIsPrimary(true);
				return true;
			}catch (Exception e){
				//there was an error updating the primary email
				return new ErrorResponse("There was an error updating the primary email, please ensure you have entered it correctly");
			}
		}
	}
}