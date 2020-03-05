package com.springvuegradle.endpoints;

import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.ProfileRepository;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.CreateUserRequest;
import com.springvuegradle.model.requests.UpdateFitnessRequest;
import com.springvuegradle.model.responses.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
public class UpdateFitnessLevelContoller {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/updatefitness")
    public Object updateFitness(@RequestBody UpdateFitnessRequest updateRequest) throws NoSuchAlgorithmException {
        try {
            profileRepository.updateFitness(userRepository.findById(updateRequest.getUuid()).get(), updateRequest.getFitnessLevel());
        } catch (Exception e) {
            return new ErrorResponse("Cannot update fitness");
        }

        return "Updated Profile Fitness";
    }
}