package org.example.server;

public class MyFile {
    private int id;
    private String name;
    private byte[] Data;
    private String fileExtension;

    public MyFile(int id, String name, byte[] data, String fileExtension) {
    }
    public void setID(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setData(byte[] Data) {
        this.Data = Data;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return Data;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
