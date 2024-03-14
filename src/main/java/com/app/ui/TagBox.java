package com.app.ui;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements a custom HBox storing tags. 
 * @author Linus Alsbach
 */
public class TagBox extends HBox {

	
	// Tags list
	private final ObservableList<String> tempTagList;
	
	// Input field
	private final TextField inputField;
	
	public TextField getField() {
		return inputField;
	}

	public List<String> getTags() {
		
		ArrayList<String> tagList = new ArrayList<String>();
		tagList.addAll(tempTagList);
		
		return tagList; // Return all tags
	}
	
	public void addAll(List<String> tags) {
		
		for(String tag: tags) {
			tempTagList.add(tag);
		}	
	}

	public TagBox() {
		
		// Set up list, field
		tempTagList = FXCollections.observableArrayList();
		inputField = InputController.createTextField(); // Selected field is monitored in MainController
		inputField.setPromptText("Action.");
		inputField.setOnAction(e -> {
			
			String input = inputField.getText();
			if (!input.isEmpty() && !tempTagList.contains(input)) {
				tempTagList.add(input);
				inputField.clear();
			}	
		});
		
		// Set up layout
		HBox.setHgrow(inputField, Priority.ALWAYS);
		setSpacing(3d);

		// Add a Listener to the input field
		tempTagList.addListener((ListChangeListener.Change<? extends String> change) -> {
			while (change.next()) {
				if (change.wasPermutated()) {
					ArrayList<Node> subscriberList = new ArrayList<>(change.getTo() - change.getFrom());
					
					for (int i = change.getFrom(), end = change.getTo(); i < end; i++) {
						subscriberList.add(null);
						subscriberList.set(change.getPermutation(i), getChildren().get(i));
					}
					
					getChildren().subList(change.getFrom(), change.getTo()).clear();
					getChildren().addAll(change.getFrom(), subscriberList);
					
				} else {
					if (change.wasRemoved()) {
						getChildren().subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
					}
					if (change.wasAdded()) {
						getChildren().addAll(change.getFrom(), change.getAddedSubList().stream().map(Tag::new).collect(Collectors.toList()));
					}
				}
			}
			
			inputField.setPromptText("");
			
		});
		
		getChildren().add(inputField);
	}

	private class Tag extends HBox {

		public Tag(String tag) {
			
			// Set up layout
			setSpacing(1d);
			
			// Set up button to remove this action later
			Button rmbutton = new Button("x");
			rmbutton.setOnAction((e) -> tempTagList.remove(tag));
			
			// Action is Displayed in a text field
			TextField action = InputController.createTextField(); // Selected field is monitored in MainController
			action.setText(tag);
			getChildren().addAll(action, rmbutton);
		}
	}

	public void clear() {
		tempTagList.clear();
		inputField.clear();
	}
}