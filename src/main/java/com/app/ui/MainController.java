package com.app.ui;

import java.util.Arrays;
import java.util.List;

import com.app.model.exceptions.VariableInvalidExpection;
import com.app.model.transition.TransitionController;
import com.app.model.transition.Variable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class MainController {

	@FXML
	private TableView<Variable> variableTableView;

	@FXML
	private TableColumn<Variable, String> nameColumn;

	@FXML
	private TableColumn<Variable, Double> valueColumn;

	@FXML
	private TextField nameField;

	@FXML
	private TextField valueField;

	@FXML
	private TextField minValueField;

	@FXML
	private TextField maxValueField;

	@FXML
	private TextArea transitionBlockField;

	// Buttons
	@FXML
	private Button addButton;

	@FXML
	private Button deleteButton;

	// Initialize method is called after the FXML file is loaded
	@FXML
	private void initialize() {
		// Set up the columns in the TableView
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

		// Add a listener to the selected item property
		variableTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Variable>() {
			@Override
			public void changed(ObservableValue<? extends Variable> observable, Variable oldValue, Variable newValue) {
				if (newValue != null) {
					// Fill the fields with the values from the selected item
					nameField.setText(newValue.getName());
					valueField.setText(String.valueOf(newValue.getValue()));
					minValueField.setText(String.valueOf(newValue.getMinValue()));
					maxValueField.setText(String.valueOf(newValue.getMaxValue()));
					transitionBlockField.setText(newValue.getTransitionBlock());
				}
			}
		});

		// Set up selection model for TableView
		variableTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}

	// Handle the "Add" button action
	@FXML
	private void handleAddVariable() {

		try {
			validate();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Get data from fields
		String name = nameField.getText();
		Double value = parseDouble(valueField.getText());
		Double minValue = parseDouble(minValueField.getText());
		Double maxValue = parseDouble(maxValueField.getText());
		String transitionBlock = transitionBlockField.getText();

		// Create a new Variable object (you should have a Variable class)
		Variable variable = new Variable(name, value, minValue, maxValue, transitionBlock);

		// Add the variable to the TableView
		variableTableView.getItems().add(variable);

		// Clear the input fields
		clearFields();
	}

	// Handle the "Delete" button action
	@FXML
	private void handleDeleteVariable() {
		// Get the selected variable
		Variable selectedVariable = variableTableView.getSelectionModel().getSelectedItem();

		if (selectedVariable != null) {
			// Remove the selected variable from the TableView
			variableTableView.getItems().remove(selectedVariable);
		}
	}

	// Handle the "Create Graph" button action
	@FXML
	private void handleCreateGraph() {

		// Get the  variables
		List<Variable> varTable = variableTableView.getItems();

		// Call TransitionController
		//TransitionController.getInstance().createStruct(varTable);

		Stage graphStage = new Stage();
		graphStage.initModality(Modality.APPLICATION_MODAL);
		graphStage.setTitle("Graph");
		
		try{
			graphStage.getIcons().add(new Image("icon.png"));
		} catch (Exception e){
			System.out.println("Missing file \"icon.png\".");
		} 
		
		// Set the scene with the SmartGraphPanel
		graphStage.setScene(new Scene(new BorderPane(), 600, 400));

		// Show the graph window
		graphStage.show();

	}

	// Helper method to clear input fields
	private void clearFields() {
		nameField.clear();
		valueField.clear();
		minValueField.clear();
		maxValueField.clear();
		transitionBlockField.clear();
	}

	// Helper method to validate input data, handling possible exceptions
	private void validate() {
		for (TextField field : Arrays.asList(nameField, valueField, minValueField, maxValueField)) {
			if(field.getText().isBlank()) {
				throw new VariableInvalidExpection(String.format("Field %s is required.", field.getId()));
			}
		}
	}

	// Helper method to parse Double from a String, handling possible exceptions
	private Double parseDouble(String text) {
		try {
			return Double.parseDouble(text);
		} catch (NumberFormatException e) {
			// Handle the exception as needed (e.g., show an error message)
			return null; // Or another default value, depending on your requirements
		}
	}
}