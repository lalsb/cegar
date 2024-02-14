package com.app.model.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import com.app.model.graph.ConsoleSink;
import com.app.model.graph.KripkeStruct;
import com.app.util.SetUtils;

import javafx.util.Pair;

public class ModelManager{

	public static int nodeId = 0;
	public static int edgeId = 0;

	/**
	 * Map of variable id and variable.
	 */
	public static Map<String, Variable> variablesMap;
	/**
	 * Map of variable id and transition block.
	 */
	public static Map<String, TransitionBlock> transitionBlockMap;	
	/**
	 * Set of initial tuples.
	 */
	public static Set<Tuple> initialTuples;	
	/**
	 * Iteration limit for all the generators.
	 */
	public static final int ITERATION_LMIT = 1000;

	public KripkeStruct abstractionGraph;

	public KripkeStruct originalGraph;

	public ModelManager() {
		// Init
		variablesMap = new HashMap<String, Variable>();	
		transitionBlockMap = new HashMap<String, TransitionBlock>();
		initialTuples = new HashSet<Tuple>();
	}

	public void setAbstractionGraph(KripkeStruct abstractionGraph) {
		this.abstractionGraph = abstractionGraph;
	}

	/**
	 * Receive all the user input and load it into local variables.
	 * @param id
	 * @param variables
	 */
	public void load(Variable ...variables) {

		assert variables.length > 0;

		Tuple init = new Tuple();

		Arrays.asList(variables).forEach(x -> {

			String id = x.getId();
			TransitionBlock block = x.getTransitionBlock();

			assert !id.isBlank();
			assert block != null;

			variablesMap.put(id, x); // fill map
			transitionBlockMap.put(id, block);
			init.put(id, x.getValue());		
		});

		initialTuples.add(init);

	}

	public static Map<String, TransitionBlock> getTransitionBlockMap(){	
		assert !transitionBlockMap.isEmpty();

		return transitionBlockMap;
	}

	public static Map<String, Variable> getvariablesMap(){
		assert !variablesMap.isEmpty();

		return variablesMap;
	}


	/**
	 * Return the Variable corresponding to a given Id
	 * @param variableId
	 * @return
	 */
	public static Variable getVariable(String variableId) {
		assert !variablesMap.isEmpty();

		return variablesMap.get(variableId);
	}

