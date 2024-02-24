package com.app.model.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Variable implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Variable identifier 
	 */
	private String id;
	/**
	 * Set of initial values
	 */
	private Set<Double> initials;
	/**
	 * Set of values allowed for this variable
	 */
	private Set<Double> domain;
	/**
	 * TransitionBlock associated with this variable
	 */
	private TransitionBlock transitionBlock;


	public Variable(String name, Set<Double> initials, Set<Double> domain, TransitionBlock transitionBlock) {
		this.id = name;
		this.initials = initials;
		this.domain = domain;
		this.transitionBlock = transitionBlock;
	}

	// Getters and setters for the attributes

	public String getId() {
		return id;
	}

	public List<Double> getInitialValues() {
		List<Double> ret = initials.stream().collect(Collectors.toList());
		Collections.sort(ret);

		return ret;	
	}

	public Set<Tuple> getInitials() {
		return initials.stream().map(v -> new Tuple(Map.of(id, v))).collect(Collectors.toSet());
	}

	public TransitionBlock getTransitionBlock() {
		return transitionBlock;
	}


	// Getters for ListView
	public String getInitialsCell() {
		return String.join(",",
				initials.stream().map(x -> String.valueOf(Double.valueOf(x).intValue()))
				.collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
	}

	public String getDomainCell() {
		return String.join(",",
				domain.stream().map(x -> String.valueOf(Double.valueOf(x).intValue()))
				.collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
	}

	public String getTransitionsCell() {
		
		return transitionBlock.toString();
	}


	@Override
	public String toString() {
		return "\n" +
				"Variable{" +
				"id='" + id + '\'' +
				", initials=" + initials +
				", domain=" + domain +
				", transitionBlock=" +  transitionBlock +
				'}';
	}

	public boolean isInBounds(double value) {
		if(domain.contains(value) ) {
			return true;
		} else {
			return false;
		}
	}

	public List<Double> getDomain() {

		List<Double> ret = domain.stream().collect(Collectors.toList());
		Collections.sort(ret);

		return ret;
	}
}

