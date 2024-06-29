package org.example.filesender;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.util.Objects;

public class FileListing implements Callback<ListView<String>, ListCell<String>> {
    // Handle the file listed on the server
    @Override
    public ListCell<String> call(ListView<String> listView) {
        return new ListCell<>() {
            private final HBox hBox = new HBox();
            private final Label label = new Label();
            private final Region spacer = new Region();
            private final Button optionsButton = new Button("Options");
            private final ContextMenu contextMenu = new ContextMenu();
            private final MenuItem downloadItem = new MenuItem("Download");
            private final MenuItem deleteItem = new MenuItem("Delete");
            private final MenuItem encryptItem = new MenuItem("Encrypt");
            private final MenuItem decryptItem = new MenuItem("Decrypt");

            {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                spacer.setMinWidth(100);
                hBox.setAlignment(Pos.CENTER_LEFT);
                optionsButton.setAlignment(Pos.CENTER_RIGHT);
                optionsButton.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styling/fullPackStyling.css")).toExternalForm());
                optionsButton.getStyleClass().add("button");
                optionsButton.setFont(new Font("Segoe UI light", 10));

                contextMenu.getItems().addAll(downloadItem, deleteItem, encryptItem, decryptItem);
                optionsButton.setOnMouseClicked(event ->
                        contextMenu.show(optionsButton, event.getScreenX(), event.getScreenY())
                );

                hBox.getChildren().addAll(label, spacer, optionsButton);
                setGraphic(hBox);
            }

            @Override
            protected void updateItem(String fileName, boolean empty) {
                super.updateItem(fileName, empty);
                if (empty || fileName == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    label.setText(fileName);
                    downloadItem.setOnAction(event -> ((ClientController) getListView().getScene().getUserData()).handleDownloadFile(fileName));
                    deleteItem.setOnAction(event -> ((ClientController) getListView().getScene().getUserData()).handleDeleteFile(fileName));
                    encryptItem.setOnAction(event -> ((ClientController) getListView().getScene().getUserData()).handleEncryptFile(fileName));
                    decryptItem.setOnAction(event -> ((ClientController) getListView().getScene().getUserData()).handleDecryptFile(fileName));
                    setGraphic(hBox);
                }
            }
        };
    }
}

