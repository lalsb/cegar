package com.app.model.graph;

/**
 * Labels options include {@code VALUE} for underlying tuple(s), {@code ID}	for arbitrary IDs
 * and {@code ATOMS} for atomic formulas holding.
 */
public enum KStateLabel {
	VALUE("Display value(s)"),
	ID("Display Ids"),
	ATOMS("Display atomic formulas."),;
	
	 private String label;

	KStateLabel(String string) {
		this.label = string;
	}
	
	 public String toString() {
            return label;
        }
}	