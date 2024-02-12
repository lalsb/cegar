package com.app.model.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import com.app.model.exceptions.IllegalInputException;
import com.app.util.PredicateSplitter;

/**
 * Transition line implementation that parses one transition line.
 * @author linus
 *
 */
public class TransitionLine implements Serializable{
	private static final long serialVersionUID = 1L;

	private String varId;
	private String condition;
	private List<String> actions;
	private List<AtomicFormula> atoms;

	public TransitionLine(String varId, String condition, List<String> action) {
		this.varId = varId;
		this.condition = condition;
		this.actions = action;
		isValid();
		parse();
	}

	public List<String> getActions() {
		return actions;
	}

	public String getCondition() {
		return condition;
	}

	public List<AtomicFormula> getAtoms(){
		return atoms;
	}

	/**
	 * Checks Transition Block6
	 * @throws IllegalArgumentException
	 */
	private void isValid() throws IllegalInputException{

		if(condition == null || condition.isBlank()) {
			throw new IllegalInputException("The field \"condition\" must not be empty or blank.");
		}
		
		if(actions == null ||actions.isEmpty() || actions.contains("") || actions.contains(null)) {
			throw new IllegalInputException("The field \"actions\" must not be empty or blank.");
		}
	}

	/**
	 * Parses a transition for atomic formulas
	 */
	private void parse() {

		atoms = new ArrayList<AtomicFormula>();

		for(String atom: PredicateSplitter.splitPredicate(condition)) { // Split conditions at junctions

			Expression e = new Expression(atom);
			String[] arguments = e.getMissingUserDefinedArguments();
			
			assert arguments.length > 0;
			
			e.defineArguments(arguments);
			atoms.add(new AtomicFormula(e)); // Atomc formula object
		}
	}

	/**
	 * Audits a transition line
	 * @param current Tuple thats audited
	 * @return New values or null if there are none
	 */
	public Set<Double> audit(Tuple current) {

		Set<Double> values = new HashSet<Double>();
		
		// Create new state and copy attributes
		Tuple s = new Tuple();
		current.forEach((k,v) -> s.put(k,v));

		// Calculate condition with mXParser
		Argument[] arguments = current.genereateArguments();
		Expression c = new Expression(condition,arguments);

		// Check if condition c is true
		if(c.calculate() == (1.0)) {

			for(String action: actions) {
				
				// Calculate action expression with mXParser
				Expression a = new Expression(action,arguments);	
				double result = a.calculate();
				if(ModelManager.getVariable(varId).isInBounds(result)) {
					values.add(result);
				}
			}

		} 

		// Return all new values
		return values;
	}

	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return condition + ":" + actions; 
	}
}