package com.app.model;

import java.util.Set;

import org.mariuszgromada.math.mxparser.Expression;

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
}
