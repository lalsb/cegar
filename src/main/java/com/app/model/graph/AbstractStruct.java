package com.app.model.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.app.model.framework.ModelManager;
import com.app.model.framework.State;
import com.app.model.framework.Tuple;
import com.brunomnsilva.smartgraph.graph.Vertex;

public class AbstractStruct extends KStruct<State> {

	@Override
	public Vertex<State> getNode(Tuple tuple) {
		
		for(Vertex<State> v :vertices()) {
			State s = v.element();
			if(s.getInverseImage().contains(tuple)) {
				return v;
			}
		}
		return null;
	}
	
	public State getNode(String id) {
		
		for(Vertex<State> v :vertices()) {
			State s = v.element();
			if(s.getId().equals(id)) {
				return s;
			}
		}
		return null;
	}
	
public Vertex<State> getNode(State state) {
		
		for(Vertex<State> v :vertices()) {
			State s = v.element();
			if(s.equals(state)) {
				return v;
			}
		}
		return null;
	}

public void reevaluateEdges(Collection<Vertex<State>> reevaluate) {
	
	for(Vertex<State> v: reevaluate) {
		// For each state
		State from = v.element();

		List<Tuple> image = new ArrayList<Tuple>();

		// For each tuple form inverse Image
		for(Tuple tuple : from.getInverseImage()) {

			Tuple result = new Tuple();
			result.putAll(tuple);

			// Calculate image
			Set<Tuple> retval = ModelManager.getImage(result);
			image.addAll(retval);
		}

		// For each image 
		for(Tuple tuple: image) {
			Vertex<State> to = getNode(tuple);
			if(!areAdjacent(v, to)) {
				insertEdge(v, to, Integer.toString(ModelManager.edgeId++));
			}
		}
	}
	
}

}
