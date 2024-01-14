package com.app.util;

import org.graphstream.graph.Graph;

import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;

public final class SmartGraphWrapper {

    private static SmartGraphWrapper INSTANCE;
    
    private SmartGraphWrapper() {        
    }
    
    public static SmartGraphWrapper getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SmartGraphWrapper();
        }
        
        return INSTANCE;
    }

    private SmartGraphPanel generateJavaFXView(Graph graph) {
    	
		return null;
    	
    }
}
