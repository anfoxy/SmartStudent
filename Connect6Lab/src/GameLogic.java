public class GameLogic {
    private int playerNumber;
    private final Player[] players;
    private boolean gameOver = false;
    private String gameOverMessage = "";

    public enum CurrentStatusCells { player1, player2, free }
    private final CurrentStatusCells[][] cells;

    public GameLogic(){
        cells = new CurrentStatusCells[19][19];
        players = new Player[2];
        players[0] = new Player(this, CurrentStatusCells.player1);
        players[1] = new Player(this, CurrentStatusCells.player2);
        players[0].setMovesCounts(1);
    }

    public void setPlayerNum(int myPlayerNum) { this.playerNumber = myPlayerNum; }
    public void markCell(int x, int y, CurrentStatusCells playerTag){ cells[x][y] = playerTag; }
    public int getPlayerNum() { return playerNumber; }
    public CurrentStatusCells getCellMarker(int x, int y){ return cells[x][y]; }
    public boolean getGameOver(){ return gameOver; }
    public String getGameOverMessage(){ return gameOverMessage; }

    // Логика игры
    public void startNewGame() {
        for (int x = 0; x < 19; x++) {
            for (int y = 0; y < 19; y++) {
                cells[x][y] = CurrentStatusCells.free;
            }
        }
    }
    public void makeShot(int x, int y, int currentPlayerNum) {
        int anotherPlayerNum = currentPlayerNum == 0 ? 1 : 0;
        if (!gameOver && isValidCell(x, y) && isCellEmpty(x, y) && players[currentPlayerNum].getMovesCounts() > 0 && players[anotherPlayerNum].getMovesCounts() == 0) {
            players[currentPlayerNum].shot(x, y);
            players[currentPlayerNum].setMovesCounts(players[currentPlayerNum].getMovesCounts() - 1);
            if (players[currentPlayerNum].getMovesCounts() == 0) {
                players[anotherPlayerNum].setMovesCounts(2);
            }
        }
        if (players[currentPlayerNum].gameOver()) {
            gameOver = true;
            gameOverMessage = currentPlayerNum + 1 == 1 ? "Победил игрок с черными камнями." : "Победил игрок с белыми камнями.";
        }
    }

    public boolean checkWin(CurrentStatusCells statusCell) {
        for (int x = 0; x < 19; x++) {
            for (int y = 0; y <= 19 - 6; y++) {
                if (checkLine(x, y, 0, 1, statusCell))
                    return true;
            }
        }
        for (int x = 0; x <= 19 - 6; x++) {
            for (int y = 0; y < 19; y++) {
                if (checkLine(x, y, 1, 0, statusCell))
                    return true;
            }
        }
        for (int x = 0; x <= 19 - 6; x++) {
            for (int y = 0; y <= 19 - 6; y++) {
                if (checkLine(x, y, 1, 1, statusCell))
                    return true;
            }
        }
        for (int x = 6 - 1; x < 19; x++) {
            for (int y = 0; y <= 19 - 6; y++) {
                if (checkLine(x, y, -1, 1, statusCell))
                    return true;
            }
        }
        return false;
    }

    public int hasPlayerShots() {
        return players[playerNumber].getMovesCounts();
    }
    private boolean isCellEmpty(int x, int y) {
        return cells[x][y].equals(CurrentStatusCells.free);
    }

    private boolean isValidCell(int x, int y){
        return x >= 0 && y >= 0 && x < 19 && y < 19;
    }

    private boolean checkLine(int start_x, int start_y, int dx, int dy, CurrentStatusCells playerTag) {
        int count = 0;
        for (int offset = 0; offset < 6; offset++)
            if (cells[start_x + offset * dx][start_y + offset * dy].equals(playerTag)) {
                count++;
                if (count == 6)
                    return true;
            }
        return false;
    }
}
