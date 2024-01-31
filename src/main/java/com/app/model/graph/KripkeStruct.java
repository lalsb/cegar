package com.app.model.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;

import com.app.model.exceptions.KripkeStructureInvalidException;
import com.app.model.framework.ModelManager;
import com.app.model.framework.Tuple;
import com.app.model.framework.Variable;

import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;

import org.graphstream.ui.javafx.FxGraphRenderer;

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
	public SmartGraphPanel<String, String> getSmartGraphView() {
		return SmartGraphWrapper.getInstance().generateJavaFXView(this);
		
	}
	
	public FxViewPanel getGraphStreamView() {

		setAttribute("ui.quality");
		nodes().forEach(node -> node.setAttribute("ui.label", node.getId()));
		nodes().forEach(node -> node.setAttribute("ui.style", "text-alignment: at-right; text-size: 20; text-mode: normal;"));
		FxViewer v = new FxViewer(this, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		
		v.enableAutoLayout();
		FxViewPanel panel = (FxViewPanel)v.addDefaultView(false, new FxGraphRenderer());
	
		
		setAttribute("ui.antialias");
		setAttribute("ui.quality");
		return panel;
		
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

	public Set<Tuple> getInitialTuples() {
		
		Set<Tuple> ret = new HashSet<Tuple>();
		Tuple t = new Tuple();
		
		ModelManager.getvariablesMap().forEach((k,v) -> t.put(k, v.getValue()));
		ret.add(t);
		
		return ret;
	}
}
