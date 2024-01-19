package com.app.model.transition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.graphstream.graph.Node;

import com.app.model.graph.KripkeStruct;
import com.app.util.StringUtils;

public class TransitionController{

	private static TransitionController INSTANCE;

	private Set<TransitionBlock> transitionblocks;
	private Set<Variable[]> tupleSet;

	private TransitionController() {        
	}

	public static TransitionController getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new TransitionController();
		}

		return INSTANCE;
	}


	public KripkeStruct createStruct(List<Variable> varTable) {

		// Kripke structure
		KripkeStruct struct = new KripkeStruct("");

		// Set of transition blocks
		transitionblocks = new HashSet<TransitionBlock>();

		for(Variable var: varTable) {
			System.out.println(var.fullString());
			transitionblocks.add(new TransitionBlock(var));	 
		}

		// Set of states
		tupleSet = new HashSet<Variable[]>();

		// Array of variables
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

	public Variable[] cleanCopy(Variable[] tuple) {

		Variable[] copy = new Variable[tuple.length];

		for(int i = 0; i < tuple.length; i++) {
			copy[i] = new Variable(tuple[i].getName(), tuple[i].getValue(), tuple[i].getMinValue(), tuple[i].getMaxValue(), tuple[i].getTransitionBlock());
		}

		return copy;	
	}

}