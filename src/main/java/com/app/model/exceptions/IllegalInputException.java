package com.app.model.exceptions;

@SuppressWarnings("serial")
public class IllegalInputException extends AbstractException {
	public IllegalInputException(String msg) {
		super(msg);
		heading = "Unable to process your input.";
	}
}
