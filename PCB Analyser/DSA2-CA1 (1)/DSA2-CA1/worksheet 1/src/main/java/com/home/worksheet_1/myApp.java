package com.home.worksheet_1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class myApp extends Application {
    static boolean dialogueBox = false;
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(myApp.class.getResource("homeView.fxml"));
        Scene scene = new Scene(root, 1000, 750);
        stage.setTitle("Home");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}