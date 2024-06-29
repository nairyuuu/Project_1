package org.example;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Logger;

public class DatabaseUtil {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASSWORD);
    }

    public static boolean userExists(String username) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM users WHERE BINARY username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static String authenticate(String username, String password) {
        String userId = null;
        String hashedPassword = getHashedPassword(username);
        if (hashedPassword != null && BCrypt.checkpw(password, hashedPassword)) {
            userId = getUserId(username);
        }
        return userId;
    }

    public static void createUser(String username, String password) throws SQLException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String userId = UUID.randomUUID().toString(); // Example user ID generation
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (user_id, username, password) VALUES (?, ?, ?)")) {
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();
        }
    }

    private static String getHashedPassword(String username) {
        String hashedPassword = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT password FROM users WHERE BINARY username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    hashedPassword = resultSet.getString("password");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(e.getMessage());
        }
        return hashedPassword;
    }

    private static String getUserId(String username) {
        String userId = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM users WHERE BINARY username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getString("user_id");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(e.getMessage());
        }
        return userId;
    }
}
