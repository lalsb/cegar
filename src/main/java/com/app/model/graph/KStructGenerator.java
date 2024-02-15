package com.app.model.graph;

public interface KStructGenerator<T extends KState> {
	
	public KStruct<T> generateStruct();

}
