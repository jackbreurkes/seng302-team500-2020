package com.springvuegradle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * custom error to be thrown when a user is attempting to carry out an operation which is disallowed
 * (e.g. with invalid password, register too may emails)
 * @author Jack van Heugten Breurkes
 * @author Josh Yee
 * @author Olivia Mackintosh
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenOperationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ForbiddenOperationException(String message) {
        super(message);
    }

    public ForbiddenOperationException(String message, Throwable t) {
        super(message, t);
    }
}