	/**
	 * Generates a Graph corresponding to the model.
	 * @return
	 */
	public KripkeStruct generateOriginalGraph() {
		assert !variablesMap.isEmpty();
		assert transitionBlockMap.keySet().equals(variablesMap.keySet());

		nodeId = 0;
		edgeId = 0;

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
		assert !variablesMap.isEmpty();
		assert transitionBlockMap.keySet().equals(variablesMap.keySet());

		nodeId = 0;
		edgeId = 0;

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
	 * @param abstractionGraph Kripkestruct
	 * @return Failure state s and S
	 */
	@SuppressWarnings("unchecked")
	public Pair<String, Set<Tuple>> splitPath(List<String> finitePath) {

		// Assertions, validate parameters
		if(!isValid(finitePath, abstractionGraph)) {
			throw new IllegalArgumentException("Path invalid");
		}

		// Assertions, validate parameters
		if(abstractionGraph == null) {
			throw new IllegalArgumentException("Graph invalid");
		}

		assert finitePath.size() > 1;
		assert getInitialTuples().size() > 0;
		assert abstractionGraph.getNode(finitePath.get(0)).hasAttribute("isInitial");
		assert abstractionGraph.getNode(finitePath.get(0)).hasAttribute("inverseImage");

		System.out.println("\nComputing SplitPATH for " + finitePath + "\n");

		// Set up S, j, S_prev
		Set<Tuple> prevS = new HashSet<Tuple>();
		Set<Tuple> currS = new HashSet<Tuple>((Set<Tuple>) abstractionGraph.getNode(finitePath.get(0)).getAttribute("inverseImage"));
		currS.retainAll(getInitialTuples());
		assert !currS.isEmpty();	
		int j = 1;

		System.out.println("S° = " + currS);

		while(!currS.isEmpty() && j < finitePath.size()) {

			prevS = new HashSet<Tuple>(currS);
			Set<Tuple> inverseImage = new HashSet<Tuple>((Set<Tuple>) abstractionGraph.getNode(finitePath.get(j)).getAttribute("inverseImage"));

			System.out.println("----------------------------------------------------------------------------");
			System.out.print(String.format("S(%d) = Img() = %s -- h^-1(%d) = %s", j, getImage(currS), j, inverseImage));

			inverseImage.retainAll(getImage(currS));	
			currS = inverseImage;

			System.out.println(" = " + currS);

			j++;
		}

		if(!currS.isEmpty()){ // if S != {} the counterexample exists  
			return null;
		} else {
			return new Pair<String, Set<Tuple>>(finitePath.get(j-2), prevS);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Pair<String, Set<Tuple>> splitLoop(List<String> finitePath, List<String> loopingPath) {
		
		int min = Integer.MAX_VALUE;
		
		for(String id: loopingPath) {
			Set<Tuple> inverseImage = (Set<Tuple>) abstractionGraph.getNode(id).getAttribute("inverseImage");
			min = Math.min(min, inverseImage.size());
		}
		
		List<String> unwoundPath = unwind(finitePath, loopingPath, min);
		
		Pair<String, Set<Tuple>> ret = splitPath(unwoundPath);
		
		int j = (int) Double.NaN;
		
		return null;
	}
	
	/**
	 * Unwinds an infinite path by copying the finite part and appending the looping part for a given number of repetitions. 
	 * 
	 * @param finitePath series of elements to be left untouched
	 * @param loopingPath series of elements to be unwound
	 * @param unwindings number of unwindings
	 * @return unwoundPath
	 */
	public List<String> unwind(List<String> finitePath, List<String> loopingPath, int unwindings){
		
		// Copy finite part
		List<String> unwoundPath = new ArrayList<String>(finitePath); 
		
		// Append looping part repeatedly
		for(int i = 0; i <= unwindings; i++)
			for(String element: loopingPath) {
				unwoundPath.add(element);
			}
		
		// Return composite
		return unwoundPath;
		
		
		
	}

	public boolean isValid(List<String> finitePath, KripkeStruct graph) {


		System.out.print("Checking Path: " + finitePath + " : ");

		if(finitePath == null || finitePath.isEmpty() || graph == null) {
			return false;
		}

		ListIterator<String> i = finitePath.listIterator();

		Node prev = graph.getNode(i.next());
		if(!prev.hasAttribute("isInitial") || !(boolean) prev.getAttribute("isInitial")) {
			System.out.println("First node " + prev + " with " + prev.getAttribute("inverseImage") + " is not initial.");
			return false;
		}
		while(i.hasNext()) {	
			Node next = graph.getNode(i.next());		
			if(prev.getEdgeToward(next) == null) {
				System.out.println("No edge from node " + prev.getId() + " to node " + next.getId() + ".");
				return false;
			}
			prev = next;
		}

		System.out.println("Valid.");
		return true;

	}

	/**
	 * Calculates the Image for a set of tuples
	 * More precisely, calculates the image for each of tuples with respect to the previously defined transitions
	 * and joins them.
	 * @param input Set of tuples
	 * @return Image
	 */
	public static Set<Tuple> getImage(Set<Tuple> input){	
		assert !transitionBlockMap.isEmpty();

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
		assert !transitionBlockMap.isEmpty();

		// Img(input) as a set of tuples 
		Set<Tuple> found = new HashSet<Tuple>();

		// Map of variable id and possible new values
		Map<String, Set<Tuple>> retAll = new HashMap<String, Set<Tuple>>();

		// Collect possible new values for each variable
		// e.g. (x=[0,1], y=[1,2], r=[0])
		input.forEach((k,v) -> {

			Set<Double> ret = ModelManager.getTransitionBlockMap().get(k).audit(input);
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

		//TODO: Not important?? found.remove(input);
		return found;
	}

	/**
	 * Refines the abstraction
	 * More precisely, partitions a particular failure state in a way that partitions
	 * bad tuples, dead-end tuples and irrelevant tuples in the following way:
	 * Bad union irrelevat and dead-end.
	 */

	@SuppressWarnings("unchecked")
	public KripkeStruct refine(String failureState, Set<Tuple> deadEnds) {


		if(failureState == null || failureState.isBlank() ||
				abstractionGraph.getNode(failureState) == null) {
			throw new IllegalArgumentException("Invalid argument 'failure State' for method 'refine'");
		}

		if(deadEnds == null || deadEnds.isEmpty()) {
			throw new IllegalArgumentException("Invalid argument 'deadEnds' for method 'refine'");
		}

		// Old Node
		Node old = abstractionGraph.getNode(failureState);

		// Update Old Node
		Set<Tuple> rest = (Set<Tuple>) old.getAttribute("inverseImage");
		rest.removeAll(deadEnds);

		// New Node
		Node n = abstractionGraph.addNode(Integer.toString(nodeId++));
		n.setAttribute("inverseImage", deadEnds);

		System.out.println("Old node " + old.getId() + "'s inverse image: " + old.getAttribute("inverseImage"));
		System.out.println("New node " + n.getId() + "'s inverse image: " + n.getAttribute("inverseImage"));

		// Re-evaluate incoming edges
		for(Edge entering: old.enteringEdges().toList()) {
			
			Set<Tuple> target = getImage((Set<Tuple>) entering.getSourceNode().getAttribute("inverseImage"));
			
			if(SetUtils.intersect(target, rest) && SetUtils.intersect(target, deadEnds)) {

				System.out.println("Entering: Keeping edge from " + entering.getSourceNode() + " to old state and adding new edge from there to new state");
				// keep edge to old node
				// add new entering edge for new sate
				abstractionGraph.addEdge(Integer.toString(edgeId++), entering.getSourceNode(), n);

			} else if(SetUtils.intersect(target, deadEnds)){

				System.out.println("Entering: Changing target of edge from " + entering.getSourceNode() + " to new node.");
				setTargetTo(abstractionGraph, entering, n); // change edge target to new node
				
			}else {
				
				System.out.println("Entering: Keeping edge from " + entering.getSourceNode());
				// keep edge to old node
				
			}
		}
		// Re-evaluate outgoing egdes
		for(Edge leaving: old.leavingEdges().toList()) {
			
			Set<Tuple> target = (Set<Tuple>) leaving.getTargetNode().getAttribute("inverseImage");
			
			if(SetUtils.intersect(target, getImage(rest)) && SetUtils.intersect(target, getImage(deadEnds))) {

				System.out.println("Leaving: Keeping edge to " + leaving.getTargetNode() + " from old state and adding new edge to there from new state");
				// keep edge from old node
				// add new leaving edge for new state
				abstractionGraph.addEdge(Integer.toString(edgeId++), n, leaving.getTargetNode());

			} else if(SetUtils.intersect(target, getImage(deadEnds))){

				// change edge source to new node
				System.out.println("Leaving: Changing source form edge towards " + leaving.getTargetNode() + " to new node.");
				setSourceTo(abstractionGraph, leaving, n);
				
			} else {
				
				System.out.println("Leaving: Keeping edge to " + leaving.getTargetNode());
				// keep edge from old node
				
			}
		}

		return abstractionGraph;
	}

	// Method to change the target node of an edge while keeping the source node and the edge ID
	public static void setTargetTo(KripkeStruct graph, Edge edge, Node newTargetNode) {
		// Remove the existing edge
		graph.removeEdge(edge);
		// Add a new edge with the same ID but different target node
		graph.addEdge(edge.getId(), edge.getSourceNode(), newTargetNode, true);
	}

	// Method to change the source node of an edge while keeping the target node and the edge ID
	public static void setSourceTo(KripkeStruct graph, Edge edge, Node newSourceNode) {
		// Remove the existing edge
		graph.removeEdge(edge);
		// Add a new edge with the same ID but different source node
		graph.addEdge(edge.getId(), newSourceNode, edge.getTargetNode(), true);
	}

	public static Set<Tuple> getInitialTuples() {	
		return initialTuples;
	}
}


