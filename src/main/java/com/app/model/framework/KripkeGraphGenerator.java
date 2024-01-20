package com.app.model.framework;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.stream.SourceBase;

import com.app.model.graph.ConsoleSink;
import com.app.model.graph.State;

public class KripkeGraphGenerator extends SourceBase
implements Generator {

	int nodeID = 0;
	int edgeID = 0;

	/**
	 * Map of variable id and variable e.g. look-up-table for variables by id
	 */
	public Map<String, Variable> vars;
	/**
	 * Map of variable ids as keys with corresponding values e.g. a 'state'
	 */
	State current;

	Iterator<State> i;

	Set<State> audited;
	Set<State> unaudited;
	Set<State> found;
	Set<State> added;

	public KripkeGraphGenerator(Map<String, Variable> vars) {
		super();
		this.vars = vars;
		this.addSink(new ConsoleSink());

		audited = new HashSet<State>();
		unaudited = new HashSet<State>();
		found = new HashSet<State>();
		added = new HashSet<State>();
	}

	public void begin() {

		current = new State(Integer.toString(nodeID)); // Create state
		vars.forEach((k,v) -> current.put(k, v.getValue())); // Add initial values
		unaudited.add(current);
	}

	public boolean nextEvents() {

		i = unaudited.iterator();
		
		if(!i.hasNext()) {
			return false;
		}

		current = i.next();
		addNode(current); // Send node to graph

		for(Variable var: vars.values()) {
			for(State s : new TransitionBlock(var).audit(current)) {
				found.add(s);
				addEdge(current, s);
			}
		}
		
		audited.add(current); // Current audit complete
		found.removeAll(audited);
		unaudited.addAll(found);

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
		sendNodeAdded(sourceId, Integer.toString(nodeID));

		for (var entry : state.entrySet()) {
			this.sendNodeAttributeAdded(sourceId, Integer.toString(nodeID), entry.getKey(), entry.getValue());
		}

		nodeID++;
	}

	
	/**
	 * Sends an edge to the generator sink (graph)
	 * @param from
	 * @param to
	 */
	private void addEdge(State from, State to) {
		this.sendEdgeAdded(sourceId,  Integer.toString(edgeID), from.id(), to.id(), true);
		
		edgeID++;

	}

}
/**
//Array of variables
		Variable[] tuple = varTable.toArray(new Variable[0]);

		// Add initial state to stack of states
		tupleSet.add(tuple);

		Set<Variable[]> testedTupleSet = new HashSet<Variable[]>();

		// Build states and edges
		while(!tupleSet.isEmpty()) {

			System.out.println("Tuple set (to be tested in while loop): " + StringUtils.setToString(tupleSet));

			Variable[] currentTuple = tupleSet.iterator().next();
			testedTupleSet.add(currentTuple);
			Set<Variable[]> newTupleSet = new HashSet<Variable[]>();

			// Add currentTuple to Kripke structure
			if(struct.getNode(Arrays.deepToString(currentTuple)) == null) {
				System.out.println("Kripke: " + Arrays.deepToString(currentTuple) + " (S)");
				struct.addNode(Arrays.deepToString(currentTuple));
			}

			for(TransitionBlock transitionBlock: transitionblocks) {

				System.out.println("************ Testing Block " + transitionBlock.getVariable().getName() + " with tuple" + Arrays.deepToString(currentTuple) + " ************");
				Set<Variable[]> newTupleSetFromOneBlock = transitionBlock.tryTuple(cleanCopy(currentTuple));
				newTupleSet.addAll(newTupleSetFromOneBlock);
			}

			// Iterate new tuples
			for(Variable[] newTuple: newTupleSet) {

				// Add new states if not already existing
				if(struct.getNode(Arrays.deepToString(newTuple)) == null) {
					struct.addNode(Arrays.deepToString(newTuple));
					System.out.println("Kripke: " + Arrays.deepToString(newTuple) + " (S)");
				}

				// Add new edges if not already existing
				if(struct.getEdge(Arrays.deepToString(newTuple) + " -> " + Arrays.deepToString(currentTuple)) == null) {
					struct.addEdge(Arrays.deepToString(currentTuple) + " -> " + Arrays.deepToString(newTuple),Arrays.deepToString(newTuple),Arrays.deepToString(currentTuple));
					System.out.println("Kripke: " +  Arrays.deepToString(currentTuple) + " -> " + Arrays.deepToString(newTuple) + " (T)");
				}
			}

			for(Variable[] oneNewTuple : newTupleSet) {

				boolean contains = false;

				for(Variable[] oneOldTuple : tupleSet) {
					if(Arrays.deepToString(oneNewTuple).equals(Arrays.deepToString(oneOldTuple))){
						contains = true;
					}	
				}

				for(Variable[] onetestedTuple : testedTupleSet) {
					if(Arrays.deepToString(oneNewTuple).equals(Arrays.deepToString(onetestedTuple))){
						contains = true;
					}	
				}

				// Tuple is not already tested or in to be tested set
				if(!contains) {
					System.out.println("Adding " + Arrays.deepToString(oneNewTuple) + "to tuple Set");
					tupleSet.add(oneNewTuple);
				}
			}

			Variable[] toRemove = null;
			for(Variable[] oneOldTuple : tupleSet) {
				if(Arrays.deepToString(oneOldTuple).equals(Arrays.deepToString(currentTuple))){
					toRemove = oneOldTuple;
				}
			}

			if(toRemove != null) {
				tupleSet.remove(toRemove);
			}

			System.out.println("Removing " + Arrays.deepToString(currentTuple) + "from tuple Set");

			// Loop continues for remaining states
		}


		return struct;
	}
 **/