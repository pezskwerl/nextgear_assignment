package finley.peter.nextgear.web.controller;

import org.springframework.http.HttpStatus;

/**
 * Object used to return error information back to the client when 
 * errors occur.
 */
public class ErrorInformation {

	private int status;
	private String error;
	private String message;
	private String exception;
	
	public ErrorInformation() {
	}
	
	public ErrorInformation(Exception exception, HttpStatus status) {
		this.message = exception.getMessage();
		this.exception = exception.getClass().getName();
		this.status = status.value();
		this.error = status.getReasonPhrase();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
