package com.app.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.app.model.framework.ModelManager;
import com.app.model.framework.Tuple;
import com.app.model.framework.Variable;
import com.app.model.graph.AbstractStruct;
import com.brunomnsilva.smartgraph.containers.ContentZoomPane;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.util.Pair;

public class GraphController {

	private ModelManager manager;

	private PrintStream ps;

	Pair<String, Set<Tuple>> result;

	@FXML
	private TextArea consoleTextArea;

	@FXML
	private Button genOriginalButton;

	@FXML
	private Button validateButton;

	@FXML
	private Button checkPathButton;

	@FXML
	private Button refineButton;

	@FXML
	private TabPane tabPane;

	@FXML
	private Tab graphTab;

	@FXML
	private Tab transitionsTab;

	@FXML
	MainController MainController;

	@FXML
	private TextField counterExampleField;

	@FXML
	private TextField loopField;
	
	@FXML
	private ToggleButton toggleLayout;
	
	private SmartGraphPanel<?,?> graphPanel;

	// Initialize method is called after the FXML file is loaded in Main
	@FXML
	private void initialize() {
		
		// Set up Console Output
		ps = new PrintStream(new Console(consoleTextArea));
		System.setOut(ps);

		// Disable functionality that is not yet usable
		validateButton.setDisable(true);
		checkPathButton.setDisable(true);
		counterExampleField.setDisable(true);
		loopField.setDisable(true);
		refineButton.setDisable(true);
		toggleLayout.setDisable(true);

		// Set up ModelManager
		manager = new ModelManager();
	}


	/**
	 * Handles the "GenerateOriginalGraph" button action.
	 */
	@FXML
	private void handleGenerateOriginalGraph() {

		List<Variable> variableList = MainController.getVariables();
		manager.load(variableList.toArray(new Variable[0]));

		// Set up Pane
		graphPanel = manager.generateOriginalGraph().getSmartGraphView();
		ContentZoomPane zoomPanel = new ContentZoomPane(graphPanel);
		zoomPanel.setStyle("-fx-background-color: #e8faf4");
		graphTab.setContent(zoomPanel);
		tabPane.getSelectionModel().select(graphTab);

		validateButton.setDisable(false);
		toggleLayout.setDisable(false);
		
		Platform.runLater(() -> {graphPanel.init();});
		
		System.out.println("Finished handleGenerateOriginalGraph");
	}
	
	@FXML
	private void handleToggleLayout() {
		
		if(toggleLayout.isSelected()){
			graphPanel.setAutomaticLayout(false);
			toggleLayout.setText("Off");
		} else {
			graphPanel.setAutomaticLayout(true);
			toggleLayout.setText("On");
		}
	}

	/**
	 * Handles the "Validate" button action.
	 */
	@FXML
	private void handleValidateOriginalGraph() {	
		manager.originalGraph.validate();
	}

	/**
	 * Handles the "Generate Initial Abstraction" button action.
	 */
	@FXML
	private void handleGenerateInitialAbstraction() {

		List<Variable> variableList = MainController.getVariables();
		manager.load(variableList.toArray(new Variable[0]));

		AbstractStruct abstraction = manager.generateInitialAbstraction();
		
		//panel = abstraction.getGraphStreamView();
		
		// Set up Pane
		graphPanel = abstraction.getSmartGraphView();
		ContentZoomPane zoomPanel = new ContentZoomPane(graphPanel);
		zoomPanel.setStyle("-fx-background-color: #e8faf4");
		graphTab.setContent(zoomPanel);
		tabPane.getSelectionModel().select(graphTab);
		
		checkPathButton.setDisable(false);
		counterExampleField.setDisable(false);
		loopField.setDisable(false);
		toggleLayout.setDisable(false);
		
		// Set up Vertex Listener
		graphPanel.setVertexDoubleClickAction(graphVertex -> {
			String updated = counterExampleField.getText() + graphVertex.getUnderlyingVertex().element() + ", ";
			counterExampleField.setText(updated);
		});
		
		
		Platform.runLater(() -> {graphPanel.init();});
	}

	/**
	 * Handles the "Check Path" button action.
	 */
	@FXML
	private void handleCheckPath() {

		List<String> finitePath = Arrays.asList(counterExampleField.getText().split(","));	
		result = manager.splitPath(finitePath);

		if(result == null) {
			System.out.println("Path corresponds to real counterexampe");
		} else {
			System.out.println("Counterexample Path spurious. Failure State:" + result.getKey() + ", S = " + result.getValue());
			refineButton.setDisable(false);
		}
	}

	/**
	 * Handles the "Refine Abstraction" button action.
	 */
	@FXML
	private void handleRefineAbstraction() {
		AbstractStruct refinement = manager.refine(result.getKey(), result.getValue());
		graphPanel = refinement.getSmartGraphView();
		ContentZoomPane zoomPanel = new ContentZoomPane(graphPanel);
		zoomPanel.setStyle("-fx-background-color: #e8faf4");
		graphTab.setContent(zoomPanel);
		tabPane.getSelectionModel().select(graphTab);
		
		Platform.runLater(() -> {graphPanel.init();});
	}

	public class Console extends OutputStream {
		private TextArea console;

		public Console(TextArea console) {
			this.console = console;
		}

		public void appendText(String valueOf) {

			Platform.runLater(() -> console.appendText(valueOf));
		}

		public void write(int b) throws IOException {
			appendText(String.valueOf((char)b));
		}
	}
}