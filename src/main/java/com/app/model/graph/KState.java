package com.app.model.graph;

import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;

public interface KState {
	
	public abstract boolean isInitial();
	
	public String getId();
	
	@SmartLabelSource
	public String getLabel();

}
