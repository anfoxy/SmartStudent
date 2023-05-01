package org.example;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class EchoClient {
    public static void main(String[] args) {
        Scanner mgsServer;
        PrintWriter msgClient;

        int serverPort = 8080;
        String serverHost = "localhost";

        try (Socket s = new Socket(serverHost, serverPort)) {
            System.out.println("Подключение к серверу установлено!");

            mgsServer = new Scanner(s.getInputStream());
            msgClient = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);

            WindowGame gameWindow = new WindowGame(msgClient);

            while (true) {
                if (mgsServer.hasNext()) {
                    String commandFromServer = mgsServer.nextLine();
                    if (commandFromServer.equals("Player1")) {
                        gameWindow.FirstMessegePanel(1);
                    } else if (commandFromServer.equals("Player2")) {
                        gameWindow.FirstMessegePanel(2);
                    } else if (commandFromServer.equals("start")) {
                        gameWindow.gameBegin();
                    } else {
                        String[] data = commandFromServer.split(":");
                        int x = Integer.parseInt(data[0]);
                        int y = Integer.parseInt(data[1]);
                        int playerNum = Integer.parseInt(data[2]);
                        gameWindow.makeMove(x, y, playerNum);
                    }
                }
            }

        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage()); // Проблема с сокетом
        }
        catch (IOException e) {
            System.out.println("IOException: " + e.getMessage()); // Проблема с вводом/выводом
        }
    }
}
