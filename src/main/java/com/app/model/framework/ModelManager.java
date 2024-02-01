package com.app.model.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graphstream.graph.Node;

import com.app.model.graph.ConsoleSink;
import com.app.model.graph.KripkeStruct;
import com.app.util.SetUtils;

import javafx.util.Pair;

public class ModelManager{
	
	public static int nodeId = 0;
	public static int edgeId = 0;

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

	public KripkeStruct abstractionGraph;
	
	public KripkeStruct originalGraph;

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
			transitionBlockMap.put(x.getId(), x.getTransitionBlock());
		});
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
		originalGraph = new KripkeStruct("Original Graph");
		OriginalGraphGenerator gen = new OriginalGraphGenerator();

		gen.addSink(originalGraph);
		originalGraph.addSink(new ConsoleSink(originalGraph)); // Print to Console

		int i = 0;
		gen.begin();
		while(gen.nextEvents() && i < ITERATION_LMIT) {i++;} // returns false if finished
		gen.end();

		return originalGraph;
	}

	/**
	 * Generates a Graph representing the initial abstraction.
	 * @return KripkeStruct
	 */
	public KripkeStruct generateInitialAbstraction() {

		// Init graph and graph generator 
		abstractionGraph = new KripkeStruct("Initial Abstraction");
		InitialAbstractionGenerator gen = new InitialAbstractionGenerator();

		gen.addSink(abstractionGraph);
		abstractionGraph.addSink(new ConsoleSink(abstractionGraph)); // Print to Console

		int i = 0;
		gen.begin();
		while(gen.nextEvents() && i < ITERATION_LMIT) {i++;} // returns false if finished
		gen.end();

		return abstractionGraph;
	}

	/**
	 * Computes the splitPath algorithm
	 * @param finitePath a finite path as a list of state ids
	 * @return Failure state s and S
	 */
	public Pair<String, Set<Tuple>> splitPATH(List<String> finitePath) {
		return splitPATH(finitePath,abstractionGraph);
	}

	/**
	 * Computes the splitPath algorithm
	 * @param finitePath a finite path as a list of state ids
	 * @param graph Kripkestruct
	 * @return Failure state s and S
	 */
	@SuppressWarnings("unchecked")
	public Pair<String, Set<Tuple>> splitPATH(List<String> finitePath, KripkeStruct graph) {

		System.out.println("\nComputing SplitPATH for " + finitePath);
		
		Set<Tuple> S = (Set<Tuple>) graph.getNode(finitePath.get(0)).getAttribute("inverseImage");
		S.retainAll(graph.getInitialTuples());
		
		Set<Tuple> prev = new HashSet<Tuple>();

		int j = 1;
		while(!S.isEmpty() && j < finitePath.size()) {
			
			prev = new HashSet<Tuple>(S);
			Set<Tuple> inverseImage = (Set<Tuple>) graph.getNode(finitePath.get(j)).getAttribute("inverseImage");
			
			// System.out.print(String.format("Img^-1(%d) = %s ^ Img(S) = %s", j, inverseImage, getImage(S)));
			
			inverseImage.retainAll(getImage(S));	
			S = inverseImage;
			
			System.out.println("S = " + S);
			
			j++;
		}

		if(!S.isEmpty()){ // if S != {} the counterexample exists  
			return null;
		} else {
			return new Pair<String, Set<Tuple>>(finitePath.get(j), prev);
		}
	}
	
	/**
	 * Calculates the Image for a set of tuples
	 * More precisely, calculates the image for each of tuples with respect to the previously defined transitions
	 * and joins them.
	 * @param input Set of tuples
	 * @return Image
	 */
	public static Set<Tuple> getImage(Set<Tuple> input){
		
		Set<Tuple> found = new HashSet<Tuple>();
		
		for(Tuple tuple: input) {
			found.addAll(getImage(tuple));
		}
		
		found.removeAll(input);
		
		return found;
		
	}

	/**
	 * Calculates the image of a tuple with respect to the previously defined transitions.
	 * @param input Tuple
	 * @return Image
	 */
	public static Set<Tuple> getImage(Tuple input) {

		// Img(input) as a set of tuples 
		Set<Tuple> found = new HashSet<Tuple>();

		// Map of variable id and possible new values
		Map<String, Set<Tuple>> retAll = new HashMap<String, Set<Tuple>>();

		// Collect possible new values for each variable
		// e.g. (x=[0,1], y=[1,2], r=[0])
		input.forEach((k,v) -> {

			Set<Double> ret = ModelManager.getTransitionBlockMap().get(k).auditAll(input);
			Set<Tuple> temp= new HashSet<Tuple>();

			for(Double d: ret) {
				Tuple t = new Tuple();
				t.put(k, d);
				temp.add(t);
			}
			retAll.put(k, temp);
		});

		List<Set<Tuple>> all = new ArrayList<Set<Tuple>>();
		retAll.forEach((k,v) -> all.add(new HashSet<Tuple>(v)));

		// Combine possible new values for each variable
		// e.g. [[x=0], [x=1]] x [[y=1], [y=2]] x [[r=0]]
		Set<Set<Object>> tuples = SetUtils.cartesianProduct(all.toArray(new Set<?>[0]));

		// Merge combination into a single tuple
		// e.g.: [[x=0],[y=1],[r=0]] -> [x=0,y=1,r=0]
		for(Set<Object> tuple : tuples) {		
			Tuple result = new Tuple();	

			for(Object d : tuple) {		
				result.putAll((Tuple) d);
			}
			found.add(result);
		}

		found.remove(input);
		return found;
	}
	
	/**
	 * Refines the abstraction
	 * More precisely, partitions a particular failure state in a way that seperates
	 * bad tuples, dead-end tuples and irrelevant tuples in the following way:
	 * Bad tuples and dead-end tuples are seperated into to partitions. If irrelevant tuples exist,
	 * they are added to the partition of dead-end tuples.
	 * The graph is modified accordingly. A new state is added for the partition with dead-end
	 * and irrelevant tuples. Edges are adjusted accordingly.
	 */
	@SuppressWarnings("unchecked")
	public KripkeStruct refine(String failureState, Set<Tuple> deadEnds) {
		
		// Partition
		Set<Tuple> bads = (Set<Tuple>) abstractionGraph.getNode(failureState).getAttribute("inverseImage");	
		bads.removeAll(deadEnds);
		
		// New Node
		Node n = abstractionGraph.addNode(failureState + nodeId++);
		n.setAttribute("inverseImage", bads);

		
		// Remvoe all edges
		abstractionGraph.edges().forEach(abstractionGraph::removeEdge);
		
		// Edges for new Node
		edgeId = 0;
		for(Node i: abstractionGraph) {
			for(Node j: abstractionGraph) {
				if(!Collections.disjoint(getImage((Set<Tuple>) i.getAttribute("inverseImage")), (Set<Tuple>) j.getAttribute("inverseImage")));
				abstractionGraph.addEdge(edgeId++ + "", i.getId(),j.getId());
			}
		}
		return abstractionGraph;
	}
}


