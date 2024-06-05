package org.example.server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerController {

    @FXML
    private VBox fileBox;

    private ArrayList<MyFile> myFiles = new ArrayList<>();

    @FXML
    private void initialize() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                int fileId = 0;
                while (true) {
                    try (Socket socket = serverSocket.accept()) {
                        System.out.println("Connected");
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                        int fileNameLength = dataInputStream.readInt();

                        if (fileNameLength > 0) {
                            byte[] fileNameBytes = new byte[fileNameLength];
                            dataInputStream.readFully(fileNameBytes);
                            String fileName = new String(fileNameBytes);
                            System.out.println(fileNameLength);


                            int fileContentLength = dataInputStream.readInt();
                            if (fileContentLength > 0) {
                                byte[] fileContentBytes = new byte[fileContentLength];
                                dataInputStream.readFully(fileContentBytes);
                                System.out.println(fileContentLength);

                                MyFile myFile = new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName));
                                myFiles.add(myFile);

                                Platform.runLater(() -> addFileToUI(myFile));

                                fileId++;
                                System.out.println(myFile.getName());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void addFileToUI(MyFile myFile) {
        Label fileLabel = new Label(myFile.getName());
        fileLabel.setStyle("-fx-font-size: 20px; -fx-padding: 10 0;");

        HBox fileRow = new HBox(fileLabel);
        fileRow.setSpacing(10);
        fileRow.setUserData(myFile);
        fileRow.setOnMouseClicked(event -> openFilePreview(myFile));

        fileBox.getChildren().add(fileRow);
    }

    private void openFilePreview(MyFile myFile) {
        // Implement file preview logic
    }

    private String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i + 1);
        } else {
            return "No extension found";
        }
    }
}
