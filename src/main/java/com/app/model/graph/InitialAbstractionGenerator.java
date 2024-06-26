package com.app.model.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.app.model.framework.AtomicFormula;
import com.app.model.framework.FormulaCluster;
import com.app.util.SetUtils;

import javafx.util.Pair;

public class InitialAbstractionGenerator implements KStructGenerator<State> {

	
	AbstractStruct struct;
	/**
	 * List of formula clusters
	 */
	private List<FormulaCluster> formulaClusters;

	/**
	 * List of states generated by this generator
	 */
	List<State> states;

	/**
	 * Map of tuples and corresponding states
	 */
	Map<Tuple, State> ImageMap;

	/**
	 * Map of tuples and corresponding states
	 */
	List<Pair<State, State>> edges;

	public InitialAbstractionGenerator() {
		super();
		formulaClusters = new ArrayList<FormulaCluster>();
		states = new ArrayList<State>();
		ImageMap = new HashMap<Tuple, State>();
		edges = new ArrayList<Pair<State, State>>();
	}
	
	public AbstractStruct generateStruct() {
		
		struct = new AbstractStruct();
		begin();
		while(nextEvents());
		System.out.println("generated intial abstraction graph");
		return struct;
		
	}
	

	/**
	 * Gets the atomic formulas
	 * @return list of atomic formulas
	 */
	public List<FormulaCluster> getFormulaClusters(){
		return formulaClusters;
	}

	/**
	 * Sorts all the atomic formulas into formula clusters
	 */
	public void begin() {

		ListIterator<AtomicFormula> iterator =  ModelManager.getAtomicFormulas().listIterator();

		while(iterator.hasNext()) {

			// Take a new atomic formula
			AtomicFormula formula = (AtomicFormula) iterator.next();
			boolean found = false;

			for(FormulaCluster cluster: formulaClusters) {

				// Check if the variables of the atomic formula and an existing cluster intersect
				if(!Collections.disjoint(cluster.getAllVariableIds(), formula.getAllVariableIds())) {
					found = true;
					cluster.addFormula(formula);
					break;
				}
			}

			// If there is no intersection create a new formula cluster
			if(!found) {
				FormulaCluster f = new FormulaCluster();
				f.addFormula(formula);
				formulaClusters.add(f);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public boolean nextEvents() {
		
		// Entire partition 
		List<Set<Set<Tuple>>> partition = new ArrayList<Set<Set<Tuple>>>();

		// Generate equivalence Classed for each formula Cluster 
		for(FormulaCluster currentCluster: formulaClusters) {
			Set<Set<Tuple>> equivalenceClass = currentCluster.generatePartition();

			partition.add(equivalenceClass);
		}
		
		System.out.println("generated partition clusters");

		// Cartesian product of all equivalence classes from each formula Cluster
		Set<Set<Tuple>> equivalenceCombinations = SetUtils.cartesianProduct(partition.toArray(new Set[0]));

		for(Set<Tuple> combination: equivalenceCombinations) {

			Set<Set<Tuple>> fullTuples = SetUtils.cartesianProduct(combination.toArray(new Set[0]));

			State s = new State(Integer.toString(ModelManager.nodeId++));

			for(Set<Tuple> full: fullTuples) {

				Tuple inverse = new Tuple();

				for(Object o: full) {
					Tuple t = (Tuple) o;
					inverse.putAll(t);
				}

				s.addToInverseImage(inverse);
				ImageMap.put(inverse, s);
			}

			addNode(s);
		}
		
		System.out.println("generated states");

		ListIterator<State> iterator = states.listIterator();

		while(iterator.hasNext()) {

			// For each state
			State from = iterator.next();

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
				State to = ImageMap.get(tuple);
				addEdge(from, to);
			}
		}
		
		System.out.println("generated edges");

		return false; // finished
	}
	
	/**
	 * Sends a node to the generator sink (graph)
	 * @param state
	 */
	protected void addNode(State state) {
		states.add(state);
		struct.insertVertex(state);
	}

	/**
	 * Sends an edge to the generator sink (graph)
	 * @param from
	 * @param to
	 */
	protected void addEdge(State from, State to) {

		Pair<State, State> edge = new Pair<State, State>(from, to);

		if(!edges.contains(edge)){
			struct.insertEdge(from, to,  Integer.toString(ModelManager.edgeId++));
			edges.add(edge);
		}
	}

}
