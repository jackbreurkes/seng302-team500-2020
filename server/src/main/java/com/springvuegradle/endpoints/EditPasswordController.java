package com.springvuegradle.endpoints;

import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.UserRepository;
import com.springvuegradle.model.requests.UpdatePasswordRequest;
import com.springvuegradle.model.responses.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

@RestController
public class EditPasswordController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/editpassword")
    @CrossOrigin
    public Object editPassword(@RequestBody UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) throws NoSuchAlgorithmException {
        if (request.getAttribute("authenticatedid") == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("you must be authenticated"));
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
