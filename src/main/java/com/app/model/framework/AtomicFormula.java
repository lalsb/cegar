package com.app.model.framework;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.parsertokens.KeyWord;

public class AtomicFormula {
	
	public Expression e; 
	
	public AtomicFormula() {
		this(null);
	}
	
	public String toString() {
		return e.getCanonicalExpressionString();
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
	
	public Set<String> getVars(){
		
		int keycount = e.getKeyWords("typeid=101").size();
		String[] var_names = new String[keycount];
		
		for(int i=0; i < keycount; i++) {
			var_names[i] = e.getKeyWords("typeid=101").get(i).wordString;
		}
		return new HashSet<>(Arrays.asList(var_names));
		
	}
		
}
