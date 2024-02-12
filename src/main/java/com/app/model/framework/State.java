package com.app.model.framework;

import java.util.HashSet;
import java.util.Set;

public class State {
	
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
	
	@Override
	public String toString() {
		
		return "#" + id + " = " + inverseImage;
		
	}

}
