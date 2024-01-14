package com.app.model;

import java.util.HashSet;
import java.util.Set;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import com.app.model.exceptions.KripkeStructureInvalidException;

/**
 * MultiGraph
 * A graph implementation that supports multiple edges between two nodes.
 * @author linus
 *
 */
public class KripkeStruct extends MultiGraph{
	
	private Set<Node> initialStates;

	public KripkeStruct(String id) {
		super(id);
		initialStates = new HashSet<Node>();
	}
	
	public boolean isValid() {
        try {
            validate();
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    public void validate() {
        if (this.getNodeCount() == 0) {
            throw new KripkeStructureInvalidException("Set of states is empty.");
        }

        if (initialStates.isEmpty()) {
            throw new KripkeStructureInvalidException("Set of initial states is empty.");
        }

        validateTransitionsAreLeftTotal();
    }

    public Set<Node> getInitialStates() {
        return initialStates;
    }

    public Set<Node> getAllSuccessorStates(Node state) {
    	return null;
    }

    private void validateTransitionsAreLeftTotal() {
        // states.forEach(state -> {
          //  if (transitions.getOrDefault(state, new HashSet<>()).isEmpty()) {
            //    throw new KripkeStructureInvalidException(
                      //  String.format("There is no transition starting from state %s.", state));
         //   }
       // });
    }

	
}
