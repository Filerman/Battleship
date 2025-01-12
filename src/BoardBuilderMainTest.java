import model.*;

public class BoardBuilderMainTest {
    public static void main(String[] args) {
        // Przykład budowania planszy z builderem.
        Board board = BoardBuilder.builder()
                .setSize(10)
                .setWaterChar('.')
                .setShipChar('S')
                .setHitChar('X')
                .setMissChar('O')
                // Dodajemy kamień jednopolowy
                .addStone(StoneBuilder.builder()
                        .addPosition(5, 5)
                        .build())
                // Dodajemy kamień dwupolowy
                .addStone(StoneBuilder.builder()
                        .addPosition(7, 7)
                        .addPosition(7, 8)
                        .build())
                .build();

        printBoard(board);
    }

    // Prosta metoda do wypisania planszy w konsoli
    private static void printBoard(Board board) {
        char[][] grid = board.getGrid();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
}
