package com.app.model.graph;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.mariuszgromada.math.mxparser.Argument;

@SuppressWarnings("serial")
public class State extends HashMap<String, Double> {;

	private String id;

	public State() {
		this("PLACEHOLDER ID");
	}

	public State(String id) {
		super();
		this.id = id;
	}

	public String id() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean equals(State other) {
		return super.equals(other);
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
		String mapAsString = id + ":" + this.keySet().stream()
				.map(key -> key + "=" + this.get(key))
				.collect(Collectors.joining(", ", "[", "]"));
		return mapAsString;
	}

}
