package com.app.model.framework;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.graphstream.stream.Sink;
import org.graphstream.stream.SinkAdapter;

import com.app.model.graph.ConsoleSink;
import com.app.model.graph.KripkeStruct;

public class ModelManager{

	private Set<TransitionBlock> transitionblocks;
	/**
	 * Map of variable id and variable
	 */
	public Map<String, Variable> vars;
	public String id;

	public ModelManager(String id) {
		// Init
		this.id = id;
		vars = new HashMap<String, Variable>();	
	}

	/**
	 * Reveive all the user input and load it into local variables.
	 * @param id
	 * @param variables
	 */
	public void load(Variable ...variables) {

		Arrays.asList(variables).forEach(x -> {

			vars.put(x.getId(), x); // fill map
			transitionblocks.add(new TransitionBlock(x)); // fill set of transition blocks

		});
	}


	/**
	 * Generates a Graph corresponding to the model.
	 * @return
	 */
	public KripkeStruct generateGraph() {

		// Init graph and graph generator 
		KripkeStruct graph = new KripkeStruct(id);
		KripkeGraphGenerator gen = new KripkeGraphGenerator(vars);

		gen.addSink(graph);
		graph.addSink(new ConsoleSink()); // Print to Console

		gen.begin();
		while(gen.nextEvents()) {} // returns if finished
		gen.end();

		return graph;

	}

	public Variable[] cleanCopy(Variable[] tuple) {

		Variable[] copy = new Variable[tuple.length];

		for(int i = 0; i < tuple.length; i++) {
			copy[i] = new Variable(tuple[i].getId(), tuple[i].getValue(), tuple[i].getMinValue(), tuple[i].getMaxValue(), tuple[i].getTransitionBlock());
		}

		return copy;	
	}
}