package com.springvuegradle.endpoints;

import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdatePasswordRequest;
import com.springvuegradle.model.responses.ErrorResponse;

@RestController
public class EditPasswordController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/editpassword")
    @CrossOrigin
    public Object editPassword(@RequestBody UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) throws NoSuchAlgorithmException {
        if (request.getAttribute("authenticatedid") == null) {
        	return ResponseEntity.status(401).body(new ErrorResponse("You are not logged in"));
        }

        User tempUser = userRepository.findById(updatePasswordRequest.getProfile_id()).get();
        if(!tempUser.getPassword().equals(updatePasswordRequest.getOldPassword())){
            return new ErrorResponse("Unable to authenticate password");
        }else if(!updatePasswordRequest.getNewPassword().equals(updatePasswordRequest.getRepeatPassword())){
            return new ErrorResponse("Passwords are not the same");
        } else {
            //here we update
            tempUser.setPassword(updatePasswordRequest.getNewPassword());
            return true;
        }

    }


}
