package view;

import model.Board;
import model.Player;

/**
 * Widok konsolowy - wyświetla informacje w terminalu i pobiera dane od użytkownika.
 */
public class ConsoleView {

    public void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Wyświetla dwie plansze obok siebie (przykładowo).
     * Możesz wprowadzić np. "mgłę wojny" dla planszy przeciwnika (by nie widzieć statków).
     */
    public void printBoards(Player player1, Player player2) {
        System.out.println("------- Plansza " + player1.getName() + " -------");
        printBoard(player1.getBoard(), true);   // true = widzimy statki
        System.out.println("------- Plansza " + player2.getName() + " -------");
        // Możemy wprowadzić mgłę wojny, np. false = nie pokazuj statków
        printBoard(player2.getBoard(), player2.isAI() ? false : true);
    }

    /**
     * Wyświetla jedną planszę w konsoli.
     * Jeżeli showShips=false, to znak statku zamieniamy na wodę, aby nie pokazywać pozycji statków.
     */
    public void printBoard(Board board, boolean showShips) {
        char[][] grid = board.getGrid();
        char waterChar = board.getWaterChar();
        char shipChar = board.getShipChar();
        char hitChar = board.getHitChar();
        char missChar = board.getMissChar();

        System.out.print("   ");
        for (int c = 0; c < grid.length; c++) {
            System.out.print(c + " ");
        }
        System.out.println();

        for (int r = 0; r < grid.length; r++) {
            System.out.print(r + "  ");
            for (int c = 0; c < grid[r].length; c++) {
                char cell = grid[r][c];
                if (!showShips && cell == shipChar) {
                    // Zamiast statku pokaż wodę
                    System.out.print(waterChar + " ");
                } else {
                    System.out.print(cell + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
