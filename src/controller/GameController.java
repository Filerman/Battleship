package controller;

import model.*;
import view.ConsoleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Główny kontroler gry.
 */
public class GameController {
    private BattleshipGame game;
    private ConsoleView view;
    private Scanner scanner;

    // Licznik rund
    private int roundCount;

    public GameController() {
        // Jeżeli chcesz, aby GameController sam tworzył obiekty View itp.
        this.view = new ConsoleView();
        this.scanner = new Scanner(System.in);
        this.roundCount = 0;
    }

    /**
     * Metoda, w której przenosimy konfigurację gry i tworzenie obiektów z klasy Main.
     * Na koniec wywołujemy startGame().
     */
    public void setupGame() {
        view.displayMessage("Wybierz tryb gry:");
        view.displayMessage("1. Gracz vs Gracz");
        view.displayMessage("2. Gracz vs Komputer");
        view.displayMessage("3. Komputer vs Komputer (symulacja)");

        int choice = readInt();

        // Dwie plansze (dla Gracza1 i Gracza2 lub AI)
        Board boardForUser;
        Board boardForAI;

        if (choice == 1 || choice == 2) {
            view.displayMessage("Czy chcesz skonfigurować planszę interaktywnie za pomocą buildera? (tak/nie)");
            String interactiveConfig = scanner.nextLine().trim();

            if (interactiveConfig.equalsIgnoreCase("tak")) {
                boardForUser = configureBoardInteractively();
            } else {
                boardForUser = configureStandardBoard();
            }
            boardForAI = configureStandardBoard();
        } else {
            // Tryb Komputer vs Komputer (symulacja) – obie plansze standardowe
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

                player1 = new Player(player1Name, false, DifficultyLevel.EASY,
                        boardForUser.getSize(), '.', 'S', 'X', 'O');
                player1.setBoard(boardForUser);

                player2 = new Player(player2Name, false, DifficultyLevel.EASY,
                        boardForAI.getSize(), '.', 'S', 'X', 'O');
                player2.setBoard(boardForAI);
            }
            case 2 -> { // Gracz vs Komputer
                view.displayMessage("Podaj nazwę Gracza 1:");
                String playerName = scanner.nextLine();
                DifficultyLevel aiDifficulty = chooseAiDifficulty();

                player1 = new Player(playerName, false, DifficultyLevel.EASY,
                        boardForUser.getSize(), '.', 'S', 'X', 'O');
                player1.setBoard(boardForUser);

                AIStrategy strategy = selectAIStrategy(aiDifficulty);

                player2 = new AIPlayer("Komputer", aiDifficulty,
                        boardForAI.getSize(), '.', 'S', 'X', 'O', strategy);
                player2.setBoard(boardForAI);
            }
            case 3 -> { // Komputer vs Komputer (symulacja)
                DifficultyLevel ai1Difficulty = chooseAiDifficulty("Ustaw trudność dla AI1: 1. EASY, 2. MEDIUM, 3. HARD");
                DifficultyLevel ai2Difficulty = chooseAiDifficulty("Ustaw trudność dla AI2: 1. EASY, 2. MEDIUM, 3. HARD");

                AIStrategy ai1Strategy = selectAIStrategy(ai1Difficulty);
                AIStrategy ai2Strategy = selectAIStrategy(ai2Difficulty);

                player1 = new AIPlayer("AI1", ai1Difficulty,
                        boardForUser.getSize(), '.', 'S', 'X', 'O', ai1Strategy);
                player1.setBoard(boardForUser);

                player2 = new AIPlayer("AI2", ai2Difficulty,
                        boardForAI.getSize(), '.', 'S', 'X', 'O', ai2Strategy);
                player2.setBoard(boardForAI);
            }
            default -> {
                view.displayMessage("Nieprawidłowy wybór! Kończę działanie.");
                System.exit(0);
            }
        }

        // Teraz tworzymy grę i uruchamiamy
        this.game = new BattleshipGame(player1, player2);

