package com.example.pillanalyser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuStart extends Application {

    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage=stage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenue.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 759, 515);
            scene.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Pill Analyser App");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
