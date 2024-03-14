package com.app.model.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import com.app.model.exceptions.ModelInputException;
import com.app.model.graph.ModelManager;
import com.app.model.graph.Tuple;

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
	private List<AtomicFormula> atomicFormulas;

	public TransitionLine(String varId, String condition, List<String> action) {
		this.varId = varId;
		this.condition = condition;
		this.actions = action;
		atomicFormulas = new ArrayList<AtomicFormula>();
		isValid();
		parse();
	}

	public List<String> getActions() {
		return actions;
	}

	public String getCondition() {
		return condition;
	}

	public List<AtomicFormula> getAtomicFormulas(){
		//TODO: entefernen
		atomicFormulas = new ArrayList<AtomicFormula>();
		parse();
		return atomicFormulas;
	}

	/**
	 * Checks Transition Block6
	 * @throws IllegalArgumentException
	 */
	private void isValid() throws ModelInputException{

		if(condition == null || condition.isBlank()) {
			throw new ModelInputException("The field \"condition\" must not be empty or blank.");
		}
		
		if(actions == null ||actions.isEmpty() || actions.contains("") || actions.contains(null)) {
			throw new ModelInputException("The field \"actions\" must not be empty or blank.");
		}
	}

	/**
	 * Parses a transition for atomic formulas
	 */
	private void parse() {


		for(String atom: AtomicFormulaScanner.scan(condition)) { // Split conditions at junctions

			Expression e = new Expression(atom);
			String[] arguments = e.getMissingUserDefinedArguments();
			
			assert arguments.length > 0;
			
			e.defineArguments(arguments);
			atomicFormulas.add(new AtomicFormula(e)); // Atomc formula object
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
		
		
		if(!c.checkSyntax()) {
			System.out.println(c.getErrorMessage());
			throw new ModelInputException("Unable to calculate \"" + c.getCanonicalExpressionString() +
					"\" while processing conditions in transition block \"" + varId + "\"");
		}

		// Check if condition c is true
		if(c.calculate() == (1.0)) {

			for(String action: actions) {
				
				// Calculate action expression with mXParser
				Expression a = new Expression(action,arguments);	
				
				if(!a.checkSyntax()) {
					System.out.println(a.getErrorMessage());
					throw new ModelInputException("Unable to calculate \"" + a.getCanonicalExpressionString() +
							"\" while processing actions in transition block \"" + varId + "\"");
				}
				
				double result = a.calculate();
				
				if(Double.isNaN(result)) {
					throw new ModelInputException("Unable to process the ");
				}
				
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