import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
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
            }

        } catch (IOException e) {
           Logger.getLogger("Error" + e.getMessage());
        } catch (SQLException e) {
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
            File userFolder = new File(FileServer.UPLOAD_DIR, userId);
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
        File file = new File(FileServer.UPLOAD_DIR + "/" + userId, fileName);

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
        File file = new File(FileServer.UPLOAD_DIR + "/" + userId, fileName);

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
}




