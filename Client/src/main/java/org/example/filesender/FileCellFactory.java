package org.example.filesender;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.util.Objects;

public class FileCellFactory implements Callback<ListView<String>, ListCell<String>> {
    // Handle the file listed on the server
    @Override
    public ListCell<String> call(ListView<String> listView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(String fileName, boolean empty) {
                super.updateItem(fileName, empty);
                if (empty || fileName == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox();
                    Label label = new Label(fileName);
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    spacer.setMinWidth(100);
                    Button optionsButton = new Button("Options");
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    optionsButton.setAlignment(Pos.CENTER_RIGHT);
                    optionsButton.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styling/fullpackstyling.css")).toExternalForm());
                    optionsButton.getStyleClass().add("button");
                    optionsButton.setFont(new Font("Segoe UI light", 10));


                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem downloadItem = new MenuItem("Download");
                    downloadItem.setOnAction(event -> ((ClientController) getListView().getScene().getUserData()).handleDownloadFile(fileName));
                    MenuItem deleteItem = new MenuItem("Delete");
                    deleteItem.setOnAction(event -> ((ClientController) getListView().getScene().getUserData()).handleDeleteFile(fileName));
                    contextMenu.getItems().addAll(downloadItem, deleteItem);

                    optionsButton.setOnMouseClicked(event ->
                            contextMenu.show(optionsButton, event.getScreenX(), event.getScreenY())
                    );

                    hBox.getChildren().addAll(label, spacer, optionsButton);
                    setGraphic(hBox);
                }
            }
        };
    }
}

