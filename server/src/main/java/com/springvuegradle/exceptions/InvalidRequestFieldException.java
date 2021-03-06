package com.springvuegradle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * custom error to be thrown when a record could not be found
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidRequestFieldException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidRequestFieldException(String message) {
        super(message);
    }

    public InvalidRequestFieldException(String message, Throwable t) {
        super(message, t);
    }
}
