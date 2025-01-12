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

        // Budujemy niestandardową planszę z kamieniami przy użyciu BoardBuildera.
        Board customBoard = BoardBuilder.builder()
                .setSize(boardSize)
                .setWaterChar(waterChar)
                .setShipChar(shipChar)
                .setHitChar(hitChar)
                .setMissChar(missChar)
                // Dodajemy kamień jednopolowy (np. na pozycji (5,5))
                .addStone(StoneBuilder.builder()
                        .addPosition(5, 5)
                        .setDestructible(true)
                        .build())
                // Dodajemy kamień dwupolowy (np. na pozycjach (7,7) i (7,8))
                .addStone(StoneBuilder.builder()
                        .addPosition(7, 7)
                        .addPosition(7, 8)
                        .build())
                .build();

        Player player1 = null;
        Player player2 = null;

        switch (choice) {
            case 1: // Gracz vs Gracz
                view.displayMessage("Podaj nazwę Gracza 1:");
                String player1Name = scanner.next();
                view.displayMessage("Podaj nazwę Gracza 2:");
                String player2Name = scanner.next();

                // Tworzymy obu graczy; przypisujemy im niestandardową planszę z kamieniami.
                player1 = new Player(player1Name, false, DifficultyLevel.EASY, boardSize, waterChar, shipChar, hitChar, missChar);
                player1.setBoard(customBoard);
                player2 = new Player(player2Name, false, DifficultyLevel.EASY, boardSize, waterChar, shipChar, hitChar, missChar);
                player2.setBoard(customBoard);
                break;

            case 2: // Gracz vs Komputer
                view.displayMessage("Podaj nazwę Gracza 1:");
                String playerName = scanner.next();
                DifficultyLevel aiDifficulty = chooseAiDifficulty(view, scanner, "Wybierz poziom trudności AI: 1. EASY, 2. MEDIUM, 3. HARD");

                // Gracz ludzki – przypisujemy niestandardową planszę
                player1 = new Player(playerName, false, DifficultyLevel.EASY, boardSize, waterChar, shipChar, hitChar, missChar);
                player1.setBoard(customBoard);

                // Komputer – tworzymy AIPlayer i ustawiamy mu także niestandardową planszę.
                AIStrategy strategy;
                if (aiDifficulty == DifficultyLevel.EASY) {
                    strategy = new RandomStrategy();
                } else if (aiDifficulty == DifficultyLevel.MEDIUM) {
                    strategy = new HuntStrategy();
                } else {
                    strategy = new SystematicStrategy();
                }
                player2 = new AIPlayer("Komputer", aiDifficulty, boardSize, waterChar, shipChar, hitChar, missChar, strategy);
                player2.setBoard(customBoard);
                break;

            case 3: // Komputer vs Komputer (symulacja)
                DifficultyLevel ai1Difficulty = chooseAiDifficulty(view, scanner, "Ustaw trudność dla AI1: 1. EASY, 2. MEDIUM, 3. HARD");
                DifficultyLevel ai2Difficulty = chooseAiDifficulty(view, scanner, "Ustaw trudność dla AI2: 1. EASY, 2. MEDIUM, 3. HARD");

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
                player1 = new AIPlayer("AI1", ai1Difficulty, boardSize, waterChar, shipChar, hitChar, missChar, ai1Strategy);
                player1.setBoard(customBoard);
                player2 = new AIPlayer("AI2", ai2Difficulty, boardSize, waterChar, shipChar, hitChar, missChar, ai2Strategy);
                player2.setBoard(customBoard);
                break;

            default:
                view.displayMessage("Nieprawidłowy wybór! Kończę działanie.");
                System.exit(0);
        }

        // Rozpoczynamy grę – rozmieszczanie statków odbywa się ręcznie (np. w metodzie placeShipsManually w GameController)
        BattleshipGame game = new BattleshipGame(player1, player2);
        GameController controller = new GameController(game, view);
        controller.startGame();
    }

    private static DifficultyLevel chooseAiDifficulty(ConsoleView view, Scanner scanner, String message) {
        view.displayMessage(message);
        int diffChoice = scanner.nextInt();
        switch (diffChoice) {
            case 1:
                return DifficultyLevel.EASY;
            case 2:
                return DifficultyLevel.MEDIUM;
            case 3:
                return DifficultyLevel.HARD;
            default:
                view.displayMessage("Nieprawidłowy wybór, domyślnie EASY.");
                return DifficultyLevel.EASY;
        }
    }
}
