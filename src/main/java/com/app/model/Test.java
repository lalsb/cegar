package com.app.model;

import org.graphstream.graph.implementations.SingleGraph;

public class Test {

	public static void main(String[] args) {
		
		System.setProperty("org.graphstream.ui", "javafx");
		org.graphstream.graph.Graph graph = new SingleGraph("Tutorial 1");
		graph.addNode("A" );
		graph.addNode("B" );
		graph.addNode("C" );
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");
		graph.display();


	}

}
