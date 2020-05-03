package com.springvuegradle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when user exceeds the maximum number of emails for a profile
 * @author Olivia Mackintosh
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class MaximumEmailsException extends Exception {

    private static final long serialVersionUID = 1L;

    public MaximumEmailsException(String message) {
        super(message);
    }

    public MaximumEmailsException(String message, Throwable t) {
        super(message, t);
    }
}
