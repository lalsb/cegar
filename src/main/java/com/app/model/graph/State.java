package com.app.model.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.app.model.framework.AtomicFormula;
import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;

public class State implements KState{
	
	private String id;
	private Set<Tuple> inverseImage;
	private boolean isInitial;
	
	public State(String id) {
		this.id = id;
		inverseImage = new HashSet<Tuple>();
		isInitial = false;
	}
	
	public Set<Tuple> getInverseImage(){
		return inverseImage;
	}
	
	public void setInverseImage(Set<Tuple> inverseImage){
		this.inverseImage = inverseImage;
	}
	
	public void addToInverseImage(Tuple tuple) {
		inverseImage.add(tuple);
		
		if(ModelManager.getInitialTuples().contains(tuple)) {
			isInitial = true;
		}
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isInitial() {
		return isInitial;
	}
	
	public boolean isEmpty() {
		return inverseImage.isEmpty();
	}
	
	@SmartLabelSource
	public String getLabel() {
		
		switch(ModelManager.getLabel()) {
		case VALUE:
			return inverseImage.toString();
		case ID:
			return getId();
		case ATOMS:
			return getHoldingAtomicFormulas();
		default:
			return getId();
		}
	}
	
private String getHoldingAtomicFormulas() {
		
		List<AtomicFormula> atomicFormulas = ModelManager.getAtomicFormulas().
				stream().distinct().collect(Collectors.toList());
	
		atomicFormulas.removeIf(f -> !f.audit(inverseImage.iterator().next()));

		return atomicFormulas.toString();
	}
	
	@Override
	public String toString() {
		return id;
	}
}
