import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;

public class Field extends JPanel {
    private final GameLogic game;
    private final PrintWriter messageFromClient;
    private final int FIELD_WIDTH = 500 - 2 * 10;
    private final int cellSize = FIELD_WIDTH / 19;

    private void sendMessageToServer(String msg) {
        try {
            messageFromClient.println(msg);
            messageFromClient.flush();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Field(GameLogic game, PrintWriter messageFromClient) {
        setVisible(false);

        this.game = game;
        this.messageFromClient = messageFromClient;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int x, y;
                x = (e.getX() - 10 + cellSize / 2) / cellSize;
                y = (e.getY() - 10 + cellSize / 2) / cellSize;

                int cx = e.getX() - 10 - x * cellSize;
                int cy = e.getY() - 10 - y * cellSize;

                // Найти ближайшие линии
                int rx = Math.round((float)cx / (float)cellSize);
                int ry = Math.round((float)cy / (float)cellSize);

                // Определить, какая ячейка ближе к клику
                if (cx - rx * cellSize > cellSize / 2) {
                    x = x + 1;
                }
                if (cy - ry * cellSize > cellSize / 2) {
                    y = y + 1;
                }
                sendMessageToServer(x + "/" + y + "/" + game.getPlayerNum());
            }
        });
    }

    // Swing
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(255, 255, 255));
        g.fillRect(10, 10, FIELD_WIDTH, FIELD_WIDTH);

        // Рисование разделительных линий
        for (int i = 0; i <= 19; i++) {
            g.setColor(Color.BLACK);
            g.drawLine(10, 10 + i * cellSize,
                    10 + FIELD_WIDTH,
                    10 + i * cellSize);
            g.drawLine(10 + i * cellSize, 10,
                    10 + i * cellSize,
                    10 + FIELD_WIDTH);
        }

        // Отображение ходов
        for (int x = 0; x < 19; x++) {
            for (int y = 0; y < 19; y++) {
                if (!game.getCellMarker(x,y).equals(GameLogic.CurrentStatusCells.free)) {
                    int cellX = 10 + x * cellSize;
                    int cellY = 10 + y * cellSize;

                    if (game.getCellMarker(x,y).equals(GameLogic.CurrentStatusCells.player1)) {
                        g.setColor(Color.BLACK);
                        g.fillOval(cellX - cellSize / 2, cellY - cellSize / 2,
                                cellSize, cellSize);
                    }

                    if (game.getCellMarker(x,y).equals(GameLogic.CurrentStatusCells.player2)) {
                        g.setColor(new Color(189, 189, 189));
                        g.fillOval(cellX - cellSize / 2, cellY - cellSize / 2,
                                cellSize, cellSize);
                    }
                }
            }
        }

        // Вывеска победы
        if (game.getGameOver()) {
            g.setColor(new Color(14, 131, 66));
            int lineHeight = FIELD_WIDTH / 5;
            g.fillRect(10, 10 + FIELD_WIDTH / 2 - lineHeight / 2,
                    FIELD_WIDTH, lineHeight);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Times New Roman", Font.BOLD, 15));
            String gom = game.getGameOverMessage();
            g.drawString(gom, 10 + FIELD_WIDTH / 4 + 30,
                    10 + FIELD_WIDTH / 2 + gom.length() / 2 + 10);
        }
    }
}
