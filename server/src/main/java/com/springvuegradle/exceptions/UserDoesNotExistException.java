package com.springvuegradle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when user who's profile is requested does not exist
 * @author Olivia Mackintosh
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserDoesNotExistException extends Exception {

    private static final long serialVersionUID = 1L;

    public UserDoesNotExistException(String message) {
        super(message);
    }

    public UserDoesNotExistException(String message, Throwable t) {
        super(message, t);
    }
}
