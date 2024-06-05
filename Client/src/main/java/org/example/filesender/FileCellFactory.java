package org.example.filesender;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class FileCellFactory implements Callback<ListView<String>, ListCell<String>> {
    // Handle the file listed on the server
    @Override
    public ListCell<String> call(ListView<String> listView) {
        return new ListCell<String>() {
            @Override
            protected void updateItem(String fileName, boolean empty) {
                super.updateItem(fileName, empty);
                if (empty || fileName == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox();
                    Label label = new Label(fileName);
                    Pane spacer = new Pane();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    Button optionsButton = new Button("Options");
                    optionsButton.setAlignment(Pos.CENTER_RIGHT);

                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem downloadItem = new MenuItem("Download");
                    downloadItem.setOnAction(event -> {
                        ((ClientController) getListView().getScene().getUserData()).handleDownloadFile(fileName);
                    });
                    MenuItem deleteItem = new MenuItem("Delete");
                    deleteItem.setOnAction(event -> {
                        ((ClientController) getListView().getScene().getUserData()).handleDeleteFile(fileName);
                    });
                    contextMenu.getItems().addAll(downloadItem, deleteItem);

                    optionsButton.setOnMouseClicked(event ->
                            contextMenu.show(optionsButton, event.getScreenX(), event.getScreenY())
                    );

                    hBox.getChildren().addAll(label, optionsButton);
                    setGraphic(hBox);
                }
            }
        };
    }
}

