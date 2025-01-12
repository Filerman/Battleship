package view;

import model.AIPlayer;
import model.Board;
import model.AnsiColors;
import model.Player;

public class ConsoleView {

    public void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Wyświetla dwie plansze obok siebie.
     * Dla trybu Gracz vs Komputer i AI vs AI statki AI są ukrywane.
     * Parametr gameMode (np. "PVP", "PVE", "AI vs AI") może być wykorzystany do dodatkowej logiki.
     */
    public void printBoards(Player player1, Player player2, String gameMode) {
        // Determine if each player is an AI using instanceof
        boolean showShipsPlayer1 = !(player1 instanceof AIPlayer);
        boolean showShipsPlayer2 = !(player2 instanceof AIPlayer);
        if (gameMode.equals("AI vs AI")) {
            // W trybie AI vs AI wyświetlamy statki obu graczy
            showShipsPlayer1 = true;
            showShipsPlayer2 = true;
        }
        displayMessage("------- Plansza " + player1.getName() + " -------");
        printBoard(player1.getBoard(), showShipsPlayer1);
        displayMessage("------- Plansza " + player2.getName() + " -------");
        printBoard(player2.getBoard(), showShipsPlayer2);
    }

    /**
     * Wyświetla pojedynczą planszę z użyciem kolorów.
     */
    // można dodać kolorowanie planszy
    public void printBoard(Board board, boolean showShips) {
        int size = board.getSize();
        char[][] grid = board.getGrid();

        // Wypisanie nagłówka kolumn z wyrównaniem
        System.out.print("    "); // Puste miejsce na nagłówek wierszy
        for (int c = 0; c < size; c++) {
            System.out.print(String.format("%4d", c)); // Wyświetlenie numerów kolumn z odstępem 4
        }
        System.out.println();

        // Wypisanie wierszy planszy
        for (int r = 0; r < size; r++) {
            System.out.print(String.format("%3d ", r)); // Numer wiersza wyrównany do prawej i dodatkowa spacja
            for (int c = 0; c < size; c++) {
                char cell = grid[r][c];

                // Ukrywanie statków, jeśli showShips == false
                if (!showShips && cell == board.getShipChar()) {
                    cell = board.getWaterChar();
                }

                // Wyświetlenie każdej komórki z odstępem 4
                System.out.print(String.format("%4s", cell));
            }
            System.out.println(); // Nowa linia po każdym wierszu
        }
    }
}
