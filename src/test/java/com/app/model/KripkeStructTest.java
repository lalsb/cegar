package com.app.model;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.app.model.graph.KripkeStruct;

class KripkeStructTest {

	KripkeStruct m;

	@Test
	void simpleStruct() {
		KripkeStruct k = new KripkeStruct("M");
		k.addNode("A");
		k.addNode("B");
		k.addEdge("AB", "A", "B");

		Assertions.assertTrue(k.edges().anyMatch(edge -> edge.getId().equals("AB")));	
		Stream.of(
				k.getNode("A"),
				k.getNode("B")
				).forEach(Assertions::assertNotNull);

	}

	@BeforeEach
	void KripkeStruct() {

		m = new KripkeStruct("M");

		// States
		m.addNode("A");
		m.addNode("B");
		m.addNode("C");
		m.addNode("D");
		m.addNode("E");
		m.addNode("F");
		m.addNode("G");

		// Transitions
		m.addEdge("1", "A", "B");
		m.addEdge("2", "A", "C");
		m.addEdge("3", "A", "D");
		m.addEdge("4", "A", "E");
		m.addEdge("5", "A", "F");
		m.addEdge("6", "A", "G");

		// States
		m.addNode("H");
		m.addNode("I");
		m.addNode("J");
		m.addNode("K");
		m.addNode("L");
		m.addNode("M");
		m.addNode("N");

		// Transitions
		m.addEdge("7", "H", "I");
		m.addEdge("8", "H", "J");
		m.addEdge("9", "H", "K");
		m.addEdge("10", "H", "L");
		m.addEdge("11", "H", "M");
		m.addEdge("12", "H", "N");
		m.addEdge("0", "A", "H");

		// Transition (Loop)
		m.addEdge("Loop!", "M", "M");

	}

	@Test
	void validateStruct(){
		
		// Assertions
		Assertions.assertTrue(m.isValid());
	}
}
