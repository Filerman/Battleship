package model;

public class SystematicStrategy implements AIStrategy {
    @Override
    public MoveCommand executeStrategy(Board board) {
        // Prosty algorytm systematycznego sprawdzania pól na planszy
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position position = new Position(row, col);
                if (!board.getShotsFired().contains(position)) {
                    return new MoveCommand(board, position);
                }
            }
        }
        return null; // Wszystkie pola już były sprawdzone
    }
}
