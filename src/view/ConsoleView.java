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
     * Dla trybu Gracz vs Komputer i AI vs AI statki AI są ukrywane.
     * Parametr gameMode (np. "PVP", "PVE", "AI vs AI") może być wykorzystany do dodatkowej logiki.
     */
    public void printBoards(Player player1, Player player2, String gameMode) {
        // W trybie PVE (Gracz vs Komputer) plansza komputera ma statki ukryte
        boolean showShipsPlayer1 = !player1.isAI();
        boolean showShipsPlayer2 = !player2.isAI();
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
    public void printBoard(Board board, boolean showShips) {
        int size = board.getSize();
        // Wypisanie nagłówka kolumn z wyrównaniem
        System.out.print(String.format("%3s", ""));
        for (int c = 0; c < size; c++) {
            System.out.print(String.format("%3d", c));
        }
        System.out.println();

        // Wypisanie wierszy
        for (int r = 0; r < size; r++) {
            System.out.print(String.format("%3d", r));
            for (int c = 0; c < size; c++) {
                char cell = board.getGrid()[r][c];
                // Jeśli nie chcemy pokazywać statków, zamieniamy 'S' na znak wody
                if (!showShips && cell == board.getShipChar()) {
                    cell = board.getWaterChar();
                }
                String output;
                if (cell == board.getWaterChar()) {
                    output = AnsiColors.BLUE + cell + AnsiColors.RESET;
                } else if (cell == 'K') {
                    output = AnsiColors.GRAY + cell + AnsiColors.RESET;
                } else if (cell == board.getShipChar()) {
                    output = AnsiColors.GREEN + cell + AnsiColors.RESET;
                } else if (cell == board.getHitChar()) {
                    output = AnsiColors.RED + cell + AnsiColors.RESET;
                } else if (cell == board.getMissChar()) {
                    output = AnsiColors.YELLOW + cell + AnsiColors.RESET;
                } else {
                    output = String.valueOf(cell);
                }
                System.out.print(String.format("%3s", output));
            }
            System.out.println();
        }
    }
}
