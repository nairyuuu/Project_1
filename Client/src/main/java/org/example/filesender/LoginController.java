package org.example.filesender;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class LoginController {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String userId = authenticate(username, password);
        if (userId != null) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("Client.fxml"));
                Parent root = loader.load();
                ClientController controller = loader.getController();
                controller.setUserId(userId);

                Scene scene = new Scene(root);
                scene.setUserData(controller);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("TCP File transfer system");
            } catch (Exception e) {
                Logger.getLogger(e.getMessage());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Error Occurred");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
        }
    }
    private String authenticate(String username, String password) {
        String userId = null;
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            dataOutput.writeUTF("AUTHENTICATE");
            dataOutput.writeUTF(username);
            dataOutput.writeUTF(password);

            String response = dataInput.readUTF();
            if ("AUTH_SUCCESS".equals(response)) {
                userId = dataInput.readUTF();
            }

        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
        }
        return userId;
    }
}
