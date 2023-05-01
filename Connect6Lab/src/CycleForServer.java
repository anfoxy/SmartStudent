import java.util.Scanner;

public class LifeCycle {
    private WindowGame gameWindow;
    private Scanner messageFromServer;

    public LifeCycle(WindowGame gameWindow, Scanner messageFromServer) {
        this.gameWindow = gameWindow;
        this.messageFromServer = messageFromServer;
    }

    public void start() {
        while (true) {
            if (messageFromServer.hasNext()) {
                String commandFromServer = messageFromServer.nextLine();
                switch (commandFromServer) {
                    case "Player1":
                        gameWindow.setPlayerFirst(true);
                        break;
                    case "Player2":
                        gameWindow.setPlayerFirst(false);
                        break;
                    case "start":
                        gameWindow.startGame();
                        break;
                    default:
                        String[] data = commandFromServer.split("/");
                        int x = Integer.parseInt(data[0]);
                        int y = Integer.parseInt(data[1]);
                        int playerNum = Integer.parseInt(data[2]);
                        gameWindow.makeMove(x, y, playerNum);
                        break;
                }
            }
        }
    }
}
