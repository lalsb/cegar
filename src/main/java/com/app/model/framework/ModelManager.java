package com.app.model.framework;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.app.model.graph.ConsoleSink;
import com.app.model.graph.KripkeStruct;

public class ModelManager{

	/**
	 * Map of variable id and variable
	 */
	public static Map<String, Variable> variablesMap;
	/**
	 * Map of variable id and transition block
	 */
	public static Map<String, TransitionBlock> transitionBlockMap;
	
	/**
	 * Iteration limit for all the generators
	 */
	public static final int ITERATION_LMIT = 1000;

	public ModelManager() {
		// Init
		variablesMap = new HashMap<String, Variable>();	
		transitionBlockMap = new HashMap<String, TransitionBlock>();
	}

	/**
	 * Receive all the user input and load it into local variables.
	 * @param id
	 * @param variables
	 */
	public void load(Variable ...variables) {

		Arrays.asList(variables).forEach(x -> {

			variablesMap.put(x.getId(), x); // fill map
			transitionBlockMap.put(x.getId(), new TransitionBlock(x));
		});
		
		System.out.println(variablesMap);
		System.out.println(transitionBlockMap);
	}
	
	public static Map<String, TransitionBlock> getTransitionBlockMap(){
		return transitionBlockMap;
	}
	
	public static Map<String, Variable> getvariablesMap(){
		return variablesMap;
	}
	
	
	/**
	 * Return the Variable corresponding to a given Id
	 * @param variableId
	 * @return
	 */
	public static Variable getVariable(String variableId) {		
		return variablesMap.get(variableId);
	}

	/**
	 * Generates a Graph corresponding to the model.
	 * @return
	 */
	public KripkeStruct generateOriginalGraph() {

		// Init graph and graph generator 
		KripkeStruct graph = new KripkeStruct("Original Graph");
		KripkeGraphGenerator gen = new KripkeGraphGenerator(variablesMap);

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
		KripkeStruct graph = new KripkeStruct("Initial Abstraction");
		InitialAbstractionGenerator gen = new InitialAbstractionGenerator();

		gen.addSink(graph);
		graph.addSink(new ConsoleSink(graph)); // Print to Console

		int i = 0;
		gen.begin();
		while(gen.nextEvents() && i < ITERATION_LMIT) {i++;} // returns false if finished
		gen.end();

		return graph;
	}
	
	
	
}
