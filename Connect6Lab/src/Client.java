import java.net.*;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;

public class Client {
    public static void main(String[] args) {
        Scanner mgsServer;
        PrintWriter msgClient;

        int serverPort = 8080;
        String serverHost = "localhost";

        try (Socket s = new Socket(serverHost, serverPort)) {
            System.out.println("Есть подключение!");
            mgsServer = new Scanner(s.getInputStream());
            msgClient = new PrintWriter(s.getOutputStream());

            new CycleForServer(new WindowGame(msgClient), mgsServer).start();

        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        }
          catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
