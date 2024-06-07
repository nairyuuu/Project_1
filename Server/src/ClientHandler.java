import java.io.*;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;

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
                case "UPLOAD":
                    handleFileUpload(dataInput, dataOutput);
                    break;
                case "LIST":
                    handleFileListing(dataOutput);
                    break;
                case "DOWNLOAD":
                    handleFileDownload(dataInput, dataOutput);
                    break;
                case "DELETE":
                    handleFileDelete(dataInput, dataOutput);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Handle the files uploaded from client
    private void handleFileUpload(DataInputStream dataInput, DataOutputStream dataOutput) throws IOException {
        String fileName = dataInput.readUTF();
        long fileSize = dataInput.readLong();
        File file = new File(FileServer.UPLOAD_DIR + fileName);

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

        dataOutput.writeUTF("File uploaded successfully");
    }
    // Handle the listing of the file viewed from client
    private void handleFileListing(DataOutputStream dataOutput) throws IOException {
        File dir = new File(FileServer.UPLOAD_DIR);
        String[] files = dir.list();
        assert files != null;
        dataOutput.writeInt(files.length);
        for (String file : files) {
            dataOutput.writeUTF(file);
        }
    }
    // Handle file delete
    private void handleFileDelete(DataInputStream dataInput, DataOutputStream dataOutput) throws IOException {
        String fileName = dataInput.readUTF();
        File file = new File(FileServer.UPLOAD_DIR + fileName);

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
        String fileName = dataInput.readUTF();
        File file = new File(FileServer.UPLOAD_DIR + fileName);

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




