package com.springvuegradle.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.springvuegradle.model.responses.ErrorResponse;

/**
 * handles exceptions thrown by endpoints globally.
 * @author Michael Freeman
 * @author Jack van Heugten Breurkes
 * @author Josh Yee
 */
@ControllerAdvice
public class ExceptionHandlerController {

	/**
	 * catches all NoHandlerFoundException thrown by endpoints and returns an ErrorResponse with code 405.
	 * @return ErrorResponse object with message equal to the exception message
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	@ResponseBody
	public ErrorResponse requestHandlingNoHandlerFound() {
		return new ErrorResponse("404 REST Endpoint not found");
	}
	
	/**
	 * catches all HttpRequestMethodNotSupportedException thrown by endpoints and returns an ErrorResponse with code 405.
	 * @return ErrorResponse object with message equal to the exception message
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	@ResponseBody
	public ErrorResponse requestHandlingBadMethod() {
		return new ErrorResponse("405 HTTP Method not allowed");
	}
	
	/**
	 * catches all HttpMessageNotReadableExceptions thrown by endpoints and returns an ErrorResponse with code 400.
	 * @return ErrorResponse object with message equal to the exception message
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<?> requestHandlingRequestData() {
		return ResponseEntity.badRequest().build();
	}
	
	/**
	 * catches all AccessDeniedExceptions thrown by endpoints and returns an ErrorResponse with code 403.
	 * @return ErrorResponse object with message equal to the exception message
	 */
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorResponse requestHandlingNotLoggedIn() {
		return new ErrorResponse("403 Forbidden");
	}

	/**
	 * catches all InvalidRequestFieldExceptions thrown by endpoints and returns an ErrorResponse with code 400.
	 * @param exception the exception object thrown by a method
	 * @return ErrorResponse object with message equal to the exception message
	 */
	@ExceptionHandler(InvalidRequestFieldException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponse requestHandlingInvalidField(InvalidRequestFieldException exception) {
		return new ErrorResponse(exception.getMessage());
	}

	/**
	 * catches all RecordNotFoundExceptions thrown by endpoints and returns an ErrorResponse with code 404.
	 * @param exception the exception object thrown by a method
	 * @return ErrorResponse object with message equal to the exception message
	 */
	@ExceptionHandler(RecordNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorResponse requestHandlingInvalidField(RecordNotFoundException exception) {
		return new ErrorResponse(exception.getMessage());
	}

	/**
	 * catches all ForbiddenOperationExceptions thrown by endpoints and returns an ErrorResponse with code 409.
	 * @param exception the exception object thrown by a method
	 * @return ErrorResponse object with message equal to the exception message
	 */
	@ExceptionHandler(ForbiddenOperationException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	@ResponseBody
	public ErrorResponse requestHandlingInvalidField(ForbiddenOperationException exception) {
		return new ErrorResponse(exception.getMessage());
	}

	/**
	 * catches all UserNotAuthenticatedExceptions thrown by endpoints and returns an ErrorResponse with code 401.
	 * @param exception the exception object thrown by a method
	 * @return ErrorResponse object with message equal to the exception message
	 */
	@ExceptionHandler(UserNotAuthenticatedException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ErrorResponse requestHandlingInvalidField(UserNotAuthenticatedException exception) {
		return new ErrorResponse(exception.getMessage());
	}

	/**
	 * catches any UserNotAuthorizedException thrown by endpoints and returns an ErrorResponse with code 403.
	 * @param exception the exception object thrown by a method
	 * @return ErrorResponse object with message equal to the exception message
	 */
	@ExceptionHandler(UserNotAuthorizedException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	@ResponseBody
	public ErrorResponse requestHandlingInvalidField(UserNotAuthorizedException exception) {
		return new ErrorResponse(exception.getMessage());
	}
	
}