package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Server myServer;
    private PrintWriter messageFromServer;
    private Scanner messageFromClient;

    public ClientHandler(Socket socket, Server myServer) {
        try {
            this.myServer = myServer;
            this.messageFromServer = new PrintWriter(socket.getOutputStream());
            this.messageFromClient = new Scanner(socket.getInputStream());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (messageFromClient.hasNext()) {
                    String clientMessage = messageFromClient.nextLine();
                    System.out.println(clientMessage);
                    myServer.sendMessageToAllClients(clientMessage);
                }
                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void sendMessageToClient(String msg) {
        try {
            messageFromServer.println(msg);
            messageFromServer.flush();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
