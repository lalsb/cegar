package com.app.model.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.stream.SourceBase;

import com.app.util.SetUtils;

public class InitialAbstractionGenerator extends SourceBase
implements Generator {

	/**
	 * List of formula clusters
	 */
	private List<FormulaCluster> formulaClusters;

	public InitialAbstractionGenerator() {
		super();
		formulaClusters = new LinkedList<FormulaCluster>();
	}

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
			partition.add(equivalenceClass);
		}
		
		// Cartesian product of all equivalence classes from each formula Cluster
		Set<Set<Set<Tuple>>> equivalenceCombinations = SetUtils.cartesianProduct(partition);
		
		int i = 0;
		for(Set<Set<Tuple>> combination: equivalenceCombinations) {
			
			State s = new State(Integer.toString(i));
			addNode(s);
			i++;
			
			for(Set<Tuple> set : combination) {
				for(Tuple tuple: set) {
					System.out.print(tuple);
				}
			}
		}
		
	
		return false;
	}

	public void end() {
		// Nothing to do
	}

	/**
	 * Sends a node to the generator sink (graph)
	 * @param state
	 */
	protected void addNode(State state) {
		System.out.print("State added: " + state.getId());
		sendNodeAdded(sourceId, state.getId());
	}

	/**
	 * Sends an edge to the generator sink (graph)
	 * @param from
	 * @param to
	 */
	protected void addEdge(Tuple from, Tuple to) {
	}

}
