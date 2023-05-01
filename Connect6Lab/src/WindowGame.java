import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;

public class WindowGame extends JFrame {
    GameLogic game = new GameLogic();
    Field gameField;
    JLabel playerNumber;
    JLabel whoMove;
    JLabel waitingPlayer;

    public WindowGame(PrintWriter messageFromClient) {
        setTitle("Lab Connect6");
        setBounds(300, 300, 502, 540);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        gameField = new Field(game, messageFromClient);
        add(gameField, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout());
        add(panel, BorderLayout.SOUTH);

        playerNumber = new JLabel("");
        whoMove = new JLabel("");
        panel.add(playerNumber);
        panel.add(whoMove);

        waitingPlayer = new JLabel("Ожидание второго игрока");
        panel.add(waitingPlayer);

        setVisible(true);
    }

    public void setPlayerFirst(boolean first) {
        if (first) {
            game.setPlayerNum(0);
            playerNumber.setText("Цвет камня: Черный");
            whoMove.setText("Вы делаете первый ход!");
        } else {
            game.setPlayerNum(1);
            playerNumber.setText("Цвет камня: Белый");
            whoMove.setText("Ожидайте другого игрока");
        }
    }

    public void startGame() {
        game.startNewGame();
        gameField.setVisible(true);
        waitingPlayer.setVisible(false);
    }

    public void makeMove(int x, int y, int playerNum) {
        game.makeShot(x, y, playerNum);
        gameField.repaint();

        if (game.getPlayerNum() == 0 && game.hasPlayerShots() != 0)
            whoMove.setText("Ваш ход!");
        if (game.getPlayerNum() == 1 && game.hasPlayerShots() == 0)
            whoMove.setText("Ход другого игрока..");

        if (game.getPlayerNum() == 1 && game.hasPlayerShots() != 0)
            whoMove.setText("Ваш ход!");
        if (game.getPlayerNum() == 0 && game.hasPlayerShots() == 0)
            whoMove.setText("Ход другого игрока..");
    }
}
