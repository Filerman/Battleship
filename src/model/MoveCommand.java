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
        previousState = targetBoard.getGrid()[position.getRow()][position.getCol()];
        targetBoard.shoot(position); // Wykonaj strzał
    }

    @Override
    public void undo() {
        targetBoard.getGrid()[position.getRow()][position.getCol()] = previousState;
        targetBoard.getShotsFired().remove(position);
    }
}
