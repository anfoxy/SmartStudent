import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class Server {
 public static ArrayList<ClientConnection> clients = new ArrayList<>();

    public static void main(String[] args) {

        try {
            int serverPort = 8080;
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Сервер запущен");

            while (true) {
                Socket s = serverSocket.accept();
                ClientConnection c = new ClientConnection(s);
                clients.add(c);

                if (clients.size() == 2) {
                    new Thread(clients.get(0)).start();
                    clients.get(0).sendMessageToClient("Player1");
                    new Thread(clients.get(1)).start();
                    clients.get(1).sendMessageToClient("Player2");
                    sendMessageToAllClients("start");
                }
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

    }
    public static void sendMessageToAllClients(String msg) {
        for (ClientConnection c: clients)
            c.sendMessageToClient(msg);
    }
}
