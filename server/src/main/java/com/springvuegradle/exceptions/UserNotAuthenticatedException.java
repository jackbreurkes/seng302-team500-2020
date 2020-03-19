package com.springvuegradle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * custom error to be thrown when a user is not authenticated
 * @author Jack van Heugten Breurkes
 * @author Josh Yee
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UserNotAuthenticatedException extends Exception {

    private static final long serialVersionUID = 1L;

    public UserNotAuthenticatedException(String message) {
        super(message);
    }

    public UserNotAuthenticatedException(String message, Throwable t) {
        super(message, t);
    }
}
