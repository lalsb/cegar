package com.app.model.graph;

import org.graphstream.stream.SinkAdapter;

/**
 *  Custom SinkAdapter to handle node and edge events
 * @author Linus Alsbach
 */
public class ConsoleSink extends SinkAdapter {
	
	// Methods to handle events from Graph
    @Override
    public void nodeAdded(String sourceId, long timeId, String nodeId) {
        System.out.println("Node added: " + nodeId);
    }

    @Override
    public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId, boolean directed) {
        System.out.println("Edge added: " + edgeId + " (" + fromNodeId + " -> " + toNodeId + ")");
    }
    
    // Methods to handle events from Generator
    public void sendEdgeAdded(String edgeId, String fromNodeId, String toNodeId) {
        System.out.println("KripkeGraphGenerator - Edge added: " + edgeId + " (" + fromNodeId + " -> " + toNodeId + ")");
    }

    public void sendNodeAdded(String nodeId) {
        System.out.println("KripkeGraphGenerator - Node added: " + nodeId);
    }

    public void sendNodeAttributeAdded(String nodeId, String attribute, Object value) {
        System.out.println("KripkeGraphGenerator - Node attribute added: " + attribute + " to Node " + nodeId + " = " + value);
    }
}
