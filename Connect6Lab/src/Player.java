public class Player {

    GameLogic.CurrentStatusCells currentStatus;
    GameLogic field;
    int movesCounts = 0;

    public Player(GameLogic field, GameLogic.CurrentStatusCells currentStatus) {
        this.currentStatus = currentStatus;
        this.field = field;
    }

    public int getMovesCounts() {
        return movesCounts;
    }
    public void setMovesCounts(int movesCounts) {
        this.movesCounts = movesCounts;
    }
    void shot(int x, int y) {
        field.markCell(x, y, currentStatus);
    }
    boolean gameOver() {
        return field.checkWin(currentStatus);
    }
}
