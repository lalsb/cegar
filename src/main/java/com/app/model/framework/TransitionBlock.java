package com.app.model.framework;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import com.app.model.exceptions.TransitionBlockInvalidException;


/**
 * Transition block implementation that splits transition blocks by line and passes them on.
 * @author Linus Alsbach
 */
public class TransitionBlock implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ALLOWED = "[^a-zA-Z0-9+\\-|&!]";
	private String varId;
	private List<TransitionLine> transitions; // Last Transition represents else

	public TransitionBlock(String var, TransitionLine ... transitions) {
		this.transitions = new ArrayList<TransitionLine>();
		for(TransitionLine transition: transitions) {
			this.transitions.add(transition);
		}
		this.varId = var;
		try {
            validate();
        } catch (RuntimeException e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * Returns corresponding variable
	 * @return
	 */
	public String getVariable() {
		return this.varId;
	}
	
	/**
	 * Return list of transition lines
	 * @return
	 */
	public List<TransitionLine> transitions(){
		return transitions;
	}
	
	/**
	 * Checks if the entire transition block contains disallowed symbols in which case it
	 * @throws TransitionBlockInvalidException
	 */
	private void validate() {
		transitions.forEach(l -> l.validate());
	}

	/**
	 * Audits a transition block (e.g. all transition lines until one condition is satisfied)
	 * @param current
	 * @return first found states
	 */
	public Set<Double> audit(Tuple current) {
		
		assert !transitions.isEmpty(); // There's at least the else condition
		
		if(current == null || current.isEmpty()) {
			throw new IllegalArgumentException("Invalid tuple audited in Transition block: " + varId);
		}
		
		ListIterator<TransitionLine> i = transitions.listIterator();
		Set<Double> result = i.next().audit(current);
		
		while(i.hasNext() && result.isEmpty()) {
			result = i.next().audit(current);
		}

		return result;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < transitions.size() - 1; i++) {
			sb.append("if ");
			sb.append(transitions.get(i).getCondition());
			sb.append(" then ");
			sb.append(transitions.get(i).getActions());
			sb.append(" ");
		}
		
		sb.append("else ");
		sb.append(transitions.get(transitions.size() - 1).getActions());
		
		return sb.toString();
	}
}
