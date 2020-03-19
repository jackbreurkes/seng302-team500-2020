package com.springvuegradle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * custom error to be thrown when a user's password is invalid.
 * should only be used for fields that should be the user's current password (not new passwords etc)
 * @author Jack van Heugten Breurkes
 * @author Josh Yee
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class InvalidPasswordException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable t) {
        super(message, t);
    }
}
