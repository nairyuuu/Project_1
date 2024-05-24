package org.example.filesender;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class FileSenderApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("FileSenderApp.fxml")));
        primaryStage.setTitle("FileSender");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
