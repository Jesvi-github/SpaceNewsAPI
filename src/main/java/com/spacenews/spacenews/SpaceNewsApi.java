package com.spacenews.spacenews;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SpaceNewsApi extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/spacenews/spacenews/hello-view.fxml"));
        Scene scene = new Scene(loader.load());

        // Set the title of the window
        primaryStage.setTitle("Space News");


        primaryStage.getIcons().add(new Image("file:src/main/resources/com/spacenews/spacenews/icon.jpeg"));

        // Set the scene
        primaryStage.setScene(scene);

        // Show the window
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
