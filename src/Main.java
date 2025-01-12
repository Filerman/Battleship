import controller.GameController;
import model.*;
import view.ConsoleView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        Scanner scanner = new Scanner(System.in);

        // Wybór trybu gry odbywa się na samym początku
        view.displayMessage("Wybierz tryb gry:");
        view.displayMessage("1. Gracz vs Gracz");
        view.displayMessage("2. Gracz vs Komputer");
        view.displayMessage("3. Komputer vs Komputer (symulacja)");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Czyszczenie bufora

        int boardSize;
        Board boardForUser;
        Board boardForAI;
        // Jeśli tryb gry to Gracz vs Gracz lub Gracz vs Komputer – umożliwiamy interaktywną konfigurację planszy
        if (choice == 1 || choice == 2) {
            view.displayMessage("Czy chcesz skonfigurować planszę interaktywnie za pomocą buildera? (tak/nie)");
            String interactiveConfig = scanner.nextLine().trim();
            if (interactiveConfig.equalsIgnoreCase("tak")) {
                // Użytkownik interaktywnie konfiguruje planszę (rozmiar + dodawanie kamieni)
                BoardBuilderInterface builder1 = BoardBuilder.builder();
                BoardDirector director1 = new BoardDirector(builder1, scanner);
                boardForUser = director1.constructInteractively();
            } else {
                // Domyślna konfiguracja: plansza bez kamieni, rozmiar domyślny = 10
                BoardBuilderInterface builder1 = BoardBuilder.builder()
                        .setSize(10)
                        .setWaterChar('.')
                        .setShipChar('S')
                        .setHitChar('X')
                        .setMissChar('O');
                BoardDirector director1 = new BoardDirector(builder1, scanner);
                boardForUser = director1.constructStandardBoardWithoutStones();
            }
            // Dla planszy AI w tych trybach używamy standardowej konfiguracji (bez interaktywnego dodawania kamieni)
            BoardBuilderInterface builder2 = BoardBuilder.builder()
                    .setSize(10)
                    .setWaterChar('.')
                    .setShipChar('S')
                    .setHitChar('X')
                    .setMissChar('O');
            BoardDirector director2 = new BoardDirector(builder2, scanner);
            boardForAI = director2.constructStandardBoardWithoutStones();
        } else {
            // Tryb AI vs AI – obie plansze budujemy przy użyciu standardowej konfiguracji
            BoardBuilderInterface builder1 = BoardBuilder.builder()
                    .setSize(10)
                    .setWaterChar('.')
                    .setShipChar('S')
                    .setHitChar('X')
                    .setMissChar('O');
            BoardDirector director1 = new BoardDirector(builder1, scanner);
            boardForUser = director1.constructStandardBoardWithoutStones();

            BoardBuilderInterface builder2 = BoardBuilder.builder()
                    .setSize(10)
                    .setWaterChar('.')
                    .setShipChar('S')
                    .setHitChar('X')
                    .setMissChar('O');
            BoardDirector director2 = new BoardDirector(builder2, scanner);
            boardForAI = director2.constructStandardBoardWithoutStones();
        }

        // Tworzenie graczy
        Player player1 = null;
        Player player2 = null;
        switch (choice) {
            case 1: // Gracz vs Gracz
                view.displayMessage("Podaj nazwę Gracza 1:");
                String player1Name = scanner.nextLine();
                view.displayMessage("Podaj nazwę Gracza 2:");
                String player2Name = scanner.nextLine();

                player1 = new Player(player1Name, false, DifficultyLevel.EASY, boardForUser.getSize(), '.', 'S', 'X', 'O');
                player1.setBoard(boardForUser);
                player2 = new Player(player2Name, false, DifficultyLevel.EASY, boardForAI.getSize(), '.', 'S', 'X', 'O');
                player2.setBoard(boardForAI);
                break;
            case 2: // Gracz vs Komputer
                view.displayMessage("Podaj nazwę Gracza 1:");
                String playerName = scanner.nextLine();
                DifficultyLevel aiDifficulty = chooseAiDifficulty(view, scanner, "Wybierz poziom trudności AI: 1. EASY, 2. MEDIUM, 3. HARD");

                player1 = new Player(playerName, false, DifficultyLevel.EASY, boardForUser.getSize(), '.', 'S', 'X', 'O');
                player1.setBoard(boardForUser);
                AIStrategy strategy;
                if (aiDifficulty == DifficultyLevel.EASY) {
                    strategy = new RandomStrategy();
                } else if (aiDifficulty == DifficultyLevel.MEDIUM) {
                    strategy = new HuntStrategy();
                } else {
                    strategy = new SystematicStrategy();
                }
                player2 = new AIPlayer("Komputer", aiDifficulty, boardForAI.getSize(), '.', 'S', 'X', 'O', strategy);
                player2.setBoard(boardForAI);
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
                player1 = new AIPlayer("AI1", ai1Difficulty, boardForUser.getSize(), '.', 'S', 'X', 'O', ai1Strategy);
                player1.setBoard(boardForUser);
                player2 = new AIPlayer("AI2", ai2Difficulty, boardForAI.getSize(), '.', 'S', 'X', 'O', ai2Strategy);
                player2.setBoard(boardForAI);
                break;
            default:
                view.displayMessage("Nieprawidłowy wybór! Kończę działanie.");
                System.exit(0);
        }

        BattleshipGame game = new BattleshipGame(player1, player2);
        GameController controller = new GameController(game, view);
        controller.startGame();
    }

    private static int readInt(Scanner scanner, ConsoleView view) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                view.displayMessage("Nieprawidłowa liczba. Spróbuj ponownie.");
            }
        }
    }

    private static DifficultyLevel chooseAiDifficulty(ConsoleView view, Scanner scanner, String message) {
        view.displayMessage(message);
        int diffChoice = scanner.nextInt();
        scanner.nextLine();
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
