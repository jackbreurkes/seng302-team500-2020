package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.data.Profile;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdateFitnessRequest;
import com.springvuegradle.model.responses.ErrorResponse;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UpdateFitnessLevelContoller {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/updatefitness")
    @CrossOrigin
    public Object updateFitness(@RequestBody UpdateFitnessRequest updateRequest, HttpServletRequest request) throws NoSuchAlgorithmException {
        if (request.getAttribute("authenticatedid") == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("you must be authenticated"));
        }
        try {
        	User user = userRepository.findById(updateRequest.getUuid()).get();
        	if (profileRepository.existsById(user.getUserId())) {
        		Profile profile = profileRepository.getOne(user.getUserId());
        		profile.setFitness(updateRequest.getFitnessLevel());
        		profileRepository.save(profile);
        	}
        } catch (Exception e) {
            return new ErrorResponse("Cannot update fitness");
        }

        return "Updated Profile Fitness";
    }
}