        // Wywołujemy naszą starą metodę, która faktycznie rozgrywa grę (pętla strzałów itd.)
        startGame();
    }

    /**
     * Metoda, która uruchamia główną pętlę rozgrywki.
     * (Ta część już była w GameController w Twoim kodzie).
     */
    public void startGame() {
        // Zawartość Twojego dotychczasowego startGame() - bez zmian
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        // 1. Rozmieszczenie statków
        placeShipsForPlayer(player1);
        placeShipsForPlayer(player2);

        // 2. Główna pętla gry
        game.getStatistics().incrementTotalGames();
        Player currentPlayer = player1;
        Player opponent = player2;

        while (true) {
            roundCount++;
            view.printBoards(player1, player2, determineGameMode(player1, player2));

            if (!currentPlayer.isAI()) {
                view.displayMessage("Tura gracza: " + currentPlayer.getName());
                view.displayMessage("Wpisz 'undo', aby cofnąć ruch, lub naciśnij Enter, aby kontynuować.");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("undo")) {
                    if (currentPlayer.undoLastCommand()) {
                        view.displayMessage("Cofnięto ostatni ruch.");
                        continue;
                    } else {
                        view.displayMessage("Brak ruchów do cofnięcia.");
                        continue;
                    }
                }
            }

            executePlayerTurn(currentPlayer, opponent);

            if (checkVictoryCondition(currentPlayer, opponent)) {
                break;
            }

            if (currentPlayer.isAI() && opponent.isAI()) {
                view.displayMessage("Naciśnij Enter, aby przejść do kolejnego ruchu...");
                scanner.nextLine();
            } else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Zmiana tury
            Player temp = currentPlayer;
            currentPlayer = opponent;
            opponent = temp;
        }

        // Po zakończeniu gry
        view.displayMessage(game.getStatistics().getRanking());
        view.displayMessage(game.getStatistics().getGameHistory());
    }

    // ----------------------------
    // Metody pomocnicze przeniesione z Main (lub analogiczne)
    // ----------------------------

    private DifficultyLevel chooseAiDifficulty() {
        return chooseAiDifficulty("Wybierz poziom trudności AI: 1. EASY, 2. MEDIUM, 3. HARD");
    }

    private DifficultyLevel chooseAiDifficulty(String message) {
        view.displayMessage(message);
        int choice = readInt();
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

    private AIStrategy selectAIStrategy(DifficultyLevel difficulty) {
        return switch (difficulty) {
            case EASY -> new RandomStrategy();
            case MEDIUM -> new HuntStrategy();
            case HARD -> new SystematicStrategy();
        };
    }

    /**
     * Konfigurowanie planszy interaktywnie (przeniesione z Main).
     */
    private Board configureBoardInteractively() {
        view.displayMessage("Podaj rozmiar planszy:");
        int size = readInt();

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
            stones = collectStones();
        }

        BoardBuilderInterface builder = BoardBuilder.builder();
        BoardDirector director = new BoardDirector(builder);
        return director.constructBoard(size, stones, waterChar, shipChar, hitChar, missChar);
    }

    private Board configureStandardBoard() {
        BoardBuilderInterface builder = BoardBuilder.builder();
        BoardDirector director = new BoardDirector(builder);
        return director.constructStandardBoard();
    }

    private List<Stone> collectStones() {
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

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                view.displayMessage("Nieprawidłowa liczba. Spróbuj ponownie.");
            }
        }
    }

    // --------------------------------------------------------
    // Reszta metod (placeShipsForPlayer, executePlayerTurn, checkVictoryCondition itd.)
    // pozostaje bez zmian – już masz je w GameController.
    // --------------------------------------------------------

    private void placeShipsForPlayer(Player player) {
        if (!player.isAI()) {
            view.displayMessage("Rozmieszczanie statków dla Gracza (" + player.getName() + ")");
            view.printBoard(player.getBoard(), true);
            placeShipsManually(player);
        } else {
            view.displayMessage("Rozmieszczanie statków dla Komputera (" + player.getName() + ")");
            placeShipsStandard(player);
        }
    }

    private void placeShipsManually(Player player) {
        // ... (bez zmian)
        Board board = player.getBoard();
        int[] shipSizes = {4, 3};

        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                view.displayMessage("Rozmieszczanie statku o długości " + size);
                view.printBoard(board, true);
                view.displayMessage("Podaj wiersz początkowy (0-" + (board.getSize() - 1) + "): ");
                int startRow = readInt();
                view.displayMessage("Podaj kolumnę początkową (0-" + (board.getSize() - 1) + "): ");
                int startCol = readInt();
                view.displayMessage("Wybierz orientację: 1. Pozioma (h), 2. Pionowa (v)");
                String orientation = scanner.next().toLowerCase();
                scanner.nextLine();

                Ship ship = new Ship();
                boolean validOrientation = true;
                for (int i = 0; i < size; i++) {
                    if (orientation.equals("1") || orientation.equals("h")) {
                        ship.addPosition(new Position(startRow, startCol + i));
                    } else if (orientation.equals("2") || orientation.equals("v")) {
                        ship.addPosition(new Position(startRow + i, startCol));
                    } else {
                        view.displayMessage("Nieprawidłowa orientacja. Spróbuj ponownie.");
                        ship.getPositions().clear();
                        validOrientation = false;
                        break;
                    }
                }
                if (!validOrientation) {
                    continue;
                }
                placed = board.placeShip(ship);
                if (!placed) {
                    view.displayMessage("Nie można umieścić statku w podanym miejscu. Spróbuj ponownie.");
                }
            }
        }
        view.printBoard(board, true);
        view.displayMessage("Rozmieszczanie statków zakończone. Naciśnij Enter, aby kontynuować.");
        scanner.nextLine();
    }

    private void placeShipsStandard(Player player) {
        // ... (bez zmian)
        Board board = player.getBoard();
        int[] shipSizes = {4, 3};

        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                int row = (int) (Math.random() * board.getSize());
                int col = (int) (Math.random() * board.getSize());
                boolean horizontal = Math.random() < 0.5;

                Ship ship = new Ship();
                for (int i = 0; i < size; i++) {
                    if (horizontal) {
                        ship.addPosition(new Position(row, col + i));
                    } else {
                        ship.addPosition(new Position(row + i, col));
                    }
                }
                placed = board.placeShip(ship);
            }
        }
    }

    private void executePlayerTurn(Player currentPlayer, Player opponent) {
        Position shot;
        Board opponentBoard = opponent.getBoard();

        if (!currentPlayer.isAI()) {
            shot = getShotFromUser();
        } else {
            shot = game.getAiShot(currentPlayer, opponentBoard);
            view.displayMessage("AI (" + currentPlayer.getName() + ") wybrało strzał: " + shot);
        }

        Command moveCommand = new MoveCommand(opponentBoard, shot);
        currentPlayer.executeCommand(moveCommand);

        char cellState = opponentBoard.getGrid()[shot.getRow()][shot.getCol()];
        if (cellState == opponentBoard.getHitChar()) {
            view.displayMessage("Trafiony!");
        } else if (cellState == opponentBoard.getMissChar()) {
            view.displayMessage("Pudło.");
        }
    }

    private boolean checkVictoryCondition(Player currentPlayer, Player opponent) {
        if (opponent.getBoard().allShipsSunk()) {
            view.displayMessage("Wszystkie statki zatopione! Wygrywa: " + currentPlayer.getName());
            game.getStatistics().addWinForPlayer(currentPlayer.getName());
            game.getStatistics().addGameHistoryEntry(
                    new GameHistoryEntry(determineGameMode(game.getPlayer1(), game.getPlayer2()), currentPlayer.getName(), roundCount)
            );
            if (roundCount <= 10) {
                unlockAchievement("Zwycięstwo w 10 ruchach!");
            }
            return true;
        }
        return false;
    }

    private Position getShotFromUser() {
        view.displayMessage("Podaj wiersz (0-9): ");
        int row = readInt();
        view.displayMessage("Podaj kolumnę (0-9): ");
        int col = readInt();
        return new Position(row, col);
    }

    private void unlockAchievement(String achievementDescription) {
        for (Achievement a : game.getAchievements()) {
            if (a.getDescription().equals(achievementDescription) && !a.isUnlocked()) {
                a.unlock();
                view.displayMessage("[Osiągnięcie] Odblokowano: " + achievementDescription);
            }
        }
    }

    private String determineGameMode(Player p1, Player p2) {
        if (!p1.isAI() && !p2.isAI()) {
            return "PVP";
        } else if (p1.isAI() && p2.isAI()) {
            return "AI vs AI";
        } else {
            return "PVE";
        }
    }
}
