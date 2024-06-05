package org.example.filesender;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientController {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private File fileToSend;

    public File getFileToSend() {
        return fileToSend;
    }



    @FXML
    private ListView<String> lvFiles;
    @FXML
    private Label jlFileName;
    @FXML
    private Button ButtonUploadFile;
    @FXML
    private Button ButtonChooseFile;
    public void setFileToSend(File fileToSend) {
        this.fileToSend = fileToSend;
    }

    @FXML
    private void initialize() {
        lvFiles.setCellFactory(new FileCellFactory());
        ButtonChooseFile.setOnAction(e -> handleChooseFile());
        ButtonUploadFile.setOnAction(e -> handleFileUpload());
//        loadFilesFromServer();
    }

    @FXML
    private void handleChooseFile() {
        Stage stage = (Stage) jlFileName.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file to send");
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            setFileToSend(selectedFile);
            jlFileName.setText("The file you want to send is " + selectedFile.getName());
        }
    }

    private void uploadFileToServer(File file) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             FileInputStream fileInput = new FileInputStream(file)) {

            dataOutput.writeUTF("UPLOAD");
            dataOutput.writeUTF(file.getName());
            dataOutput.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int read;
            while ((read = fileInput.read(buffer, 0, buffer.length)) > 0) {
                dataOutput.write(buffer, 0, read);
            }

            try (DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {
                String response = dataInput.readUTF();
                System.out.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void handleFileUpload() {
        uploadFileToServer(getFileToSend());
        jlFileName.setText("File sent successfully!");
    }

    private void handleFileListing() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            dataOutput.writeUTF("LIST");

            int fileCount = dataInput.readInt();
            lvFiles.getItems().clear();
            for (int i = 0; i < fileCount; i++) {
                lvFiles.getItems().add(dataInput.readUTF());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void handleDownloadFile(String fileName) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            dataOutput.writeUTF("DOWNLOAD");
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
                }

                System.out.println("File downloaded successfully");
            } else {
                System.out.println("File not found");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void handleDeleteFile(String fileName) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            dataOutput.writeUTF("DELETE");
            dataOutput.writeUTF(fileName);

            String response = dataInput.readUTF();
            System.out.println(response);

            handleFileListing();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


