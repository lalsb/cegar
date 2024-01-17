package com.app.model.exceptions;

@SuppressWarnings("serial")
public class TransitionBlockInvalidException extends RuntimeException {
    public TransitionBlockInvalidException(String msg) {
        super(msg);
    }
}
