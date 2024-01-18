package com.app.model.transition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.app.model.graph.KripkeStruct;

public class TransitionController{

	private static TransitionController INSTANCE;

	private Set<TransitionBlock> transitionblocks;
	private Set<Variable[]> states;

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
		states = new HashSet<Variable[]>();

		// Array of variables
		Variable[] state = (Variable[]) varTable.toArray();

		// Add initial state to stack of states
		states.add(state);

		// Build new States
		while(!states.isEmpty()) {
			
			Variable[] currentTuple = states.iterator().next();
			Set<Variable[]> newTuples = new HashSet<Variable[]>();
			
			// Add currentTuple to Kripke structure
			struct.addNode(currentTuple.toString());
			
			for(TransitionBlock transitionBlock: transitionblocks) {
				newTuples.addAll(transitionBlock.tryTuple(currentTuple));
			}
			
			// Add edges to Kripke structure
			for(Variable[] newTuple: newTuples) {
				struct.addEdge(currentTuple.toString(), newTuple.toString(), currentTuple.toString() + " -> " + newTuple.toString());
			}
			
			states.remove(currentTuple);
			
			// Loop continues for remaining states
		}
		
		
		return struct;
	}

}
/**
	// Define initial states
				// toBeTested list == initial states

					// take one tuple from toBeTested
					// test one transitions until on condition is satisfied
						// perform action -> new tuple
						// if new tuple doesnt exist create new tuple and add it to toBeTested List
						// transition between old and new tuple
						// continue with other conditions
						// if end of conidtions is reached, remove old tuple form ToBeTested List
		public void model(TransitionBlock... transitionblocks) {

			Stack<double[]> states = new Stack<double[]>();

			double[] state = new double[transitionblocks.length];
			Argument[] arguments = new Argument[transitionblocks.length];
			int i = 0;

			for(TransitionBlock transitionBlock: transitionblocks) {
				arguments[i] = new Argument(transitionBlock.getVariable().getName());
				state[i] = transitionBlock.getVariable().getValue();
				i++;
			}

			states.add(state);

			for(TransitionBlock transitionBlock: transitionblocks) {
				// Check if true for values()
				}
			}
 **/
