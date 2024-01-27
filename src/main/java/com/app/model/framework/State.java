package com.app.model.framework;

import java.util.HashSet;
import java.util.Set;

public class State {
	
	private String id;
	private Set<Tuple> inverseImage;
	
	public State(String id) {
		this.id = id;
		inverseImage = new HashSet<Tuple>();
	}
	
	public Set<Tuple> getInverseImage(){
		return inverseImage;
	}
	
	public void setInverseImage(Set<Tuple> inverseImage){
		this.inverseImage = inverseImage;
	}
	
	public void addToInverseImage(Tuple tuple) {
		inverseImage.add(tuple);
	}

	public String getId() {
		return id;
	}

}
