//package com.springvuegradle.endpoints;
//
//import java.security.NoSuchAlgorithmException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.springvuegradle.model.data.Email;
//import com.springvuegradle.model.data.Gender;
//import com.springvuegradle.model.data.Profile;
//import com.springvuegradle.model.data.User;
//import com.springvuegradle.model.repository.EmailRepository;
//import com.springvuegradle.model.repository.ProfileRepository;
//import com.springvuegradle.model.repository.UserRepository;
//import com.springvuegradle.model.requests.CreateUserRequest;
//import com.springvuegradle.model.responses.ErrorResponse;
//import com.springvuegradle.model.responses.ProfileCreatedResponse;
//import com.springvuegradle.util.FormValidator;
//
///**
// * Endpoint for the /createprofile request, Processes creating a profile
// *
// * @author Michael Freeman
// * @author Alex Hobson
// */
//@RestController
//public class CreateProfileController {
//
//	/**
//	 * Repository (database) of user credentials
//	 */
//	@Autowired
//	private UserRepository userRepository;
//
//	/**
//	 * Repository (database) of email addresses
//	 */
//	@Autowired
//	private EmailRepository emailRepository;
//
//	/**
//	 * Repository (database) of profiles
//	 */
//	@Autowired
//	private ProfileRepository profileRepository;
//
//	/**
//	 * Date format to parse
//	 */
//	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//	/**
//	 * Handle when user tries to POST to /createprofile
//	 */
//	@PostMapping("/profiles")
//	public Object createprofile(@RequestBody CreateUserRequest userRequest) throws NoSuchAlgorithmException {
//		String firstname = userRequest.getFirstname();
//		String middlename = userRequest.getMiddlename();
//		String lastname = userRequest.getLastname();
//		String nickname = userRequest.getNickname();
//		String bio = userRequest.getBio();
//		String email = userRequest.getPrimaryEmail();
//
//		// validate inputs
//		if (!FormValidator.validateName(firstname, true)) {
//			return ResponseEntity.badRequest().body(new ErrorResponse("Illegal first name"));
//		}
//
//		if (!FormValidator.validateName(middlename, false)) {
//			return ResponseEntity.badRequest().body(new ErrorResponse("Illegal middle name"));
//		} else if (middlename == null) {
//			middlename = "";
//		}
//
//		if (!FormValidator.validateName(lastname, true)) {
//			return ResponseEntity.badRequest().body(new ErrorResponse("Illegal last name"));
//		}
//
//		if (!FormValidator.validateNickname(nickname, false)) {
//			return ResponseEntity.badRequest().body(new ErrorResponse("Illegal nickname"));
//		} else if (nickname == null) {
//			nickname = "";
//		}
//
//		if (!FormValidator.validateEmail(email, true)) {
//			return ResponseEntity.badRequest().body(new ErrorResponse("Illegal email address"));
//		}
//
//		if (!FormValidator.validateBio(bio, false)) {
//			return ResponseEntity.badRequest().body(new ErrorResponse("Illegal bio"));
//		} else if (bio == null) {
//			bio = "";
//		}
//
//		Date dob;
//		try {
//			dob = dateFormat.parse(userRequest.getDateOfBirth());
//		} catch (ParseException ex) {
//			return ResponseEntity.badRequest().body(new ErrorResponse("Illegal date of birth"));
//		}
//
//		Gender gender = Gender.matchGender(userRequest.getGender());
//
//		if (gender == null) {
//			return ResponseEntity.badRequest().body(new ErrorResponse("Illegal gender"));
//		}
//
//		String password = userRequest.getPassword();
//
//		if (password == null || password.length() < 8) {
//			return ResponseEntity.badRequest().body(new ErrorResponse("Illegal password"));
//		}
//
//		if (emailRepository.existsById(email)) {
//			return ResponseEntity.status(HttpStatus.resolve(403)).body(new ErrorResponse("Account with specified email address already exists"));
//		}
//
//		User user = new User();
//		Profile profile = new Profile(user, firstname, lastname, dob, gender);
//
//		profile.setBio(bio);
//		profile.setMiddleName(middlename);
//		profile.setNickName(nickname);
//
//		// workaround since userid is not known until saved to the DB
//		userRepository.save(user);
//		user.setPassword(password);
//		userRepository.save(user);
//
//		Email dbemail = new Email(user, email, true);
//		emailRepository.save(dbemail);
//
//		profileRepository.save(profile);
//
//		return ResponseEntity.status(HttpStatus.resolve(201)).body(new ProfileCreatedResponse(user.getUserId()));
//	}
//
//}
