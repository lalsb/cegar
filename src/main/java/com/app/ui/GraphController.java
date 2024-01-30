package com.app.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class GraphController {

    @FXML
    private TextArea consoleTextArea;
    private PrintStream ps ;

    @FXML
    private Button genOriginalButton;

    @FXML
    private Button validateButton;

    // Add any additional code/logic for the Graph scene...

    @FXML
    private void initialize() {
    	
    	ps = new PrintStream(new Console(consoleTextArea)) ;
    	
    	System.setOut(ps);
    	System.setErr(ps);
    	
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

 	@FXML
 	private void handleGenerateOriginalGraph() {
 		System.out.println("handleGenerateOriginalGraph");
 	}
 	
 	@FXML
 	private void handleValidateOriginalGraph() {
 		System.out.println("handleValidateOriginalGraph");
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