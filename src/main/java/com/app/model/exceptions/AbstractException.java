package com.app.model.exceptions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

@SuppressWarnings("serial")
public abstract class AbstractException extends RuntimeException {
	
	public static String heading = "Unable to continue.";
	
	public AbstractException(String msg) {
		super(msg);
	}
	
	public static void showErrorMessage(StackPane rootPane, String message) {
        // Create a dialog layout with a label for the error message
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Label(heading));
        content.setBody(new Label(message));

        // Create a dialog with the custom layout
        JFXDialog dialog = new JFXDialog();
        dialog.setContent(content);
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false); // Disable closing the dialog by clicking outside

        // Create a button to close the dialog
        JFXButton closeButton = new JFXButton("OK");
        closeButton.setOnAction(event -> dialog.close());
        content.setActions(closeButton);

        // Show the dialog on the screen
        dialog.show(rootPane);
    }

}
