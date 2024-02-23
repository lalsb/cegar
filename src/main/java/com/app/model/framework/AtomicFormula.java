package com.app.model.framework;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mariuszgromada.math.mxparser.Expression;

public class AtomicFormula implements Serializable {

	public Expression e; 
	
	public AtomicFormula() {
		this(null);
	}
	
	@Override
	public String toString() {
		return e.getCanonicalExpressionString();
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
            return true;
        }
		
		if (!(o instanceof AtomicFormula)) {
            return false;
        }
		
		AtomicFormula a = (AtomicFormula) o;
		
		return this.toString().equals(a.toString());
	}
	
	public AtomicFormula(Expression e) {
		this.e = e;
	}
	
	public void setExpression(Expression e) {
		this.e = e;
	}
	
	public Expression getExpression() {
		return e;
	}
	
	public Set<String> getAllVariableIds(){
		
		int keycount = e.getKeyWords("typeid=101").size();
		String[] var_names = new String[keycount];
		
		for(int i=0; i < keycount; i++) {
			var_names[i] = e.getKeyWords("typeid=101").get(i).wordString;
		}
		
		//TODO:
		
		return new HashSet<>(Arrays.asList(var_names));	
	}

	public boolean audit(Tuple current) {
		
		e.removeAllArguments();
		e.addArguments(current.genereateArguments());
		
		if(e.calculate() == 1.0) {
			return true;
		} else
			return false;
		
	}
		
}
