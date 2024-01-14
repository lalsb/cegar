package com.app.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class KripkeStructTest {

	@Test
	void simpleStruct() {
		KripkeStruct m = new KripkeStruct("M");
		m.addNode("A" );
		m.addNode("B" );
		m.addEdge("AB", "A", "B");
	}

}
