package com.app.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.graphstream.ui.fx_viewer.FxViewPanel;

import com.app.model.framework.ModelManager;
import com.app.model.framework.Tuple;
import com.app.model.framework.Variable;
import com.app.model.graph.KripkeStruct;
import com.brunomnsilva.smartgraph.containers.ContentZoomPane;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.util.Pair;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class GraphController {

	private ModelManager manager;

	private PrintStream ps;

	FxViewPanel panel;

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
	
	private SmartGraphPanel<String, String> graphView;

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

		// For SmartGraph use:
		panel = manager.generateOriginalGraph().getGraphStreamView();
		graphTab.setContent(panel);
		
		tabPane.getSelectionModel().select(graphTab);

		validateButton.setDisable(false);
	}
	
	@FXML
	private void handleToggleLayout() {
		
		if(toggleLayout.isSelected()){
			graphView.setAutomaticLayout(false);
			toggleLayout.setText("Off");
		} else {
			graphView.setAutomaticLayout(true);
			toggleLayout.setText("On");
		}
	}

	/**
	 * Handles the "Validate" button action.
	 */
	@FXML
	private void handleValidateOriginalGraph() {	
		manager.originalGraph.isValid();
	}

	/**
	 * Handles the "Generate Initial Abstraction" button action.
	 */
	@FXML
	private void handleGenerateInitialAbstraction() {

		List<Variable> variableList = MainController.getVariables();
		manager.load(variableList.toArray(new Variable[0]));

		KripkeStruct abstraction = manager.generateInitialAbstraction();
		
		//panel = abstraction.getGraphStreamView();
		
		graphView = abstraction.getSmartGraphView();
		ContentZoomPane zoomPane = new ContentZoomPane(graphView);
		zoomPane.setStyle("-fx-background-color: #e8faf4");
		
		graphTab.setContent(zoomPane);
		tabPane.getSelectionModel().select(graphTab);
		
		checkPathButton.setDisable(false);
		counterExampleField.setDisable(false);
		loopField.setDisable(false);
		//activateZoomSlider();
		
		// Set up Vertex Listener
		graphView.setVertexDoubleClickAction(graphVertex -> {
			String updated = counterExampleField.getText() + graphVertex.getUnderlyingVertex().element() + ", ";
			counterExampleField.setText(updated);
		});
		
		
		Platform.runLater(() -> {graphView.init();});
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
		KripkeStruct refinement = manager.refine(result.getKey(), result.getValue());
		graphView = refinement.getSmartGraphView();
		ContentZoomPane zoomPane = new ContentZoomPane(graphView);
		
		graphTab.setContent(zoomPane);		
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