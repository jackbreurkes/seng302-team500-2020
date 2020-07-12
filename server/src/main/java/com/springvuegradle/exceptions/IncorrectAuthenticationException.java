package com.springvuegradle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * custom error to be thrown when a user is authenticated but does not have permission to
 * complete the request.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class IncorrectAuthenticationException extends Exception {

    private static final long serialVersionUID = 1L;

    public IncorrectAuthenticationException(String message) {
        super(message);
    }

    public IncorrectAuthenticationException(String message, Throwable t) {
        super(message, t);
    }
}
