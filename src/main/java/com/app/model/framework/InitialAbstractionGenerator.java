package com.app.model.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.stream.SourceBase;

import com.app.util.SetUtils;

import javafx.util.Pair;

public class InitialAbstractionGenerator extends SourceBase
implements Generator {

	private int nodeId = 0;
	private int edgeId = 0;

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

		Map<String, TransitionBlock> transitionBlocks = ModelManager.getTransitionBlockMap();
		List<AtomicFormula> atomicFormulas = new LinkedList<AtomicFormula>();

		transitionBlocks.forEach((k,v)->{
			v.transitions().forEach(x-> {
				atomicFormulas.addAll(x.atoms());
			});
		});

		ListIterator<AtomicFormula> iterator = atomicFormulas.listIterator();

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

	public boolean nextEvents() {

		// Entire partition 
		List<Set<Set<Tuple>>> partition = new ArrayList<Set<Set<Tuple>>>();

		// Generate equivalence Classed for each formula Cluster 
		for(FormulaCluster currentCluster: formulaClusters) {
			Set<Set<Tuple>> equivalenceClass = currentCluster.generatePartition();

			for(Set<Tuple> printlist: equivalenceClass) {
				System.out.println(printlist + " # = " + printlist.size());
			}


			partition.add(equivalenceClass);
		}

		// Cartesian product of all equivalence classes from each formula Cluster
		Set<Set<Set<Tuple>>> equivalenceCombinations = SetUtils.cartesianProduct(partition);

		System.out.println("\nGenerating states:");

		for(Set<Set<Tuple>> combination: equivalenceCombinations) {

			Set<Set<Object>> fullTuples = SetUtils.cartesianProduct(combination.toArray(new Set<?>[0]));

			State s = new State(Integer.toString(nodeId));

			for(Set<Object> full: fullTuples) {

				Tuple inverse = new Tuple();

				for(Object o: full) {
					Tuple t = (Tuple) o;
					inverse.putAll(t);
				}

				s.addToInverseImage(inverse);
				ImageMap.put(inverse, s);
			}

			addNode(s);
			nodeId++;
		}
		return false;
	}

	public void end() {

		System.out.println("\nGenerating edges: ");

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
				result.forEach((k,v) -> {

					double ret = ModelManager.getTransitionBlockMap().get(k).audit(result);
					if(!Double.isNaN(ret)) {
						result.replace(k, ret);
					}	
				});



				// Collect image
				if(!image.contains(result)) {
					image.add(result);
				}	
			}

			// For each image 
			for(Tuple tuple: image) {
				State to = ImageMap.get(tuple);
				addEdge(from, to);
			}
		}



	}

	/**
	 * Sends a node to the generator sink (graph)
	 * @param state
	 */
	protected void addNode(State state) {
		System.out.println("#" + state.getId() + " = " + state.getInverseImage());

		states.add(state);
		sendNodeAdded(sourceId, state.getId());
	}

	/**
	 * Sends an edge to the generator sink (graph)
	 * @param from
	 * @param to
	 */
	protected void addEdge(State from, State to) {

		Pair<State, State> edge = new Pair<State, State>(from, to);

		if(!edges.contains(edge)){
			
			System.out.println("#" + edgeId + " = #" + from.getId() + " > #" +  to.getId());
			
			sendEdgeAdded(sourceId, Integer.toString(edgeId) , from.getId(), to.getId(), true);
			edges.add(edge);
			edgeId++;
		}
	}

}
