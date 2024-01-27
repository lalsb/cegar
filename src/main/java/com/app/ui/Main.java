package com.app.ui;

import org.mariuszgromada.math.mxparser.License;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	// MX Parser
    	License.iConfirmNonCommercialUse("Linus Alsbach");
    	
    	// Load CSS Stylesheet
    	Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
    	
        // Load the FXML file
    	Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
    	 
        // Create the scene
        Scene scene = new Scene(root, 700, 600);

        // Set up the stage
        primaryStage.setTitle("Cegar");
        
        try{
        	primaryStage.getIcons().add(new Image("icon.png"));
        } catch (Exception e){System.out.println("Missing file \"icon.png\".");} 
       
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
