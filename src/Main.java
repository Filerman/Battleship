import controller.GameController;
import model.*;
import view.ConsoleView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        Scanner scanner = new Scanner(System.in);

        view.displayMessage("Wybierz tryb gry:");
        view.displayMessage("1. Gracz vs Gracz");
        view.displayMessage("2. Gracz vs Komputer");
        view.displayMessage("3. Komputer vs Komputer (symulacja)");
        int choice = scanner.nextInt();

        int boardSize = 10;
        char waterChar = '.';
        char shipChar = 'S';
        char hitChar = 'X';
        char missChar = 'O';

        Player player1 = null;
        Player player2 = null;

        switch (choice) {
            case 1: // Gracz vs Gracz
                view.displayMessage("Podaj nazwę Gracza 1:");
                String player1Name = scanner.next();
                view.displayMessage("Podaj nazwę Gracza 2:");
                String player2Name = scanner.next();

                player1 = new Player(player1Name, false, DifficultyLevel.EASY, boardSize, waterChar, shipChar, hitChar, missChar);
                player2 = new Player(player2Name, false, DifficultyLevel.EASY, boardSize, waterChar, shipChar, hitChar, missChar);
                break;

            case 2: // Gracz vs Komputer
                view.displayMessage("Podaj nazwę Gracza 1:");
                String playerName = scanner.next();
                DifficultyLevel aiDifficulty = chooseAiDifficulty(view, scanner, "Wybierz poziom trudności AI: 1. EASY, 2. MEDIUM, 3. HARD");
                player1 = new Player(playerName, false, DifficultyLevel.EASY, boardSize, waterChar, shipChar, hitChar, missChar);
                player2 = new Player("Komputer", true, aiDifficulty, boardSize, waterChar, shipChar, hitChar, missChar);
                break;

            case 3: // Komputer vs Komputer (symulacja)
                DifficultyLevel ai1Difficulty = chooseAiDifficulty(view, scanner, "Ustaw trudność dla AI1: 1. EASY, 2. MEDIUM, 3. HARD");
                DifficultyLevel ai2Difficulty = chooseAiDifficulty(view, scanner, "Ustaw trudność dla AI2: 1. EASY, 2. MEDIUM, 3. HARD");

                player1 = new Player("AI1", true, ai1Difficulty, boardSize, waterChar, shipChar, hitChar, missChar);
                player2 = new Player("AI2", true, ai2Difficulty, boardSize, waterChar, shipChar, hitChar, missChar);
                break;

            default:
                view.displayMessage("Nieprawidłowy wybór! Kończę działanie.");
                System.exit(0);
        }

        BattleshipGame game = new BattleshipGame(player1, player2);
        GameController controller = new GameController(game, view);
        controller.startGame();
    }

    private static DifficultyLevel chooseAiDifficulty(ConsoleView view, Scanner scanner, String message) {
        view.displayMessage(message);
        int diffChoice = scanner.nextInt();
        switch (diffChoice) {
            case 1: return DifficultyLevel.EASY;
            case 2: return DifficultyLevel.MEDIUM;
            case 3: return DifficultyLevel.HARD;
            default:
                view.displayMessage("Nieprawidłowy wybór, domyślnie EASY.");
                return DifficultyLevel.EASY;
        }
    }
}
