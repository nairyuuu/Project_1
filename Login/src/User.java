import org.mindrot.jbcrypt.BCrypt;

public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = hashPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public boolean checkPassword(String plainTextPassword) {
        return BCrypt.checkpw(plainTextPassword, this.password);
    }
}
