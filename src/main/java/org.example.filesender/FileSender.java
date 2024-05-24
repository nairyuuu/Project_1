package org.example.filesender;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class FileSender {
    private File fileToSend;

    public File getFileToSend() {
        return fileToSend;
    }

    public void setFileToSend(File fileToSend) {
        this.fileToSend = fileToSend;
    }

    public void sendFile() throws IOException {
        if (fileToSend == null) {
            throw new IllegalStateException("No file selected.");
        }

        try (FileInputStream fileInputStream = new FileInputStream(fileToSend);
             Socket socket = new Socket("localhost", 12345);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

            String fileName = fileToSend.getName();
            byte[] fileNameBytes = fileName.getBytes();

            byte[] fileContentBytes = new byte[(int) fileToSend.length()];
            fileInputStream.read(fileContentBytes);

            dataOutputStream.writeInt(fileNameBytes.length);
            dataOutputStream.write(fileNameBytes);

            dataOutputStream.writeInt(fileContentBytes.length);
            dataOutputStream.write(fileContentBytes);


        }
    }
}
