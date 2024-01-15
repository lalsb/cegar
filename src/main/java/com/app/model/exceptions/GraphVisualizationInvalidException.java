package com.app.model.exceptions;

@SuppressWarnings("serial")
public class GraphVisualizationInvalidException extends  RuntimeException {
    public GraphVisualizationInvalidException(String msg) {
        super(msg);
    }
}
