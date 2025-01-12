package view;

import model.Board;
import model.AnsiColors;
import model.Player;

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
     * Jeśli showShips=false, znak statku zamieniamy na wodę, aby nie pokazywać pozycji statków.
     */
    public void printBoard(Board board, boolean showShips) {
        char[][] grid = board.getGrid();
        char waterChar = board.getWaterChar();
        char shipChar = board.getShipChar();
        char hitChar = board.getHitChar();
        char missChar = board.getMissChar();

        // Wypisanie nagłówka kolumn
        System.out.print("   ");
        for (int c = 0; c < grid.length; c++) {
            System.out.print(c + " ");
        }
        System.out.println();

        for (int r = 0; r < grid.length; r++) {
            System.out.print(r + "  ");
            for (int c = 0; c < grid[r].length; c++) {
                char cell = grid[r][c];
                String output;

                if (cell == waterChar) {
                    output = AnsiColors.BLUE + cell + AnsiColors.RESET;
                } else if (cell == 'K') { // kamień – ustawiany w metodzie placeStone
                    output = AnsiColors.GRAY + cell + AnsiColors.RESET;
                } else if (cell == shipChar) {
                    // Wyświetlamy statek – jeśli statki są do pokazania
                    if (showShips) {
                        output = AnsiColors.GREEN + cell + AnsiColors.RESET;
                    } else {
                        // Jeśli nie pokazujemy statków, zamiast tego pokazujemy wodę
                        output = AnsiColors.BLUE + waterChar + AnsiColors.RESET;
                    }
                } else if (cell == hitChar) {
                    output = AnsiColors.RED + cell + AnsiColors.RESET;
                } else if (cell == missChar) {
                    output = AnsiColors.YELLOW + cell + AnsiColors.RESET;
                } else {
                    // Domyślny kolor, jeśli nie obsłużono innego przypadku
                    output = cell + "";
                }

                System.out.print(output + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
