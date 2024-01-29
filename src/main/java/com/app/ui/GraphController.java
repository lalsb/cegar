package com.app.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class GraphController {

    @FXML
    private TextArea consoleTextArea;

    @FXML
    private Button button1;

    @FXML
    private Button button2;

    // Add any additional code/logic for the Graph scene...

    @FXML
    private void initialize() {
        try {
            FXMLLoader childLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
            VBox childNode = childLoader.load();
            MainController childController = childLoader.getController();
            // Do something with the child node and controller
            //childContainer.getChildren().add(childNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle the "Edit Transitions" button action
 	@FXML
 	private void handleEditTransitions() {
 		
 	}

}