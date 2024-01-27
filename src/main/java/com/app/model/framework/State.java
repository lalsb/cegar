package com.app.model.framework;

import java.util.Set;

public class State {
	
	private String id;
	private Set<Tuple> inverseImage;
	
	public State(String id) {
		this.id = id;
	}
	
	public Set<Tuple> getInverseImage(){
		return inverseImage;
	}

	public String getId() {
		return id;
	}

}
