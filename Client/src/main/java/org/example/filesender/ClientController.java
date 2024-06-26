package org.example.filesender;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientController {
    private File fileToSend;
    private String userId;
    private final Object lock = new Object();

    public File getFileToSend() {
        return fileToSend;
    }
    public void setFileToSend(File fileToSend) {
        this.fileToSend = fileToSend;
    }
    public void setUserId(String userId) {
        this.userId = userId;
        synchronized (lock) {
            this.userId = userId;
            lock.notifyAll(); // Notify the waiting thread that userId is set
        }
    }
    @FXML
    private ListView<String> lvFiles;
    @FXML
    private Label LabelFileName;
    @FXML
    private Button ButtonUploadFile;
    @FXML
    private Button ButtonChooseFile;

    @FXML
    private void initialize() {

        lvFiles.setCellFactory(new FileListing());
        ButtonChooseFile.setOnAction(e -> handleChooseFile());
        ButtonUploadFile.setOnAction(e -> handleFileUpload());
        new Thread(() -> {
            synchronized (lock) {
                while (userId == null) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Logger.getLogger(e.getMessage());
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                Platform.runLater(this::handleFileListing);// Load files from server after userId is set
            }
        }).start();


    }
    @FXML
    private void handleChooseFile() {
        Stage stage = (Stage) LabelFileName.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file to upload");
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            setFileToSend(selectedFile);
            LabelFileName.setText("The file you want to upload is " + selectedFile.getName());
        }
    }
    private void uploadFileToServer(File file) {
        try (Socket socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             FileInputStream fileInput = new FileInputStream(file)) {

            dataOutput.writeUTF("UPLOAD");
            dataOutput.writeUTF(userId);
            dataOutput.writeUTF(file.getName());
            dataOutput.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int read;
            while ((read = fileInput.read(buffer, 0, buffer.length)) > 0) {
                dataOutput.write(buffer, 0, read);
            }

            try (DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {
                String response = dataInput.readUTF();
                if ("UPLOAD SUCCESSFUL".equals(response)) {
                    LabelFileName.setText("File uploaded successfully!");
                    handleFileListing();
                } else {
                    LabelFileName.setText("File upload failed");
                    Logger.getLogger("File upload failed: " + response);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
        }
    }
    private void handleFileUpload() {
        if (getFileToSend() == null) {
            LabelFileName.setText("No file selected");
        } else {
            uploadFileToServer(getFileToSend());
        }
    }
    private void handleFileListing() {
        try (Socket socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            dataOutput.writeUTF("LIST");
            dataOutput.writeUTF(userId);

            int fileCount = dataInput.readInt();
            lvFiles.getItems().clear();
            for (int i = 0; i < fileCount; i++) {
                lvFiles.getItems().add(dataInput.readUTF());
            }

        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
        }
    }
    void handleDownloadFile(String fileName) {
        try (Socket socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            dataOutput.writeUTF("DOWNLOAD");
            dataOutput.writeUTF(userId);
            dataOutput.writeUTF(fileName);

            String response = dataInput.readUTF();
            if ("FOUND".equals(response)) {
                long fileSize = dataInput.readLong();
                File file = new File("downloads/" + fileName);

                try (FileOutputStream fileOutput = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int read;
                    long totalRead = 0;
                    while ((read = dataInput.read(buffer, 0, buffer.length)) > 0) {
                        totalRead += read;
                        fileOutput.write(buffer, 0, read);
                        if (totalRead >= fileSize) break;
                    }
                    LabelFileName.setText("File downloaded successfully");
                }
            } else {
                LabelFileName.setText("Failed to download file");
            }
        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
        }
    }
    void handleDeleteFile(String fileName) {
        try (Socket socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            dataOutput.writeUTF("DELETE");
            dataOutput.writeUTF(userId);
            dataOutput.writeUTF(fileName);

            String response = dataInput.readUTF();
            LabelFileName.setText(response);

            handleFileListing();
        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
        }
    }

    public void handleEncryptFile(String fileName) {
        try (Socket socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            String userPrivateKey = getUserPrivateKey();
            if (userPrivateKey == null) return;

            dataOutput.writeUTF("ENCRYPT");
            dataOutput.writeUTF(userId);
            dataOutput.writeUTF(fileName);
            dataOutput.writeUTF(userPrivateKey);

            String response = dataInput.readUTF();
            LabelFileName.setText(response);
            handleFileListing();

        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
        }
    }

    public void handleDecryptFile(String fileName) {
        try (Socket socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            String password = getUserPrivateKey();
            if (password == null) return;

            dataOutput.writeUTF("DECRYPT");
            dataOutput.writeUTF(userId);
            dataOutput.writeUTF(fileName);
            dataOutput.writeUTF(password);

            String response = dataInput.readUTF();
            LabelFileName.setText(response);
            handleFileListing();

        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
        }
    }
    private String getUserPrivateKey() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Private key Input");
        dialog.setHeaderText("Enter your encryption/decryption private key \nMake sure to remember them or else you can't decrypt your file");
        dialog.setContentText("Key:");

        return dialog.showAndWait().orElse(null);
    }
}


