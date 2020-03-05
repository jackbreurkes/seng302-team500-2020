package com.springvuegradle.auth;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springvuegradle.model.repository.EmailRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

	/**
	 * Email repository
	 */
    @Autowired
    private EmailRepository emailRepository;

    /**
     * Loads a user's credentials (UserDetails), including their email and hashed password from database
     * @param email Provided user's email address
     * @return UserDetails object containing email address and hashed password
     * @throws UsernameNotFoundException if the email address is not registered
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //Load the user from DB and pass into a springboot class
    	if (!emailRepository.existsById(email)) {
    		throw new UsernameNotFoundException("User "+email+" does not exist");
    	}
    	com.springvuegradle.model.data.User user = emailRepository.findByEmail(email).getUser();
        return new User(email, user.getPassword(), new ArrayList<>()); //TODO give admin users privelidge
    }
}
