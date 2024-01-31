package com.app.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.graphstream.ui.fx_viewer.FxDefaultView;
import org.graphstream.ui.fx_viewer.FxViewPanel;

import com.app.model.framework.ModelManager;
import com.app.model.framework.Variable;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class GraphController {

	private ModelManager manager;

	private PrintStream ps;

	FxViewPanel panel;

	@FXML
	private TextArea consoleTextArea;

	@FXML
	private Button genOriginalButton;

	@FXML
	private Button validateButton;

	@FXML
	private Button checkPathButton;

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
	private Slider zoomSlider;

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
		zoomSlider.setDisable(true);

		// Set up ModelManager
		manager = new ModelManager();

		// Set up Zoom Slider
		zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(
					ObservableValue<? extends Number> observableValue, 
					Number oldValue, 
					Number newValue) { 
				panel.getCamera().setViewPercent(newValue.doubleValue());
			}
		});
	}


	/**
	 * Handles the "GenerateOriginalGraph" button action.
	 */
	@FXML
	private void handleGenerateOriginalGraph() {

		List<Variable> variableList = MainController.getVariables();
		manager.load(variableList.toArray(new Variable[0]));

		// For SmartGraph use: manager.generateOriginalGraph().getSmartGraphView();
		panel = manager.generateOriginalGraph().getGraphStreamView();
		graphTab.setContent(panel);
		tabPane.getSelectionModel().select(graphTab);

		validateButton.setDisable(false);
		activateZoomSlider();
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

		panel = manager.generateInitialAbstraction().getGraphStreamView();
		graphTab.setContent(panel);
		tabPane.getSelectionModel().select(graphTab);

		checkPathButton.setDisable(false);
		counterExampleField.setDisable(false);
		activateZoomSlider();
	}

	/**
	 * Handles the "Refine Abstraction" button action.
	 */
	@FXML
	private void handleCheckPath() {

		List<String> finitePath = Arrays.asList(counterExampleField.getText().split(","));	
		manager.splitPATH(finitePath);
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
	
	private void activateZoomSlider() {+
		
		zoomSlider.setDisable(false);
		panel.setOnScroll((ScrollEvent event) -> {
            zoomSlider.setValue(zoomSlider.getValue() + event.getDeltaY()/1000);
        });
	}

}