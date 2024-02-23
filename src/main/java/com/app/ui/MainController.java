package com.app.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.app.model.exceptions.IllegalInputException;
import com.app.model.framework.TransitionBlock;
import com.app.model.framework.TransitionLine;
import com.app.model.framework.Variable;
import com.app.util.TagBox;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * Controller for the Transition Part
 * 
 * @author Linus Alsbach
 */
public class MainController {

	private ObservableList<Variable> tempVariableList = FXCollections.observableArrayList();

	private List<Variable> variableList;

	public static TextField selectedTextField;

	// Table, TextFields
	@FXML
	private TableView<Variable> variableTableView;

	@FXML
	private TableColumn<Variable, String> nameColumn;

	@FXML
	private TableColumn<Variable, String> initialsColumn;

	@FXML
	private TableColumn<Variable, String> domainColumn;

	@FXML
	private TableColumn<Variable, String> transitionsColumn;

	@FXML
	private TextField nameField;

	@FXML
	private TextField initialValuesField;

	@FXML
	private TextField domainField;

	// Buttons
	@FXML
	private Button addButton;

	@FXML
	private Button deleteButton;

	@FXML
	private Button saveButton;

	@FXML
	private Button loadButton;

	@FXML
	private VBox transitionBox;

	@FXML
	private Button AddTransitionButton;

	@FXML
	private TagBox elseBox;

	// Initialize method is called after the FXML file is loaded
	@FXML
	public void initialize() {	

		// Set up else Box
		elseBox.getField().setPromptText("Enter a compulsory else action.");

		// Set up the columns in the TableView
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
		initialsColumn.setCellValueFactory(new PropertyValueFactory<>("InitialsCell"));
		domainColumn.setCellValueFactory(new PropertyValueFactory<>("DomainCell"));
		transitionsColumn.setCellValueFactory(new PropertyValueFactory<>("TransitionsCell"));

		// Add a listener to the selected item property
		variableTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Variable>() {
			@Override
			public void changed(ObservableValue<? extends Variable> observable, Variable oldValue, Variable newValue) {
				if (newValue != null) {
					// Clear fields
					transitionBox.getChildren().clear();
					// Fill name field
					nameField.setText(newValue.getId());
					// Fill intiial values field
					initialValuesField.setText(String.join(",",
							newValue.getInitialValues().stream().map(x -> String.valueOf(Double.valueOf(x).intValue()))
							.collect(ArrayList::new, ArrayList::add, ArrayList::addAll)));
					// Fill Domain field
					domainField.setText(String.join(",",
							newValue.getDomain().stream().map(x -> String.valueOf(Double.valueOf(x).intValue()))
							.collect(ArrayList::new, ArrayList::add, ArrayList::addAll)));
					// Fill case block
					List<TransitionLine> lines = newValue.getTransitionBlock().transitions();	
					
					for (TransitionLine line : lines.subList(0, lines.size() - 1)) {
						_handleAddTransition(line);
					}
					// Fill else case
					elseBox.clear();
					elseBox.addAll(lines.get(lines.size() -1).getActions());

					

				}
			}
		});

