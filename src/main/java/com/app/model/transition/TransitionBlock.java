package com.app.model.transition;

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
import com.app.util.StringUtils;

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
	 * Checks each transition line for a certain combination of values
	 * @param tuple
	 * @return
	 */
	public Set<Variable[]> tryTuple(Variable[] tuple) {
		
		Set<Variable[]> newTupleSet = new HashSet<Variable[]>();
		
		for(TransitionLine transition: transitions) {
			
			Variable[] newTuple = transition.tryTuple(tuple);
			
			if(newTuple != null) {
				newTupleSet.add(newTuple);
				System.out.println("found tuple in bock: " + this.getVariable().getName() + ": "+ Arrays.deepToString(newTuple));
			}
		}
		
		System.out.println("new tuple set from block " + this.getVariable().getName() + ": " + StringUtils.setToString(newTupleSet) + " returning");
		return newTupleSet;
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
}
