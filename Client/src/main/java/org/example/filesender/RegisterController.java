package org.example.filesender;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class RegisterController {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            dataOutput.writeUTF("REGISTER");
            dataOutput.writeUTF(username);
            dataOutput.writeUTF(password);

            String response = dataInput.readUTF();
            if ("SUCCESS".equals(response)) {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.close();
            } else {
                errorLabel.setText(response);
            }
        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
            errorLabel.setText("An error occurred. Please try again.");
        }
    }
}
