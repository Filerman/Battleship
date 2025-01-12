package view;

import model.Board;
import model.AnsiColors;
import model.Player;

public class ConsoleView {

    public void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Wyświetla dwie plansze obok siebie.
     * Jeśli obaj gracze są AI, wyświetlamy statki obu graczy.
     */
    public void printBoards(Player player1, Player player2) {
        boolean showShipsPlayer1 = true;
        boolean showShipsPlayer2 = true;

        // Jeśli przynajmniej jeden gracz nie jest AI, stosujemy dotychczasową logikę
        if (!player1.isAI() || !player2.isAI()) {
            // Dla gracza ludzkiego lub trybu Gracz vs Gracz – uzywamy oryginalnej logiki:
            showShipsPlayer1 = !player1.isAI();
            showShipsPlayer2 = !player2.isAI();
        }

        System.out.println("------- Plansza " + player1.getName() + " -------");
        printBoard(player1.getBoard(), showShipsPlayer1);
        System.out.println("------- Plansza " + player2.getName() + " -------");
        printBoard(player2.getBoard(), showShipsPlayer2);
    }

    /**
     * Wyświetla jedną planszę w konsoli.
     * Jeśli showShips=false, to znak statku zamieniamy na wodę.
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
                } else if (cell == 'K') {
                    output = AnsiColors.GRAY + cell + AnsiColors.RESET;
                } else if (cell == shipChar) {
                    if (showShips) {
                        output = AnsiColors.GREEN + cell + AnsiColors.RESET;
                    } else {
                        // Jeśli statki nie mają być pokazane, zamiast nich pokazujemy wodę.
                        output = AnsiColors.BLUE + waterChar + AnsiColors.RESET;
                    }
                } else if (cell == hitChar) {
                    output = AnsiColors.RED + cell + AnsiColors.RESET;
                } else if (cell == missChar) {
                    output = AnsiColors.YELLOW + cell + AnsiColors.RESET;
                } else {
                    output = cell + "";
                }
                System.out.print(output + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
