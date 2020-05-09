package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ActivityTypeRepository;
import com.springvuegradle.model.repository.CountryRepository;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.LocationRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.responses.ErrorResponse;
import com.springvuegradle.model.responses.UserResponse;

/**
 * Process GET /whoami
 * @author Alex Hobson
 * @author Olivia Mackintosh
 */
@RestController
public class WhoAmIController {
	
	/**
     * Repository (database) of user credentials
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository (database) of email addresses
     */
    @Autowired
    private EmailRepository emailRepository;

    private final short SUPER_ADMIN_USER_PERMISSION = 127;
	
    /**
     * Calls checkStartUpAdmin method
     */
    @PostConstruct
    public void initSuperAdmin() {
    	
    	checkStartUpAdmin();
    	
    }
    
    /**
     * Checks whether there is a super admin (permission level above) already. If not, creates one.
     * Caution: If tests that use this class are run, this code will be run.
     * 		If not mocked, method will cause failures due to index out of bounds resulting from
     * 		getting the admin just "saved" which is not saved in testing as actual repo is not used.
     * @return User object representing the super admin in the system
     */
    public User checkStartUpAdmin() {
    	User superAdmin;
        if (userRepository.superAdminExists() == 0) {
            superAdmin = new User();
            superAdmin.setPermissionLevel(SUPER_ADMIN_USER_PERMISSION);
            userRepository.save(superAdmin);
            
            superAdmin = userRepository.getSuperAdmin().get(0);	// Will fail here in tests if no mocking is used
            try {
                superAdmin.setPassword("password");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            userRepository.save(superAdmin);

            Email superAdminEmail = new Email(superAdmin, "super@admin.com", true);
            emailRepository.save(superAdminEmail);
            emailRepository.findByEmail(superAdminEmail.getEmail()).setIsPrimary(true);
        } else {
            superAdmin = userRepository.getSuperAdmin().get(0);
        }
        
        return superAdmin;
    }

	/**
	 * Processes a /whoami GET request
	 * @param httpRequest Contains authentication information
	 * @return ResponseEntity depending on whether the user is logged in or not
	 */
	@GetMapping("/whoami")
	@CrossOrigin
	public Object whoAmI(HttpServletRequest httpRequest) {
		Long authId = (Long) httpRequest.getAttribute("authenticatedid");
		
		if (authId == null) {
			return ResponseEntity.status(401).body(new ErrorResponse("You are not logged in"));
		} else {
			return ResponseEntity.status(200).body(new UserResponse(authId));
		}
	}
}