		// Set up Listener to update selected TextField
		nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				selectedTextField = nameField;
			}
		});
		initialValuesField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				selectedTextField = initialValuesField;
			}
		});
		domainField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				selectedTextField = domainField;
			}
		});

		// Set up Variable List
		variableList = new LinkedList<Variable>();

		// Set the items in the TableView
		variableTableView.setItems(tempVariableList);

		// Set up selection model for TableView
		variableTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		handleAddTransition();
	}

	// Regular methods:

	/**
	 * Returns a List of all Variable instances in the TableView.
	 * 
	 * @return List of type Variable
	 */
	public List<Variable> getVariables() {
		variableList.clear();
		variableList.addAll(tempVariableList);
		return variableList;
	}

	// Methods handling the Button Actionevents:

	/**
	 * Handles the "Add" button action.
	 */
	@FXML
	private void handleAddVariable() throws IllegalInputException {
			validate();

		// Get data from fields
		String name = nameField.getText();
		Set<Double> initials = new HashSet<Double>();
		Set<Double> domain = new HashSet<Double>();

		List<String> isource = Arrays.asList(initialValuesField.getText().split(","));
		isource.forEach(string -> initials.add(parseDouble(string)));

		List<String> dsource = Arrays.asList(domainField.getText().split(","));
		dsource.forEach(string -> domain.add(parseDouble(string)));

		List<TransitionLine> transitions = new ArrayList<TransitionLine>();
		
		for (Node hbox : transitionBox.getChildren()) {

			String condition = ((TextField) ((HBox) hbox).getChildren().get(0)).getText();
			List<String> actions = ((TagBox) ((HBox) hbox).getChildren().get(2)).getTags();	

			TransitionLine line = new TransitionLine(name, condition, actions);

			transitions.add(line);
		}

		transitions.add(new TransitionLine(name, "1.0", elseBox.getTags())); // Add else Tag last

		TransitionBlock block = new TransitionBlock(name, transitions.toArray(new TransitionLine[0]));

		// Create a new Variable object
		Variable variable = new Variable(name, initials, domain, block);

		// Add the variable to the TableView
		tempVariableList.add(variable);

		// Clear the input fields
		clearFields();
	}

	/**
	 * Handles the "Delete" button action.
	 */
	@FXML
	private void handleDeleteVariable() {
		// Get the selected variable
		Variable selectedVariable = variableTableView.getSelectionModel().getSelectedItem();

		if (selectedVariable != null) {
			// Remove the selected variable from the TableView
			tempVariableList.remove(selectedVariable);
		}

		clearFields();
	}

	/**
	 * Handles the "Save" button action.
	 */
	@FXML
	private void handleSaveVariables() {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Variables");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized Files", "*.ser"));
			File file = fileChooser.showSaveDialog(variableTableView.getScene().getWindow());

			if (file != null) {
				try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {

					// Save the variables from the observable list
					variableList.clear();
					variableList.addAll(tempVariableList);
					oos.writeObject(variableList);
				}
			}
		} catch (IOException e) {
			e.printStackTrace(); // Handle or log the exception as needed
		}
	}

	/**
	 * Handles the "Load" button action.
	 */
	@SuppressWarnings("unchecked")
	@FXML
	private void handleLoadVariables() {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Load Variables");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized Files", "*.ser"));
			File file = fileChooser.showOpenDialog(variableTableView.getScene().getWindow());

			if (file != null) {
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
					List<Variable> loadedVariables = (List<Variable>) ois.readObject();

					// Load all the variables into the observable list
					variableList.clear();
					variableList.addAll(loadedVariables);
					tempVariableList.setAll(loadedVariables);
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace(); // Handle or log the exception as needed
		}
	}

	/**
	 * Handle the "+" Button action.
	 */
	@FXML
	private void handleAddTransition() {
		_handleAddTransition(null);
	}

	/**
	 * Handle the "+" Button action and fill TextFields with TransitionLine attributes
	 * @param line TransitionLine
	 */
	private void _handleAddTransition(TransitionLine line) {

		TextField condition = createTextField();
		condition.setPrefSize(200.0, 32.0);
		condition.setMinWidth(150);
		condition.setPromptText("Condition.");


		TagBox actionBox = new TagBox();
		actionBox.setPrefSize(400.0, 32.0);

		Button colon = new Button(":");
		colon.setPrefSize(32.0, 32.0);
		colon.setDisable(true);
		Button rmbutton = new Button();
		rmbutton.setText("-");
		HBox hbox = new HBox(condition, colon, actionBox, rmbutton);
		hbox.setSpacing(3.0);
		rmbutton.setOnAction((e) -> {
			transitionBox.getChildren().remove(hbox);
		});

		if (line != null) {
			actionBox.addAll(line.getActions());
			condition.setText(line.getCondition());
		}

		transitionBox.getChildren().add(hbox);
	}

	/**
	 * Handle the "Create Graph" button action
	 */
	@FXML
	private void handleClear() {
		clearFields();
	}

	@FXML
	private void addAnd() {
		putSymbol("&");
	}

	@FXML
	private void addOr() {
		putSymbol("|");
	}

	@FXML
	private void addNot() {
		putSymbol("!");
	}

	@FXML
	private void addEquals() {
		putSymbol("=");
	}

	@FXML
	private void addTrue() {
		putSymbol("1");
	}

	@FXML
	private void addFalse() {
		putSymbol("0");
	}

	@FXML
	private void addLessThan() {
		putSymbol("<");
	}

	@FXML
	private void addGreaterThan() {
		putSymbol(">");
	}

	/**
	 * Helper method to append a symol to the last selected Textfield.
	 * @param symbol String thats appended.
	 */
	private void putSymbol(String symbol) {

		if (selectedTextField != null) {
			selectedTextField.appendText(symbol);

			Platform.runLater(() -> {
				// Select nothing in the text field
				selectedTextField.selectEnd();
			});

			selectedTextField.requestFocus();
		}
	}

	/**
	 * Helper method to create textFields
	 * @return TextField TextField instance
	 */
	public static TextField createTextField() {
		TextField textField = new TextField();

		// Add an event handler to track the focus
		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				// Set the currently selected TextField
				selectedTextField = textField;
			}
		});

		return textField;
	}

	/**
	 * Helper method to clear input fields. More precisely, clears all input fields
	 * and resets the area with all the transitions
	 */
	private void clearFields() {
		nameField.clear();
		initialValuesField.clear();
		domainField.clear();
		transitionBox.getChildren().clear();
		elseBox.clear();
		variableTableView.getSelectionModel().clearSelection();
	}

	/**
	 * Helper method to validate input data, handling possible exceptions
	 * @throws IllegalInputException
	 */
	private void validate() throws IllegalInputException {

		for (TextField field : Arrays.asList(nameField, initialValuesField, domainField)) {
			if (field.getText().isBlank()) {
				throw new IllegalInputException(String.format("The field \"%s\" must not be empty or blank.", field.getId()));
			}
		}
		
		if (elseBox.getTags().isEmpty() || elseBox.getTags().contains("")) {
			throw new IllegalInputException(String.format("The field \"%s\" must not be empty or blank.", elseBox.getId()));
		}
	}

	/**
	 * Helper method to parse Double from a String, handling possible exceptions
	 * 
	 * @param text String
	 * @return Double value
	 */
	private Double parseDouble(String text) throws IllegalInputException{
		try {
		return Double.parseDouble(text);
		} catch(NumberFormatException e) {
			throw new IllegalInputException(String.format("Unable to format %s as a number. Please enter numeric values only.", text));
		}
	}
}