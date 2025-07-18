package org.restaurant;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/restaurant/MainView.fxml"));
        TabPane root = loader.load();  // Changed from AnchorPane to TabPane
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    primaryStage.setTitle("Restaurant Management System");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
