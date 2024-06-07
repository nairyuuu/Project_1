import java.sql.SQLException;

public class LoginManager {

    private UserDAO userDAO;

    public LoginManager() {
        userDAO = new UserDAO();
    }

    public boolean login(String username, String password) {
        try {
            User user = userDAO.getUser(username);
            if (user != null && user.checkPassword(password)) {
                System.out.println("Login successful!");
                return true;
            } else {
                System.out.println("Invalid username or password.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void register(String username, String password) {
        try {
            User user = new User(username, password);
            userDAO.addUser(user);
            System.out.println("User registered successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
