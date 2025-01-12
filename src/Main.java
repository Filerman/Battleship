import controller.GameController;
import model.*;
import view.ConsoleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        Scanner scanner = new Scanner(System.in);

        view.displayMessage("Wybierz tryb gry:");
        view.displayMessage("1. Gracz vs Gracz");
        view.displayMessage("2. Gracz vs Komputer");
        view.displayMessage("3. Komputer vs Komputer (symulacja)");
        int choice = readInt(scanner, view);

        Board boardForUser;
        Board boardForAI;

        if (choice == 1 || choice == 2) {
            view.displayMessage("Czy chcesz skonfigurować planszę interaktywnie za pomocą buildera? (tak/nie)");
            String interactiveConfig = scanner.nextLine().trim();

            if (interactiveConfig.equalsIgnoreCase("tak")) {
                boardForUser = configureBoardInteractively(scanner, view);
            } else {
                boardForUser = configureStandardBoard();
            }
            boardForAI = configureStandardBoard();
        } else {
            boardForUser = configureStandardBoard();
            boardForAI = configureStandardBoard();
        }

        Player player1 = null;
        Player player2 = null;

        switch (choice) {
            case 1 -> { // Gracz vs Gracz
                view.displayMessage("Podaj nazwę Gracza 1:");
                String player1Name = scanner.nextLine();
                view.displayMessage("Podaj nazwę Gracza 2:");
                String player2Name = scanner.nextLine();

                player1 = new Player(player1Name, false, DifficultyLevel.EASY, boardForUser.getSize(), '.', 'S', 'X', 'O');
                player1.setBoard(boardForUser);

                player2 = new Player(player2Name, false, DifficultyLevel.EASY, boardForAI.getSize(), '.', 'S', 'X', 'O');
                player2.setBoard(boardForAI);
            }
            case 2 -> { // Gracz vs Komputer
                view.displayMessage("Podaj nazwę Gracza 1:");
                String playerName = scanner.nextLine();
                DifficultyLevel aiDifficulty = chooseAiDifficulty(view, scanner);

                player1 = new Player(playerName, false, DifficultyLevel.EASY, boardForUser.getSize(), '.', 'S', 'X', 'O');
                player1.setBoard(boardForUser);

                AIStrategy strategy = selectAIStrategy(aiDifficulty);
                player2 = new AIPlayer("Komputer", aiDifficulty, boardForAI.getSize(), '.', 'S', 'X', 'O', strategy);
                player2.setBoard(boardForAI);
            }
            case 3 -> { // Komputer vs Komputer (symulacja)
                DifficultyLevel ai1Difficulty = chooseAiDifficulty(view, scanner, "Ustaw trudność dla AI1: 1. EASY, 2. MEDIUM, 3. HARD");
                DifficultyLevel ai2Difficulty = chooseAiDifficulty(view, scanner, "Ustaw trudność dla AI2: 1. EASY, 2. MEDIUM, 3. HARD");

                AIStrategy ai1Strategy = selectAIStrategy(ai1Difficulty);
                AIStrategy ai2Strategy = selectAIStrategy(ai2Difficulty);

                player1 = new AIPlayer("AI1", ai1Difficulty, boardForUser.getSize(), '.', 'S', 'X', 'O', ai1Strategy);
                player1.setBoard(boardForUser);

                player2 = new AIPlayer("AI2", ai2Difficulty, boardForAI.getSize(), '.', 'S', 'X', 'O', ai2Strategy);
                player2.setBoard(boardForAI);
            }
            default -> {
                view.displayMessage("Nieprawidłowy wybór! Kończę działanie.");
                System.exit(0);
            }
        }

        BattleshipGame game = new BattleshipGame(player1, player2);
        GameController controller = new GameController(game, view);
        controller.startGame();
    }

    private static Board configureBoardInteractively(Scanner scanner, ConsoleView view) {
        view.displayMessage("Podaj rozmiar planszy:");
        int size = readInt(scanner, view);

        view.displayMessage("Podaj znak wody (np. '.'): ");
        char waterChar = scanner.nextLine().trim().charAt(0);

        view.displayMessage("Podaj znak statku (np. 'S'): ");
        char shipChar = scanner.nextLine().trim().charAt(0);

        view.displayMessage("Podaj znak trafienia (np. 'X'): ");
        char hitChar = scanner.nextLine().trim().charAt(0);

        view.displayMessage("Podaj znak pudła (np. 'O'): ");
        char missChar = scanner.nextLine().trim().charAt(0);

        view.displayMessage("Czy chcesz dodać kamienie na planszy? (tak/nie)");
        String addStones = scanner.nextLine().trim();
        List<Stone> stones = null;

        if (addStones.equalsIgnoreCase("tak")) {
            stones = collectStones(scanner, view);
        }

        BoardBuilderInterface builder = BoardBuilder.builder();
        BoardDirector director = new BoardDirector(builder);
        return director.constructBoard(size, stones, waterChar, shipChar, hitChar, missChar);
    }

    private static Board configureStandardBoard() {
        BoardBuilderInterface builder = BoardBuilder.builder();
        BoardDirector director = new BoardDirector(builder);
        return director.constructStandardBoard();
    }

    private static List<Stone> collectStones(Scanner scanner, ConsoleView view) {
        List<Stone> stones = new ArrayList<>();
        view.displayMessage("Podaj pozycje kamieni w formacie x,y lub wpisz 'koniec', aby zakończyć:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("koniec")) {
                break;
            }
            String[] parts = input.split(",");
            if (parts.length == 2) {
                try {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    stones.add(new Stone(List.of(new Position(x, y))));
                } catch (NumberFormatException e) {
                    view.displayMessage("Nieprawidłowy format pozycji. Spróbuj ponownie.");
                }
            } else {
                view.displayMessage("Nieprawidłowy format. Użyj formatu x,y.");
            }
        }
        return stones;
    }

    private static DifficultyLevel chooseAiDifficulty(ConsoleView view, Scanner scanner) {
        return chooseAiDifficulty(view, scanner, "Wybierz poziom trudności AI: 1. EASY, 2. MEDIUM, 3. HARD");
    }

    private static DifficultyLevel chooseAiDifficulty(ConsoleView view, Scanner scanner, String message) {
        view.displayMessage(message);
        int choice = readInt(scanner, view);
        return switch (choice) {
            case 1 -> DifficultyLevel.EASY;
            case 2 -> DifficultyLevel.MEDIUM;
            case 3 -> DifficultyLevel.HARD;
            default -> {
                view.displayMessage("Nieprawidłowy wybór, domyślnie EASY.");
                yield DifficultyLevel.EASY;
            }
        };
    }

    private static AIStrategy selectAIStrategy(DifficultyLevel difficulty) {
        return switch (difficulty) {
            case EASY -> new RandomStrategy();
            case MEDIUM -> new HuntStrategy();
            case HARD -> new SystematicStrategy();
        };
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
}
