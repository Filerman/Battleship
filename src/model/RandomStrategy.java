package model;

import java.util.Random;

public class RandomStrategy implements AIStrategy {
    @Override
    public MoveCommand executeStrategy(Board board) {
        Random random = new Random();
        int size = board.getSize();
        Position position;

        // Wybieranie losowego ruchu
        do {
            position = new Position(random.nextInt(size), random.nextInt(size));
        } while (board.getShotsFired().contains(position)); // Unikaj już zajętych miejsc

        return new MoveCommand(board, position);
    }
}
