package com.app.model.framework;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

/**
 * Transition line implementation that parses one transition line.
 * @author linus
 *
 */
public class TransitionLine implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final String LITERALS = "[A-Za-z]+";
	public static final String JUNCTIONS = "[&|]";
	public static final String TRANSITION_SEPERATOR = ":";

	private String varId;
	private String condition;
	private String action;
	private List<AtomicFormula> atoms;

	public TransitionLine(String varId, String condition, String action) {
		this.varId = varId;
		this.condition = condition;
		this.action = action;
		isValid();
		parse();
	}

	public String actionSubstring() {
		return action;
	}

	public String conditionSubstring() {
		return condition;
	}

	public List<AtomicFormula> atoms(){
		return atoms;
	}

	/**
	 * Checks Transition Block6
	 * @throws IllegalArgumentException
	 */
	private void isValid() throws IllegalArgumentException{

		if(condition.isBlank()) {
			throw new IllegalArgumentException();
		}
		
		if(action.isBlank()) {
			throw new IllegalArgumentException();
		}

	}

	/**
	 * Parses a transition, performing the following steps:
	 * - Splits transitions into a condition C (predicate) and an action A (expression)
	 * - 
	 * -
	 */
	private void parse() {

		atoms = new LinkedList<AtomicFormula>();
		Pattern p = Pattern.compile(LITERALS);

		for(String atom: condition.split(JUNCTIONS)) { // Split conditions at junctions

			Set<String> vars = new HashSet<String>();
			Matcher m = p.matcher(atom);

			while (m.find()) {
				vars.add(m.group()); // Note variables for a particular set
			}

			Expression e = new Expression(atom);
			e.defineArguments(e.getMissingUserDefinedArguments());
			atoms.add(new AtomicFormula(e)); // Atomc formula object
			}
	}

	/**
	 * Audits a transition line
	 * @param current
	 * @return
	 */
	public double audit(Tuple current) {

		// Create new state and copy attributes
		Tuple s = new Tuple();
		current.forEach((k,v) -> s.put(k,v));

		// Calculate condition with mXParser
		Argument[] arguments = current.genereateArguments();
		Expression c = new Expression(condition,arguments);

		// Check if condition c is true
		if(c.calculate() == (1.0)) {

			// Calculate action expression with mXParser
			Expression a = new Expression(action,arguments);	
			double result = a.calculate();
			if(ModelManager.getVariable(varId).isInBounds(result)) {
				return result;
			}

		} 

		// Conditions false or new value not new or not in bounds
		return Double.NaN;
	}

	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString() {
		
		return "(" + condition + ":" + action + ")"; 
	}
}