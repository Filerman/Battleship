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

                // Stwórz gracza dla gracza 1
                player1 = new Player(playerName, false, DifficultyLevel.EASY, boardSize, waterChar, shipChar, hitChar, missChar);

                // Ustaw strategię dla gracza AI
                AIStrategy strategy;
                if (aiDifficulty == DifficultyLevel.EASY) {
                    strategy = new RandomStrategy(); // Losowa strategia
                } else if (aiDifficulty == DifficultyLevel.MEDIUM) {
                    strategy = new HuntStrategy(); // Strategia "Hunt"
                } else {
                    strategy = new SystematicStrategy(); // Systematyczna strategia
                }

                // Stwórz AIPlayer z odpowiednią strategią
                player2 = new AIPlayer("Komputer", aiDifficulty, boardSize, waterChar, shipChar, hitChar, missChar, strategy);
                break;

            case 3: // Komputer vs Komputer (symulacja)
                DifficultyLevel ai1Difficulty = chooseAiDifficulty(view, scanner, "Ustaw trudność dla AI1: 1. EASY, 2. MEDIUM, 3. HARD");
                DifficultyLevel ai2Difficulty = chooseAiDifficulty(view, scanner, "Ustaw trudność dla AI2: 1. EASY, 2. MEDIUM, 3. HARD");

                // Ustaw strategie dla obu graczy AI
                AIStrategy ai1Strategy;
                AIStrategy ai2Strategy;

                if (ai1Difficulty == DifficultyLevel.EASY) {
                    ai1Strategy = new RandomStrategy();
                } else if (ai1Difficulty == DifficultyLevel.MEDIUM) {
                    ai1Strategy = new HuntStrategy();
                } else {
                    ai1Strategy = new SystematicStrategy();
                }

                if (ai2Difficulty == DifficultyLevel.EASY) {
                    ai2Strategy = new RandomStrategy();
                } else if (ai2Difficulty == DifficultyLevel.MEDIUM) {
                    ai2Strategy = new HuntStrategy();
                } else {
                    ai2Strategy = new SystematicStrategy();
                }

                // Tworzenie AIPlayer dla obu graczy
                player1 = new AIPlayer("AI1", ai1Difficulty, boardSize, waterChar, shipChar, hitChar, missChar, ai1Strategy);
                player2 = new AIPlayer("AI2", ai2Difficulty, boardSize, waterChar, shipChar, hitChar, missChar, ai2Strategy);
                break;

            default:
                view.displayMessage("Nieprawidłowy wybór! Kończę działanie.");
                System.exit(0);
        }

        // Rozpoczęcie gry
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
