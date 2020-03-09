package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.CreateUserRequest;
import com.springvuegradle.model.responses.ErrorResponse;

/**
 * Endpoint for the /createprofile request
 * 
 * @author Michael Freeman
 */
@RestController
public class CreateProfileController {

	// The autowired user repository
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailRepository emailRepository;

	@Autowired
	private ProfileRepository profileRepository;

	/**
	 * Handle when user tries to POST to /createprofile
	 */
	@PostMapping("/createprofile")
	public Object createprofile(@RequestBody CreateUserRequest userRequest) throws NoSuchAlgorithmException {
		// parse the json into a new profile object
		Profile tempProfile;
		User tempUser = new User();

		// if information is invalid then correct error code is returned
		try {
			String email = userRequest.getPrimaryEmail();
			if (emailRepository.existsById(email)) {
				return new ErrorResponse("Account with specified email address already exists");
			}

			Gender tempGender;

			if (userRequest.getGender().equals("male")) {
				tempGender = Gender.MALE;
			} else if (userRequest.getGender().equals("female")) {
				tempGender = Gender.FEMALE;
			} else {
				tempGender = Gender.NON_BINARY;
			}

			tempProfile = new Profile(tempUser, userRequest.getFirstname(), userRequest.getLastname(),
					userRequest.getDateOfBirth(), tempGender);
			tempProfile.setBio(userRequest.getBio());
			tempProfile.setMiddleName(userRequest.getMiddlename());
			tempProfile.setNickName(userRequest.getNickname());

			Email tempEmail = new Email(tempUser, email, true);

			userRepository.save(tempUser);
			tempUser.setPassword(userRequest.getPassword());
			userRepository.save(tempUser);

			emailRepository.save(tempEmail);
			profileRepository.save(tempProfile);
		} catch (InvalidDataAccessApiUsageException | NullPointerException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return tempProfile.getUser().getUserId();
	}

}
