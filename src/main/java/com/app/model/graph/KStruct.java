package com.app.model.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.app.model.exceptions.IllegalStructStateException;
import com.app.model.framework.Tuple;
import com.app.model.framework.Variable;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;

/**
 * An implementation of a Kripke structures which is a directed graph whose vertices are labeled by a set of atomic propositions.
 * 
 * Requires the JGrapht graph implementation DiGraph which supports directed edges between two nodes.
 * 
 * @author Linus Alsbach
 *
 */
public abstract class KStruct<T extends KState> extends DigraphEdgeList<T, String> {


	//TODO: graphView.getStylableVertex("1").setStyleClass("init-vertex");
	/**
	 * Map of variable id and variable
	 */
	private Map<String, Variable> vars;

	public KStruct(Variable ...variables) {
		super();

		// Fill map
		vars = new HashMap<String, Variable>();	
		Arrays.asList(variables).forEach(x -> {vars.put(x.getId(), x);});
	}

	public abstract Vertex<T> getNode(Tuple tuple);

	/**
	 * Generate a JavaFX viewable graph using SmartGraphWrapper.
	 * @return
	 */
	public SmartGraphPanel<T, String> getSmartGraphView() {

		validate();
	
		SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
		SmartGraphPanel<T, String> graphPanel = new SmartGraphPanel<T, String>(this, strategy);
		graphPanel.setAutomaticLayout(true);
		
		vertices().forEach(v -> {if(v.element().isInitial()) {graphPanel.getStylableVertex(v).setStyleClass("init-vertex");
		}});

		return graphPanel;
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
	public void validate() {
		if (numVertices() == 0) {
			throw new IllegalStructStateException("Set of states S is empty.");
		}

		validateLeftTotalRelation();

		System.out.println("The Kripke structure satifies all formal properties.");
	}

	/**
	 * Validation method
	 * @return
	 */
	private void validateLeftTotalRelation() {

		for(Vertex<T> v: vertices()) {
			if(outboundEdges(v).isEmpty()) {
				throw new IllegalStructStateException(String.format("State %s does not satisfy the left total property.", v.element().getId()));
			}
		}
	}
}
