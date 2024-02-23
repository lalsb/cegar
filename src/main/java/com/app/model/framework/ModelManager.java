package com.app.model.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.app.model.graph.AbstractStruct;
import com.app.model.graph.OriginalStruct;
import com.app.util.SetUtils;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;

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

	public AbstractStruct abstractionGraph;

	public OriginalStruct originalGraph;

	public ModelManager() {
		// Init
		variablesMap = new HashMap<String, Variable>();	
		transitionBlockMap = new HashMap<String, TransitionBlock>();
		initialTuples = new HashSet<Tuple>();
	}

	public void setAbstractionGraph(AbstractStruct abstractionGraph) {
		this.abstractionGraph = abstractionGraph;
	}

	/**
	 * Receive all the user input and load it into local variables.
	 * @param id
	 * @param variables
	 */
	public void load(Variable ...variables) {

		assert variables.length > 0;

		Arrays.asList(variables).forEach(v-> {

			String id = v.getId();
			TransitionBlock block = v.getTransitionBlock();

			assert !id.isBlank();
			assert block != null;

			variablesMap.put(id, v); // fill map
			transitionBlockMap.put(id, block); // fill map
		});

		Set<Set<Tuple>> initials = SetUtils.cartesianProduct(Arrays.asList(variables).stream().map(v -> v.getInitials()).toArray(Set[]::new));
		initialTuples.addAll(SetUtils.condense(initials));
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
	 * @return Variable
	 */
	public static Variable getVariable(String variableId) {
		assert !variablesMap.isEmpty();

		return variablesMap.get(variableId);
	}

	/**
	 * Generates a Graph corresponding to the model.
	 * @return OriginalStruct
	 */
	public OriginalStruct generateOriginalGraph() {
		assert !variablesMap.isEmpty();
		assert transitionBlockMap.keySet().equals(variablesMap.keySet());

		nodeId = 0;
		edgeId = 0;

		// Init graph and graph generator 
		OriginalGraphGenerator gen = new OriginalGraphGenerator();
		originalGraph = gen.generateStruct();

		return originalGraph;
	}

	/**
	 * Generates a Graph representing the initial abstraction.
	 * @return AbstractStruct
	 */
	public AbstractStruct generateInitialAbstraction() {
		assert !variablesMap.isEmpty();
		assert transitionBlockMap.keySet().equals(variablesMap.keySet());

		nodeId = 0;
		edgeId = 0;

		// Init graph and graph generator 
		InitialAbstractionGenerator gen = new InitialAbstractionGenerator();
		abstractionGraph = gen.generateStruct();

		return abstractionGraph;
	}

	/**
	 * Computes the splitPath algorithm
	 * @param finitePath a finite path as a list of state ids
	 * @param abstractionGraph Kripkestruct
	 * @return Failure state s and S
	 */
	public Pair<String, Set<Tuple>> splitPath(List<String> finitePath) {

		// Assertions, validate parameters
		if(!isValid(finitePath, abstractionGraph)) {
			throw new IllegalArgumentException("There appears to be a problem in validating your path.");
		}

		// Assertions, validate parameters
		if(abstractionGraph == null) {
			throw new IllegalArgumentException("The graph appears to be missing. ");
		}

		assert finitePath.size() > 1;
		assert getInitialTuples().size() > 0;
		assert abstractionGraph.getNode(finitePath.get(0)).isInitial();
		assert !abstractionGraph.getNode(finitePath.get(0)).isEmpty();

		System.out.println("\nComputing SplitPATH for " + finitePath + "\n");

		// Set up S, j, S_prev
		Set<Tuple> prevS = new HashSet<Tuple>();
		Set<Tuple> currS = new HashSet<Tuple>((Set<Tuple>) abstractionGraph.getNode(finitePath.get(0)).getInverseImage());
		currS.retainAll(getInitialTuples());
		assert !currS.isEmpty();	
		int j = 1;

		System.out.println("S° = " + currS);

		while(!currS.isEmpty() && j < finitePath.size()) {

			prevS = new HashSet<Tuple>(currS);
			Set<Tuple> inverseImage = new HashSet<Tuple>((Set<Tuple>) abstractionGraph.getNode(finitePath.get(j)).getInverseImage());

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

	public Pair<String, Set<Tuple>> splitLoop(List<String> finitePath, List<String> loopingPath) {

		// Assertions, validate parameters
		if(abstractionGraph == null) {
			throw new IllegalArgumentException("The graph appears to be missing. ");
		}

		int min = Integer.MAX_VALUE;

		for(String id: loopingPath) {
			Set<Tuple> inverseImage = abstractionGraph.getNode(id).getInverseImage();
			min = Math.min(min, inverseImage.size());
		}

		List<String> unwoundPath = unwind(finitePath, loopingPath, min);

		if(!isValid(unwoundPath, abstractionGraph)) {
			throw new IllegalArgumentException("There appears to be a problem in validating your path.");
		}
		
		
		return splitPath(unwoundPath);
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

	public boolean isValid(List<String> finitePath, AbstractStruct graph) {


		System.out.print("Checking Path: " + finitePath + " : ");

		if(finitePath == null || finitePath.isEmpty() || graph == null) {
			return false;
		}

		ListIterator<String> i = finitePath.listIterator();

		State prev = graph.getNode(i.next());

		if(prev == null) return false;

		if(!prev.isInitial()) {
			System.out.println("First node " + prev + " with " + prev.getInverseImage() + " is not initial.");
			return false;
		}
		while(i.hasNext()) {

			State next = graph.getNode(i.next());		
			if(next == null) return false;

			if(!graph.areAdjacent(graph.getNode(prev), graph.getNode(next))) {
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
		Set<Set<Tuple>> tuples = SetUtils.cartesianProduct(all.toArray(new Set[0]));

		// Merge combination into a single tuple
		// e.g.: [[x=0],[y=1],[r=0]] -> [x=0,y=1,r=0]
		for(Set<Tuple> tuple : tuples) {		
			Tuple result = new Tuple();	

			for(Tuple t : tuple) {		
				result.putAll(t);
			}
			found.add(result);
		}

		return found;
	}

	/**
	 * Refines the abstraction
	 * More precisely, partitions a particular failure state in a way that partitions
	 * bad tuples, dead-end tuples and irrelevant tuples in the following way:
	 * Bad union irrelevat and dead-end.
	 */

	public AbstractStruct refine(String failureState, Set<Tuple> deadEnds) {


		if(failureState == null || failureState.isBlank() ||
				abstractionGraph.getNode(failureState) == null) {
			throw new IllegalArgumentException("Invalid argument 'failure State' for method 'refine'");
		}

		if(deadEnds == null || deadEnds.isEmpty()) {
			throw new IllegalArgumentException("Invalid argument 'deadEnds' for method 'refine'");
		}

		// Old Node
		State old = abstractionGraph.getNode(failureState);

		// Update Old Node
		Set<Tuple> rest = old.getInverseImage();
		rest.removeAll(deadEnds);

		// New Node
		State n = new State(Integer.toString(nodeId++));
		n.setInverseImage(deadEnds);

		abstractionGraph.insertVertex(n);

		System.out.println("Old node " + old.getId() + "'s inverse image: " + old.getInverseImage());
		System.out.println("New node " + n.getId() + "'s inverse image: " + n.getInverseImage());


		List<Vertex<State>> reevaluate = new ArrayList<Vertex<State>>();
		reevaluate.add(abstractionGraph.getNode(old));
		reevaluate.add(abstractionGraph.getNode(n));
		Vertex<State> oldVertex = abstractionGraph.getNode(old);
		for(Edge<String, State> e: abstractionGraph.incidentEdges(oldVertex)) {
			reevaluate.add(abstractionGraph.opposite(oldVertex, e));
		}

		for(Edge<String, State> e: abstractionGraph.incidentEdges(oldVertex)) {
			abstractionGraph.removeEdge(e);
		}

		for(Edge<String, State> e: abstractionGraph.outboundEdges(oldVertex)) {
			abstractionGraph.removeEdge(e);
		}

		abstractionGraph.reevaluateEdges(reevaluate);

		return abstractionGraph;


		// unevaluated vertices = all vetices from inbound edges, old state, new state
		// delete = inbound edges, outbound edges
		// reeavalute vertice

	}

	public static Set<Tuple> getInitialTuples() {	
		return initialTuples;
	}
}


