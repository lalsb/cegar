package com.app.model.graph;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.*;
import org.graphstream.ui.javafx.FxGraphRenderer;

import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GraphStreamTest2 extends Application{
	
	protected String styleSheet = "graph {padding: 60px;}";


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage ignored) {

		KripkeStruct g = new KripkeStruct("g");

		System.setProperty("org.graphstream.ui", "javafx"); // set ui library
		
		Node nodeA = g.addNode("A");
		double[] values = {1.0, 2.0, 3.0};
		nodeA.setAttribute("ui.label", "Node A");
		g.addNode("B");
		g.addNode("C");
		g.addNode("D");
		g.addNode("E");
		g.addNode("F");
		g.addNode("G");

		g.addEdge("1", "A", "B");
		g.addEdge("2", "A", "C");
		g.addEdge("3", "A", "D");
		g.addEdge("4", "A", "E");
		g.addEdge("5", "A", "F");
		g.addEdge("6", "A", "G");

		g.addNode("H");
		g.addNode("I");
		g.addNode("J");
		g.addNode("K");
		g.addNode("L");
		g.addNode("M");
		g.addNode("N");

		g.addEdge("7", "H", "I");
		g.addEdge("8", "H", "J");
		g.addEdge("9", "H", "K");
		g.addEdge("10", "H", "L");
		g.addEdge("11", "H", "M");
		g.addEdge("12", "H", "N");
		g.addEdge("Loop!", "M", "M");

		g.addEdge("0", "A", "H");

		/**
		g.setAttribute("ui.quality");
		g.nodes().forEach(node -> node.setAttribute("ui.label", node.getId()));
		g.nodes().forEach(node -> node.setAttribute("ui.style", "text-alignment: at-right; text-size: 20; text-mode: normal;"));
		
		
		FxViewer v = new FxViewer(g, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		
		v.enableAutoLayout();
		FxViewPanel panel = (FxViewPanel)v.addDefaultView(false, new FxGraphRenderer());
		
		g.setAttribute("ui.antialias");
		g.setAttribute("ui.quality");
		g.setAttribute("ui.stylesheet", styleSheet);
		**/
		
		
		//SmartGraphPanel<String, String> panel = SmartGraphWrapper.getInstance().generateJavaFXView(g); // Für Wrapper-Test!!
		SmartGraphPanel<String, String> panel = g.getSmartGraphView();
		
		Button nodes = new Button("Nodes");
		nodes.setOnAction((event)-> {

			List<Node> nodeList = new ArrayList<>();
			g.nodes().forEach(nodeList::add);
			System.out.println(nodeList);
			;});
		
		Button edges = new Button("Edges");
		edges.setOnAction((event)-> {

			List<Edge> edgeList = new ArrayList<>();
			g.edges().forEach(edgeList::add);
			System.out.println(edgeList);
			;});	
		
		FlowPane flowPane = new FlowPane();
		flowPane.setPadding(new Insets(10, 0, 0, 0));
		flowPane.getChildren().addAll(nodes, edges);
		flowPane.setHgap(2);

		Label title = new Label("Kripke structure:");
		title.setPadding(new Insets(10, 10, 10, 10));

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(10, 10, 10, 10));
		root.setTop(title);
		root.setCenter(panel);
		root.setBottom(flowPane);

		Scene scene = new Scene(root, 1024, 768);
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.setTitle("JavaFXGraph Visualization");
		stage.setScene(scene);
		stage.show();
		
		panel.init(); // Für Wrapper-Test!!
	}
}
