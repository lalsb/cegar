package com.app.model.framework;

import com.brunomnsilva.smartgraph.graph.Vertex;

public class StateVertex implements Vertex<State> {
	
	
	private State state;

	public StateVertex(State state) {
		this.state = state;
	}

	@Override
	public State element() {
		return state;
	}

}
