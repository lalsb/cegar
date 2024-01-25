package com.app.model.framework;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.stream.SourceBase;
import com.app.model.framework.TransitionLine;

import com.app.model.graph.State;

public class InitialAbstractionGenerator extends SourceBase
implements Generator {

	/**
	 * Map of variable id and variable e.g. look-up-table for variables by id
	 */
	public Map<String, Variable> vars;
	public Map<String, TransitionBlock> transitionBlocks;
	public List<FormulaCluster> formulaClusters;

	public InitialAbstractionGenerator(Map<String, Variable> vars, Map<String, TransitionBlock> transitionBlocks) {
		super();
		this.vars = vars;
		this.transitionBlocks = transitionBlocks;
		formulaClusters = new LinkedList<FormulaCluster>();
	}

	public void begin() {

		List<AtomicFormula> atoms = new LinkedList<AtomicFormula>();

		transitionBlocks.forEach((k,v)->{
			v.transitions().forEach(x-> {
				atoms.addAll(x.atoms());
			});
		});

		ListIterator<AtomicFormula> iterator = atoms.listIterator();

		while(iterator.hasNext()) {

			// Take a new atomic formula
			AtomicFormula a = (AtomicFormula) iterator.next();
			boolean found = false;

			for(FormulaCluster cluster: formulaClusters) {

				// Check if the variables of the atomic formula and an existing cluster intersect
				if(!Collections.disjoint(cluster.getVariableCluster(), a.getVars())) {
					found = true;
					cluster.addFormula(a);
					break;
				}
			}
		
			// If there is no intersection create a new formula cluster
			if(!found) {
				FormulaCluster f = new FormulaCluster();
				f.addFormula(a);
				formulaClusters.add(f);
			}
			
			for(FormulaCluster cluster: formulaClusters) {
				
				cluster.getVariableCluster();
			}
		}
	}

	public boolean nextEvents() {
		
		return true;
	}

	public void end() {
		// Nothing to do
	}

	/**
	 * Sends a node to the generator sink (graph)
	 * @param state
	 */
	protected void addNode(State state) {
	}

	/**
	 * Sends an edge to the generator sink (graph)
	 * @param from
	 * @param to
	 */
	protected void addEdge(State from, State to) {
	}

}
