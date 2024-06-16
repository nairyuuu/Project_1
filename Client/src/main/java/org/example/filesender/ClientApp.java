package org.example.filesender;

import  javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;


public class ClientApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Login");
        primaryStage.getIcons().add(new Image("file:src/main/resources/org/example/filesender/images/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }
}
