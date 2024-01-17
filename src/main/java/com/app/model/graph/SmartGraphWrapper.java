package com.app.model.graph;

import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;

import com.app.model.exceptions.GraphVisualizationInvalidException;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;

public final class SmartGraphWrapper {

    private static SmartGraphWrapper INSTANCE;
    private static final Integer NODE_LIMIT = 50;
    private Graph<String, String> smartGraph;
    private org.graphstream.graph.Graph regularGraph;
    
    private SmartGraphWrapper() {        
    }
    
    public static SmartGraphWrapper getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SmartGraphWrapper();
        }
        
        return INSTANCE;
    }

    public SmartGraphPanel<String, String> generateJavaFXView(org.graphstream.graph.Graph regularGraph) {
    	
    	smartGraph = new GraphEdgeList<>();
    	this.regularGraph = regularGraph;
    	
    	inspect();
    	fill();
    	
    	SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
		SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(smartGraph, strategy);
		
		graphView.setAutomaticLayout(true);
    	
		return graphView;
    	
    }

	private void inspect() {
		
		if(!regularGraph.nodes().iterator().hasNext()) {
			throw new GraphVisualizationInvalidException("GraphStream: Set of states is empty.");
		}
		
		if(regularGraph.nodes().count() > NODE_LIMIT) {
			throw new GraphVisualizationInvalidException(String.format("GraphStream: Numbes of states exceeds %d.", NODE_LIMIT));
		}
		
	}

	private void fill() {
		
		for(Node node: regularGraph) {
			smartGraph.insertVertex(node.getId());
		}
		
		for(Edge edge: regularGraph.edges().toList()) {
			smartGraph.insertEdge(edge.getSourceNode().getId(), edge.getTargetNode().getId(), edge.getId());
		}
		
		validate();
	}

	private void validate() {
		
		if(smartGraph.vertices().isEmpty()) {
			throw new GraphVisualizationInvalidException("SmartGraph: Set of states is empty.");
		}
		
		if(smartGraph.numVertices()  > NODE_LIMIT) {
			throw new GraphVisualizationInvalidException(String.format("SmartGraph: Numbes of states exceeds %d.", NODE_LIMIT));
		}
		
	}
}
