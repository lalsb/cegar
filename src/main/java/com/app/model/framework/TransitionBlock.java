package com.app.model.framework;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.graphstream.graph.Node;

import com.app.model.exceptions.TransitionBlockInvalidException;
import com.app.model.graph.KripkeStruct;


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
	private String var;
	private List<TransitionLine> transitions;

	public TransitionBlock(String var, TransitionLine ... transitions) {
		this.transitions = new ArrayList<TransitionLine>();
		this.transitions.addAll(Arrays.asList(transitions));
		this.var = var;
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
		return this.var;
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
	 * Audits a transition block (e.g. all transition lines)
	 * @param current
	 * @return first found states
	 */
	public double audit(Tuple current) {
	
		for(TransitionLine transition: transitions) {
			double result = transition.audit(current);
			
			if(!Double.isNaN(result)) {
				return result;
			}
		}
		return Double.NaN;
	}
	
	
	/**
	 * Audits a transition block (e.g. all transition lines)
	 * @param current
	 * @return all found states
	 */
	public Set<Double> auditAll(Tuple current) {
		
		Set<Double> ret = new HashSet<Double>();
	
		for(TransitionLine transition: transitions) {
			double result = transition.audit(current);
			
			if(!Double.isNaN(result)) {
				ret.add(result);
			}
		}
		
		ret.add(current.get(var));
		// System.out.println("-- found (" + var.getId() + "): " + ret);
		
		return ret;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		transitions.forEach(t -> sb.append(t + ","));
		
		if(!transitions.isEmpty()) {
			return sb.substring(0, sb.length() - 1);
		}
		
		return sb.toString();
	}
}
