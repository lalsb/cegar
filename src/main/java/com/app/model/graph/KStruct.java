package com.app.model.graph;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.app.model.exceptions.ModelStateException;
import com.app.model.framework.Variable;
import com.app.ui.Main;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartGraphVertexNode;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartStylableNode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

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
		
		URI uri = null;
		URI uri2 = null;
		InputStream inputStream = null;
		
		try {
			uri = Main.class.getClassLoader().getResource("smartgraph.css").toURI();
			uri2 = Main.class.getClassLoader().getResource("smartgraph.properties").toURI();
			inputStream = uri2.toURL().openStream();
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		
		
		SmartGraphProperties properties = new SmartGraphProperties(inputStream);

		SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
		SmartGraphPanel<T, String> graphPanel = new SmartGraphPanel<T, String>(this, properties, strategy, uri);
		graphPanel.setAutomaticLayout(true);

		vertices().forEach(v -> {

				SmartStylableNode node = graphPanel.getStylableVertex(v);
				
				if(v.element().isInitial()) {
				node.setStyleClass("init-vertex");
				}
			});

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
			throw new ModelStateException("set of states is empty");
		}
		
		validateLeftTotalRelation();

		System.out.println("checked kripke properties");
	}

	/**
	 * Validation method
	 * @return
	 */
	private void validateLeftTotalRelation() {

		for(Vertex<T> v: vertices()) {
			if(outboundEdges(v).isEmpty()) {
				throw new ModelStateException(String.format("state %s does not satisfy the left total property", v.element().getId()));
			}
		}
	}
}
