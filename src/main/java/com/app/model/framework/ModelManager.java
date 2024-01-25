package com.app.model.framework;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.app.model.graph.KripkeStruct;

public class ModelManager{

	/**
	 * Map of variable id and variable
	 */
	public Map<String, Variable> vars;
	/**
	 * Map of variable id and transition block
	 */
	public Map<String, TransitionBlock> transitionBlocks;
	public String id;
	
	/**
	 * Iteration limit for all the generators
	 */
	public static final int ITERATION_LMIT = 1000;

	public ModelManager(String id) {
		// Init
		this.id = id;
		vars = new HashMap<String, Variable>();	
		transitionBlocks = new HashMap<String, TransitionBlock>();
	}

	/**
	 * Receive all the user input and load it into local variables.
	 * @param id
	 * @param variables
	 */
	public void load(Variable ...variables) {

		Arrays.asList(variables).forEach(x -> {

			vars.put(x.getId(), x); // fill map
			transitionBlocks.put(x.getId(), new TransitionBlock(x));
		});
	}
	
	public Map<String, TransitionBlock> getTransitionBlocks(){
		return transitionBlocks;
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
		//graph.addSink(new ConsoleSink(graph)); // Print to Console

		int i = 0;
		gen.begin();
		while(gen.nextEvents() && i < ITERATION_LMIT) {i++;} // returns false if finished
		gen.end();

		return graph;
	}
	
	/**
	 * Generates a Graph visualizing the initial abstraction
	 * @return
	 */
	public KripkeStruct generateInitialAbstraction() {

		// Init graph and graph generator 
		KripkeStruct graph = new KripkeStruct(id);
		InitialAbstractionGenerator gen = new InitialAbstractionGenerator(vars, transitionBlocks);

		gen.addSink(graph);
		//graph.addSink(new ConsoleSink(graph)); // Print to Console

		int i = 0;
		gen.begin();
		while(gen.nextEvents() && i < ITERATION_LMIT) {i++;} // returns false if finished
		gen.end();

		return graph;
	}
	
	
	
}
