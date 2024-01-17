package com.app.model.transition;

import java.util.Arrays;
import java.util.List;
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

	public TransitionBlock(String block, Variable var) {
		this.block = block;	
		this.var = var;
		try {
            validate();
            parse();
        } catch (RuntimeException e) {
        	e.printStackTrace();
        }
	}
	
	public KripkeStruct model() {
		
		KripkeStruct m = new KripkeStruct("m");
		
		// Define initial states
		Node i = m.addNode(String.format("%d", var.getValue()));
		
		// toBeTested list == initial states
		
			// take one tuple from toBeTested
			// test one transitions until on condition is satisfied
				// perform action -> new tuple
				// if new tuple doesnt exist create new tuple and add it to toBeTested List
				// transition between old and new tuple
				// continue with other conditions
				// if end of conidtions is reached, remove old tuple form ToBeTested List
		
		
		
		return null;
		
	}

	public List<TransitionLine> transitions(){
		return transitions;
	}
	
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
		
		for(String line: lines) {
			transitions.add(new TransitionLine(line));
		}
	}
}
