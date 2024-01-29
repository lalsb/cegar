package com.app.model.framework;


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
public class TransitionBlock {

	public static final String ALLOWED = "[^a-zA-Z0-9+\\-|&!]";
	private String block;
	private Variable var;
	private List<TransitionLine> transitions;

	public TransitionBlock(Variable var) {
		this.block = var.getTransitionBlock();
		this.var = var;
		try {
            validate();
            parse();
        } catch (RuntimeException e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * Returns corresponding variable
	 * @return
	 */
	public Variable getVariable() {
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
		if(!Pattern.compile(ALLOWED).matcher(block).find()) {
			throw new TransitionBlockInvalidException("Invalid symbol in transition block.");
		}
	}

	private void parse() {

		List<String> lines = Arrays.stream(block.split("\\r?\\n")) // split by new line, trim and filter empty line
				.map(x -> x.trim())
				.filter(x -> x.length() > 0)
				.collect(Collectors.toList());
		
		this.transitions = new LinkedList<TransitionLine>();
		
		for(String line: lines) {
			transitions.add(new TransitionLine(line, var));
		}
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
		
		ret.add(current.get(var.getId()));
		// System.out.println("-- found (" + var.getId() + "): " + ret);
		
		return ret;
	}
}