package com.app.model.graph;

import java.util.HashSet;
import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import com.app.model.exceptions.KripkeStructureInvalidException;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;

/**
 * An implementation of a Kripke structures which is a directed graph whose vertices are labeled by a set of atomic propositions.
 * 
 * Requires the GraphStream graph implementation Multigraph which supports multiple edges between two nodes.
 * 
 * @author Linus Alsbach
 *
 */
public class KripkeStruct extends MultiGraph{
	
	private Set<Node> initialStates;

	public KripkeStruct(String id) {
		super(id);
		initialStates = new HashSet<Node>();
	}
	
	public boolean isValid() {
        try {
            validate();
        } catch (RuntimeException e) {
        	e.printStackTrace();
            return false;
        }
        return true;
    }

    public void validate() {
        if (this.getNodeCount() == 0) {
            throw new KripkeStructureInvalidException("Set of states S is empty.");
        }

        if (initialStates.isEmpty()) {
            throw new KripkeStructureInvalidException("Set of initial states I is empty.");
        }

        validateLeftTotalRelation();
        
        System.out.println("The Kripke structure satifies all formal properties.");
    }
    
    public void addInitialStates(Node ...nodes) {
    	for(Node node: nodes) {
    		initialStates.add(node);
    		}
    	}
    
    public void removeInitialStates(Node ...nodes) {
    	for(Node node: nodes) {
    		initialStates.remove(node);
    		}
    	}

    public Set<Node> getInitialStates() {
        return initialStates;
    }
    
    public Set<Node> getStates() {
    	
    	Set<Node> nodeSet = new HashSet<>();   	
		this.nodes().forEach(nodeSet::add);
        return nodeSet; 
    }
    
    public Set<Edge> getTransitions() {
    	
    	Set<Edge> edgeSet = new HashSet<>();   	
		this.edges().forEach(edgeSet::add);
        return edgeSet; 
    }
    
    public Set<Node> getImage(Node node) {
    	Set<Node> imageSet = new HashSet<>(); 
    	node.neighborNodes().forEach(imageSet::add);
    	return imageSet;
    }
    
	public SmartGraphPanel<String, String> generateVisuals() {
		return SmartGraphWrapper.getInstance().generateJavaFXView(this);
		
	}

    private void validateLeftTotalRelation() {
    	
		for(Node node: this) {
			if(!node.leavingEdges().iterator().hasNext()) {
				throw new KripkeStructureInvalidException(String.format("State %s does not satisfy the left total property.", node));
				}
		}
    }
}
