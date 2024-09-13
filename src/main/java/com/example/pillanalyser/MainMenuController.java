package com.example.pillanalyser;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;

public class MainMenuController {


    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Button switchToScene2;

    @FXML
    private void switchToScene2(ActionEvent event) throws IOException {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scene2.fxml"));
            Parent root = loader.load();

            // Create the scene
            Scene scene = new Scene(root);

            // Set the scene to the primary stage
            MainMenuStart.primaryStage.setScene(scene);
        } catch (IOException e) {
            // Log the exception
            e.printStackTrace();
        }
    }
}





