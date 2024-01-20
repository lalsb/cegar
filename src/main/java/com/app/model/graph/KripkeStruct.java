package com.app.model.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import com.app.model.exceptions.KripkeStructureInvalidException;
import com.app.model.framework.Variable;

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
	
	/**
	 * Map of variable id and variable
	 */
	private Map<String, Variable> vars;

	public KripkeStruct(String id, Variable ...variables) {
		super(id);
		
		// Fill map
		vars = new HashMap<String, Variable>();	
		Arrays.asList(variables).forEach(x -> {vars.put(x.getId(), x);});
	}
	
	/**
	 * Generate a JavaFX viewable graph using SmartGraphWrapper.
	 * @return
	 */
	public SmartGraphPanel<String, String> generateVisuals() {
		return SmartGraphWrapper.getInstance().generateJavaFXView(this);
		
	}

	/**
	 * For a certain value check if a value is allowed or disallowed.
	 * @param variable Variable
	 * @param value	double
	 * @return 
	 */
	public boolean isInBounds(String variable, double value) {
		return vars.get(variable).isInBounds(value);
	}
	
	/**
	 * Validation method
	 * @return
	 */
	public boolean isValid() {
        try {
            validate();
        } catch (RuntimeException e) {
        	e.printStackTrace();
            return false;
        }
        return true;
    }

	/**
	 * Validation method
	 * @return
	 */
    public void validate() {
        if (this.getNodeCount() == 0) {
            throw new KripkeStructureInvalidException("Set of states S is empty.");
        }
        
        validateLeftTotalRelation();
        
        System.out.println("The Kripke structure satifies all formal properties.");
    }

    /**
	 * Validation method
	 * @return
	 */
    private void validateLeftTotalRelation() {
    	
		for(Node node: this) {
			if(!node.leavingEdges().iterator().hasNext()) {
				throw new KripkeStructureInvalidException(String.format("State %s does not satisfy the left total property.", node));
				}
		}
    }
}
