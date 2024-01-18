package com.app.model.transition;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Variable {
	private final String name;
	private final DoubleProperty value; // Use DoubleProperty for binding
    private double minValue;
    private double maxValue;
    private String transitionBlock;

    public Variable(String name, double value, double minValue, double maxValue, String transitionBlock) {
        this.name = name;
        this.value = new SimpleDoubleProperty(value);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.transitionBlock = transitionBlock;
    }

    // Getters and setters for the attributes

    public String getName() {
        return name;
    }

    public DoubleProperty valueProperty() {
        return value;
    }
    
    public double getValue() {
        return value.get();
    }

    public void setValue(double value) {
        this.value.set(value);
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
        return String.format("%d", value);
    }

    public String fullString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", transitionBlock=" +  transitionBlock +
                '}';
    }
}

