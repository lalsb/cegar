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
		System.out.print("[ConsoleSink] Node #" + nodeId + " = ");
	}

	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId, boolean directed) {
		System.out.println("[ConsoleSink] Edge #" + edgeId + " = (" + fromNodeId + " -> " + toNodeId + ")");
	}
	
	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		System.out.println(value);
	}
}
