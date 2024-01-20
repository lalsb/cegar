package com.app.model.framework;

import java.io.Serializable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Variable implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id;
	private double initValue; // Use DoubleProperty for binding
	private double minValue;
	private double maxValue;
	private String transitionBlock;

	public Variable(String name, double value, double minValue, double maxValue, String transitionBlock) {
		this.id = name;
		this.initValue = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.transitionBlock = transitionBlock;
	}

	// Getters and setters for the attributes

	public String getId() {
		return id;
	}

	public double getValue() {
		return initValue;
	}

	public void setValue(double value) {
		this.initValue = value;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public String getTransitionBlock() {
		return transitionBlock;
	}

	public void setTransitionBlock(String transitionBlock) {
		this.transitionBlock = transitionBlock;
	}

	@Override
	public String toString() {
		return "" + initValue;
	}

	public String fullString() {
		return "Variable{" +
				"name='" + id + '\'' +
				", value=" + initValue +
				", minValue=" + minValue +
				", maxValue=" + maxValue +
				", transitionBlock=" +  transitionBlock +
				'}';
	}

	public boolean isInBounds(double value) {
		if(minValue  <= value && value <= maxValue ) {
			return true;
		} else {
			return false;
		}
	}
}

