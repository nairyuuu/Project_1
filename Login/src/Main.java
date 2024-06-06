import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LoginManager loginManager = new LoginManager();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome! Please choose an option:");
        System.out.println("1. Register");
        System.out.println("2. Login");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            loginManager.register(username, password);
        } else if (choice == 2) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            loginManager.login(username, password);
        } else {
            System.out.println("Invalid choice.");
        }

        scanner.close();
    }
}
