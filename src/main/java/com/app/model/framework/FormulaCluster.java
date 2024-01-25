package com.app.model.framework;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.app.model.graph.State;

public class FormulaCluster {
	
	public int nodeID;
	
	List<AtomicFormula> atoms;
	Set<String> varNames;
	
	public FormulaCluster() {
		atoms = new LinkedList<AtomicFormula>();
		varNames = new HashSet<String>();
	}
	
	public boolean addFormula(AtomicFormula formula) {	
		formula.getVars().addAll(varNames);	
		return atoms.add(formula);
	}
	
	public Set<String> getVariableCluster(){
		return varNames;
	}
	
	public Set<State> cartesianProduct(Map<String, Variable> vars){
	
		Set<State> states = new HashSet<State>();
		
		State current = new State(Integer.toString(nodeID));// Create state
		
		for(String name: varNames) {
			current.put(name, vars.get(name).getValue());
		}
		
		
		
		
		return null;
	}
	
	
}
