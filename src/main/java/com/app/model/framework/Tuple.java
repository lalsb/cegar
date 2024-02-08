package com.app.model.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.mariuszgromada.math.mxparser.Argument;

@SuppressWarnings("serial")
public class Tuple extends HashMap<String, Double> {	
	
	public Tuple() {
        super();
    }
	
	public Tuple(Map<String, Double> map) {
        super(map);
    }
	
	public Tuple(Tuple tuple) {
		super(tuple);
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

	@Override
	public String toString() {
		String mapAsString = this.keySet().stream()
				.map(key -> key + "=" + this.get(key))
				.collect(Collectors.joining(", ", "[", "]"));
		return mapAsString;
	}

}
