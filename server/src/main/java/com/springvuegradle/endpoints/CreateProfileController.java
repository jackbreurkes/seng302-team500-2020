package com.springvuegradle.endpoints;


import com.springvuegradle.model.data.Email;
import com.springvuegradle.model.data.Gender;
import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.CreateUserRequest;
import com.springvuegradle.model.requests.LoginRequest;
import com.springvuegradle.model.responses.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

/**
 * Endpoint for the /createprofile request
 * @author Michael Freeman
 */
@RestController
public class CreateProfileController {

    //The autowired user repository
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
        //parse the json into a new profile object
        Profile tempProfile;
        User tempUser = new User();

        //if information is invalid then correct error code is returned
        try {
            tempUser.setPassword(userRequest.getPassword());

            Gender tempGender;

            if(userRequest.getGender().equals("male")){
                tempGender = Gender.MALE;
            } else if(userRequest.getGender().equals("female")){
                tempGender = Gender.FEMALE;
            }else {
                tempGender = Gender.NON_BINARY;
            }

            tempProfile = new Profile(tempUser, userRequest.getFname(), userRequest.getLname(), userRequest.getDateOfBirth(), tempGender);
            tempProfile.setBio(userRequest.getBio());
            tempProfile.setMiddleName(userRequest.getMname());
            tempProfile.setNickName(userRequest.getNickname());
            Email tempEmail = new Email(tempUser, userRequest.getEmail(), true);

            userRepository.save(tempUser);
            emailRepository.save(tempEmail);
            profileRepository.save(tempProfile);
        } catch (Exception e){
            return new ErrorResponse("Invalid Profile Request" + e.toString());
        }

        return tempProfile;
    }

}
