package org.example.filesender;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class FileSenderAppController {
    private FileSender fileSender = new FileSender();

    @FXML
    private Label jlFileName;
    @FXML
    private Button jbSendFile;
    @FXML
    private Button jbChooseFile;

    @FXML
    private void initialize() {
        jbChooseFile.setOnAction(e -> chooseFile());
        jbSendFile.setOnAction(e -> sendFile());
    }

    private void chooseFile() {
        Stage stage = (Stage) jlFileName.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file to send");
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            fileSender.setFileToSend(selectedFile);
            jlFileName.setText("The file you want to send is " + selectedFile.getName());
        }
    }

    private void sendFile() {
        try {
            fileSender.sendFile();
            jlFileName.setText("File sent successfully!");
        } catch (IOException e) {
            jlFileName.setText("Error sending file.");
            e.printStackTrace();
        }
    }
}
