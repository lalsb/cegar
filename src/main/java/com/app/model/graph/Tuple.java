package com.app.model.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mariuszgromada.math.mxparser.Argument;

import com.app.model.framework.AtomicFormula;
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
	
	public Tuple(Set<Tuple> tuples) {
		super();
		tuples.forEach(tuple -> putAll(tuple));
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
		
		switch(ModelManager.getLabel()) {
		case VALUE:
			String mapAsString = this.keySet().stream()
			.map(key -> key + "=" + this.get(key))
			.collect(Collectors.joining(", ", "[", "]"));
			return mapAsString;
		case ID:
			return getId();
		case ATOMS:
			return getHoldingAtomicFormulas();
		default:
			return getId();
		}
	}


	private String getHoldingAtomicFormulas() {
		
		List<AtomicFormula> atomicFormulas = new ArrayList<AtomicFormula>(ModelManager.getAtomicFormulas());
		atomicFormulas.removeIf(f -> !f.audit(this));

		return atomicFormulas.toString();
	}

	@Override
	public String getId() {
		return id;
	}

}
