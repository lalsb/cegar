package com.app.model.exceptions;

@SuppressWarnings("serial")
public class ModelStateException extends ModelException {
    public ModelStateException(String msg) {
        super(msg);
        heading = "Unable to verify valid graph state.";
    }
    
    public ModelStateException(String msg, Throwable cause) {
        super(msg, cause);
        heading = "Unable to verify valid graph state.";
    }
}
