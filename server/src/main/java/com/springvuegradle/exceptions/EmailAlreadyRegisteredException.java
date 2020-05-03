package com.springvuegradle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when email is already registered to another user
 * @author Olivia Mackintosh
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class EmailAlreadyRegisteredException extends Exception {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }

    public EmailAlreadyRegisteredException(String message, Throwable t) {
        super(message, t);
    }
}
