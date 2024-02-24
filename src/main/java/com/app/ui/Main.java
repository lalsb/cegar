package com.app.ui;

import org.mariuszgromada.math.mxparser.License;

import com.app.model.exceptions.ModelInputException;
import com.app.model.exceptions.ModelStateException;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		// MX Parser
		License.iConfirmNonCommercialUse("Linus Alsbach");

		// Load CSS Stylesheet
		Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

		// Load the FXML file
		StackPane rootPane = new StackPane();
		Parent root = FXMLLoader.load(getClass().getResource("Graph.fxml"));

		rootPane.getChildren().add(root);
		
		// Create the scene
		Scene scene = new Scene(rootPane);

		// Set up the stage
		primaryStage.setTitle("Cegar");

		// Set up error handling
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {

			throwable.printStackTrace();
			
			Platform.runLater(() -> {

				if (throwable.getCause().getCause() instanceof ModelInputException) {
					ModelInputException.showErrorMessage(rootPane, throwable.getCause().getCause().getMessage());;
				}
				
				if (throwable.getCause().getCause() instanceof ModelStateException) {
					ModelStateException.showErrorMessage(rootPane, throwable.getCause().getCause().getMessage());;
				}
				
				if (throwable.getCause().getCause() instanceof IllegalArgumentException) {
					ModelStateException.showErrorMessage(rootPane, throwable.getCause().getCause().getMessage());;
				}
				
			});

		});

		try{
			primaryStage.getIcons().add(new Image(getClass().getResource("/resources/icon.png").toURI().toString()));
		} catch (Exception e){System.out.println("Missing file \"icon.png\".");} 

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
