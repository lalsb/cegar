/*
 * The MIT License
 *
 * JavaFXSmartGraph | Copyright 2023  brunomnsilva@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.app.model;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author brunomnsilva
 */
public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage ignored) {

		Graph<String, String> g = new GraphEdgeList<>();
		
		g.insertVertex("A");
		g.insertVertex("B");
		g.insertVertex("C");
		g.insertVertex("D");
		g.insertVertex("E");
		g.insertVertex("F");
		g.insertVertex("G");

		g.insertEdge("A", "B", "1");
		g.insertEdge("A", "C", "2");
		g.insertEdge("A", "D", "3");
		g.insertEdge("A", "E", "4");
		g.insertEdge("A", "F", "5");
		g.insertEdge("A", "G", "6");

		g.insertVertex("H");
		g.insertVertex("I");
		g.insertVertex("J");
		g.insertVertex("K");
		g.insertVertex("L");
		g.insertVertex("M");
		g.insertVertex("N");

		g.insertEdge("H", "I", "7");
		g.insertEdge("H", "J", "8");
		g.insertEdge("H", "K", "9");
		g.insertEdge("H", "L", "10");
		g.insertEdge("H", "M", "11");
		g.insertEdge("H", "N", "12");
		g.insertEdge("M", "M", "Loop!");

		g.insertEdge("A", "H", "0");

		SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
		SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(g, strategy);
		
		
		Button neu = new Button("Neu");
		Button speichern = new Button("Speichern");
		Button loeschen = new Button("Löschen");	
		FlowPane flowPane = new FlowPane();
		flowPane.setPadding(new Insets(10, 0, 0, 0));
		flowPane.getChildren().addAll(neu, speichern, loeschen);
		flowPane.setHgap(2);
		
		Label title = new Label("Kripke Struct:");
		title.setPadding(new Insets(10, 10, 10, 10));
		
		
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(10, 10, 10, 10));
		root.setTop(title);
		root.setCenter(graphView);
		root.setBottom(flowPane);
			
		Scene scene = new Scene(root, 1024, 768);
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.setTitle("JavaFXGraph Visualization");
		stage.setScene(scene);
		stage.show();

		graphView.init();
		graphView.setAutomaticLayout(true);

	}
}