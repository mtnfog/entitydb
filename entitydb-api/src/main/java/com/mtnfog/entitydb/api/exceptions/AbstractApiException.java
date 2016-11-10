package com.mtnfog.entitydb.api.exceptions;

public abstract class AbstractApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AbstractApiException(String message, Throwable t) {
		super(message, t);
	}
	
	public AbstractApiException(String message) {
		super(message);
	}
	
}