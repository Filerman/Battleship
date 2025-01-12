package model;
import java.util.ArrayList;
import java.util.List;

public class HuntStrategy implements AIStrategy {
    @Override
    public MoveCommand executeStrategy(Board board) {
        // Implementacja strategii "Hunt" (np. wybieranie pól wokół trafionego statku)
        for (Position hit : board.getShotsFired()) {
            if (board.getGrid()[hit.getRow()][hit.getCol()] == board.getHitChar()) {
                // Szukaj sąsiadów trafienia
                for (Position neighbor : getAdjacentPositions(hit, board.getSize())) {
                    if (!board.getShotsFired().contains(neighbor)) {
                        return new MoveCommand(board, neighbor);
                    }
                }
            }
        }
        // Jeśli nie ma aktywnych trafień, wybierz losowy ruch
        return new RandomStrategy().executeStrategy(board);
    }

    private List<Position> getAdjacentPositions(Position pos, int size) {
        List<Position> neighbors = new ArrayList<>();
        int row = pos.getRow();
        int col = pos.getCol();

        if (row > 0) neighbors.add(new Position(row - 1, col));
        if (row < size - 1) neighbors.add(new Position(row + 1, col));
        if (col > 0) neighbors.add(new Position(row, col - 1));
        if (col < size - 1) neighbors.add(new Position(row, col + 1));

        return neighbors;
    }
}
