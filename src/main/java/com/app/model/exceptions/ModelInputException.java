package com.app.model.exceptions;

@SuppressWarnings("serial")
public class ModelInputException extends ModelException {
	public ModelInputException(String msg) {
		super(msg);
		heading = "Unable to process your input.";
	}
	
    public ModelInputException(String msg, Throwable cause) {
        super(msg, cause);
        heading = "Unable to process your input.";
    }
}
