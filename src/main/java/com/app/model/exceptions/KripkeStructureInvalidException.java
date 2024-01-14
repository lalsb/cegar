package com.app.model.exceptions;

@SuppressWarnings("serial")
public class KripkeStructureInvalidException extends RuntimeException {
    public KripkeStructureInvalidException(String msg) {
        super(msg);
    }
}
