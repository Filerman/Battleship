package model;


public class MoveCommand implements Command {
    private Board targetBoard;
    private Position position;
    private char previousState;

    public MoveCommand(Board targetBoard, Position position) {
        this.targetBoard = targetBoard;
        this.position = position;
    }

    @Override
    public void execute() {
        // Zapamiętujemy co było w polu
        previousState = targetBoard.getGrid()[position.getRow()][position.getCol()];
        // Wykonujemy strzał
        targetBoard.shoot(position);
    }

    @Override
    public void undo() {
        // Przywracamy poprzedni znak
        targetBoard.getGrid()[position.getRow()][position.getCol()] = previousState;
        // Usuwamy ten strzał z listy
        targetBoard.getShotsFired().remove(position);
    }
}
