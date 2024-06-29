package org.example;

public class Config {
    protected static final String UPLOAD_DIR = "uploads/";
    protected static final int PORT = 12345;
    protected static final String DB_URL = "jdbc:mysql://localhost:3306/FileSenderDB";
    protected static final String DB_USER = "root";
    protected static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
}
