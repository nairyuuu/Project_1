import java.sql.*;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
public class DatabaseUtilBak {
    private static final String URL = "jdbc:mysql://localhost:3306/FileSenderDB";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public static boolean userExists(String username) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static String authenticate(String username, String password) {
        String userId = null;
        String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getString("user_id");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(e.getMessage());
        }
        return userId;
    }
    public static void createUser(String userId, String username, String password) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (user_id, username, password) VALUES (?, ?, ?)")) {
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.executeUpdate();
        }
    }
}