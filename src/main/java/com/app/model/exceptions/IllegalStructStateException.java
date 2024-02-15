package com.app.model.exceptions;

@SuppressWarnings("serial")
public class IllegalStructStateException extends AbstractException {
    public IllegalStructStateException(String msg) {
        super(msg);
        heading = "Unable to verify valid graph state.";
    }
}
