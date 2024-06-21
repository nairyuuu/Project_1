import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;


class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
             DataInputStream dataInput = new DataInputStream(input);
             OutputStream output = socket.getOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(output)) {

            String command = dataInput.readUTF();
            switch (command) {
                case "REGISTER":
                    handleRegister(dataInput, dataOutput);
                    break;
                case "AUTHENTICATE":
                    handleAuthentication(dataInput, dataOutput);
                    break;
                case "UPLOAD":
                    handleFileUpload(dataInput, dataOutput);
                    break;
                case "LIST":
                    handleFileListing(dataInput, dataOutput);
                    break;
                case "DOWNLOAD":
                    handleFileDownload(dataInput, dataOutput);
                    break;
                case "DELETE":
                    handleFileDelete(dataInput, dataOutput);
                    break;
                case "ENCRYPT":
                    handleFileEncrypt(dataInput, dataOutput);
                    break;
                case "DECRYPT":
                    handleFileDecrypt(dataInput, dataOutput);
                    break;
            }

        } catch (IOException e) {
           Logger.getLogger("Error" + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void handleAuthentication(DataInputStream dataInput, DataOutputStream dataOutput) throws IOException {
        String username = dataInput.readUTF();
        String password = dataInput.readUTF();
        String userId = DatabaseUtil.authenticate(username, password);
        dataOutput.writeUTF(userId != null ? "AUTH_SUCCESS" : "AUTH_FAILURE");
        if (userId != null) {
            dataOutput.writeUTF(userId);
        }
    }
    private void handleRegister(DataInputStream dataInput, DataOutputStream dataOutput) throws IOException, SQLException {
        String username = dataInput.readUTF();
        String password = dataInput.readUTF();

        if (DatabaseUtil.userExists(username)) {
            dataOutput.writeUTF("User already exists.");
        } else {
            DatabaseUtil.createUser(username, password);
            dataOutput.writeUTF("SUCCESS");
        }
    }
    // Handle the files uploaded from client
    private void handleFileUpload(DataInputStream dataInput, DataOutputStream dataOutput) throws IOException {
        try {
            String userId = dataInput.readUTF();
            if (userId.isEmpty()) {
                dataOutput.writeUTF("INVALID USER ID");
                return;
            }

            String fileName = dataInput.readUTF();
            long fileSize = dataInput.readLong();
            File userDir = new File("uploads/" + userId);
            if (!userDir.exists()) {
                boolean mkdir = userDir.mkdirs();
                if (!mkdir) {
                    Logger.getLogger("Error creating folder");
                    dataOutput.writeUTF("UPLOAD FAILED");
                    return;
                }
            }
            File file = new File(userDir, fileName);

            try (FileOutputStream fileOutput = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int read;
                long totalRead = 0;
                while (totalRead < fileSize && (read = dataInput.read(buffer, 0, buffer.length)) > 0) {
                    fileOutput.write(buffer, 0, read);
                    totalRead += read;
                }
            }

            dataOutput.writeUTF("UPLOAD SUCCESSFUL");

        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
            dataOutput.writeUTF("UPLOAD FAILED");
        }
    }
    // Handle the listing of the file viewed from client
    private void handleFileListing(DataInputStream dataInput, DataOutputStream dataOutput) {
        try {
            String userId = dataInput.readUTF();
            if (userId.isEmpty()) {
                return;
            }
            File userFolder = new File(Config.UPLOAD_DIR, userId);
            if (!userFolder.exists()) {
                boolean mkdir = userFolder.mkdirs();
                if (!mkdir) {
                    Logger.getLogger("Error creating folder");
                }
            }
            File[] files = userFolder.listFiles();
            if (files != null) {
                dataOutput.writeInt(files.length);
                for (File file : files) {
                    dataOutput.writeUTF(file.getName());
                }
            } else {
                dataOutput.writeInt(0);
            }
        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
        }
    }

    // Handle file delete
    private void handleFileDelete(DataInputStream dataInput, DataOutputStream dataOutput) throws IOException {
        String userId = dataInput.readUTF();
        String fileName = dataInput.readUTF();
        File file = new File(Config.UPLOAD_DIR + "/" + userId, fileName);

        if (file.exists()) {
            if (file.delete()) {
                dataOutput.writeUTF("File deleted successfully");
            } else {
                dataOutput.writeUTF("Failed to delete file");
            }
        } else {
            dataOutput.writeUTF("File not found");
        }
    }
    private void handleFileDownload(DataInputStream dataInput, DataOutputStream dataOutput) throws IOException {
        String userId = dataInput.readUTF();
        String fileName = dataInput.readUTF();
        File file = new File(Config.UPLOAD_DIR + "/" + userId, fileName);

        if (file.exists()) {
            dataOutput.writeUTF("FOUND");
            dataOutput.writeLong(file.length());

            try (FileInputStream fileInput = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = fileInput.read(buffer, 0, buffer.length)) > 0) {
                    dataOutput.write(buffer, 0, read);
                }
            }
        } else {
            dataOutput.writeUTF("NOT FOUND");
        }
    }
    private SecretKey getKey(String privateKey) throws Exception {
        byte[] key = (privateKey).getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, "AES");
    }
    private void handleFileEncrypt(DataInputStream dataInput, DataOutputStream dataOutput) throws Exception {
        String userId = dataInput.readUTF();
        String fileName = dataInput.readUTF();
        String privateKey = dataInput.readUTF();

        File file = new File("uploads/" + userId + "/" + fileName);
        if (!file.exists()) {
            dataOutput.writeUTF("File not found");
            return;
        }

        // Generate a key from the password
        SecretKey secretKey = getKey(privateKey);

        // Encrypt the file
        File encryptedFile = new File("uploads/" + userId + "/" + fileName + ".enc");
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(encryptedFile)) {

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] inputBuffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(inputBuffer)) != -1) {
                byte[] outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
                if (outputBuffer != null) {
                    fos.write(outputBuffer);
                }
            }

            byte[] outputBuffer = cipher.doFinal();
            if (outputBuffer != null) {
                fos.write(outputBuffer);
            }

        } catch (Exception e) {
            Logger.getLogger("Error encrypting file: " + e.getMessage());
        }
        if (file.delete()) {
            dataOutput.writeUTF("File encrypted successfully");
        } else {
            dataOutput.writeUTF("File encrypted successfully, but failed to delete original file");
        }
    }

    private void handleFileDecrypt(DataInputStream dataInput, DataOutputStream dataOutput) throws Exception {
        String userId = dataInput.readUTF();
        String encryptedFileName = dataInput.readUTF();
        String privateKey = dataInput.readUTF();

        File encryptedFile = new File("uploads/" + userId + "/" + encryptedFileName);
        if (!encryptedFile.exists()) {
            dataOutput.writeUTF("File not found");
            return;
        }

        // Generate a key from the password
        SecretKey secretKey = getKey(privateKey);

        // Decrypt the file
        File decryptedFile = new File("uploads/" + userId + "/" + encryptedFileName.substring(0, encryptedFileName.length() - 4));
        boolean decryptionSuccessful = false;
        boolean deleteDecryptedFile = false;
        try (FileInputStream fis = new FileInputStream(encryptedFile);
             FileOutputStream fos = new FileOutputStream(decryptedFile)) {

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] inputBuffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(inputBuffer)) != -1) {
                byte[] outputBuffer;
                outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
                if (outputBuffer != null) {
                    fos.write(outputBuffer);
                }
            }
            byte[] outputBuffer;
            try {
                outputBuffer = cipher.doFinal();
            } catch (BadPaddingException e) {
                // Handle BadPaddingException
                dataOutput.writeUTF("Error: Incorrect decryption password");
                deleteDecryptedFile = true; // Set flag to delete decrypted file
                return;
            }
            if (outputBuffer != null) {
                fos.write(outputBuffer);
            }

            decryptionSuccessful = true;
        } catch (Exception e) {
            Logger.getLogger("Error decrypting file: " + e.getMessage());
            dataOutput.writeUTF("Error decrypting file: " + e.getMessage());
        } finally {
            // Always try to delete the decrypted file if flag is set
            if (deleteDecryptedFile && decryptedFile.exists()) {
                if (decryptedFile.delete()) {
                    Logger.getLogger("Decrypted file deleted successfully");
                } else {
                    Logger.getLogger("Failed to delete decrypted file");
                }
            }

            // If decryption was successful, delete encrypted file
            if (decryptionSuccessful && encryptedFile.exists()) {
                if (!encryptedFile.delete()) {
                    Logger.getLogger("Failed to delete encrypted file");
                }
            }
        }

        // Respond based on decryption success
        if (decryptionSuccessful) {
            dataOutput.writeUTF("File decrypted successfully");
        }
    }
}




