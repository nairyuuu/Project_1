import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class FileServer {
    static boolean running = true;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Config.PORT)) {
            while (running) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            Logger.getLogger(e.getMessage());
        }
    }
}

