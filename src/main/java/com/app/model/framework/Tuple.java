package com.app.model.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.mariuszgromada.math.mxparser.Argument;

import com.app.model.graph.KState;
import com.brunomnsilva.smartgraph.graphview.SmartLabelSource;

@SuppressWarnings("serial")
public class Tuple extends HashMap<String, Double> implements KState{	
	
	private String id;
	
	public Tuple() {
        super();
    }
	
	public Tuple(Map<String, Double> map) {
        super(map);
    }
	
	public Tuple(Tuple tuple) {
		super(tuple);
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isInitial() {
		assert ModelManager.getInitialTuples() != null;	
		
		return ModelManager.getInitialTuples().contains(this);
		
	}

	/**
	 * Generates an array of arguments as required for expression checking by mXParser.
	 * @return
	 */
	public Argument[] genereateArguments() {

		Argument[] arguments = new Argument[this.size()];

		int i = 0;
		for(var entry: this.entrySet()) {
			arguments[i] = new Argument(entry.getKey());
			arguments[i].setArgumentValue(entry.getValue());
			i++;
		}

		return arguments;

	}
	
	@SmartLabelSource
	public String getLabel() {
		String mapAsString = this.keySet().stream()
				.map(key -> key + "=" + this.get(key))
				.collect(Collectors.joining(", ", "[", "]"));
		return "#" + id + mapAsString;
	}
	
	
	@Override
	public String getId() {
		return id;
	}

}
