package controller;

import model.*;
import view.ConsoleView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameController {
    private BattleshipGame game;
    private ConsoleView view;
    private Scanner scanner;

    // Licznik rund (ile łącznie wykonano ruchów)
    private int roundCount;

    // Pancerze
    private int armorTokensPlayer1 = 2;
    private int armorTokensPlayer2 = 2;

    // Domyślne rozmiary statków, jeśli gracz nie zmieni
    private int[] defaultShipSizes = {4, 3};

    public GameController() {
        this.view = new ConsoleView();
        this.scanner = new Scanner(System.in);
        this.roundCount = 0;
    }

    public GameStatistics getStatistics() {
        if (game != null) {
            return game.getStatistics();
        }
        return new GameStatistics();
    }

    /**
     * Rozpoczyna procedurę konfiguracji gry i uruchamia rozgrywkę.
     */
    public void setupGame() {
        // Próbujemy wczytać statystyki z pliku przed uruchomieniem nowej gry
        // (wczytywanie do tymczasowego obiektu)
        try {
            GameStatistics tempStats = new GameStatistics();
            tempStats.loadFromFile("stats.txt");
        } catch (IOException e) {
            view.displayMessage("Nie udało się wczytać statystyk (stats.txt), zaczynamy od 0.");
        }

        // Wybór trybu
        view.displayMessage("Wybierz tryb gry:");
        view.displayMessage("1. Gracz vs Gracz");
        view.displayMessage("2. Gracz vs Komputer");
        view.displayMessage("3. Komputer vs Komputer (symulacja)");
        int choice = readInt();

        Board boardForUser;
        Board boardForAI;

        // 1. Ustalamy, czy chcemy standard czy interaktywny builder
        boolean useInteractiveBoard = false;
        if (choice == 1 || choice == 2) {
            view.displayMessage("Czy chcesz skonfigurować planszę interaktywnie? (tak/nie)");
            String interactiveConfig = scanner.nextLine().trim();
            useInteractiveBoard = interactiveConfig.equalsIgnoreCase("tak");
        }

        // Tworzymy dwie plansze
        if (useInteractiveBoard) {
            boardForUser = configureBoardInteractively();
            // Druga (np. AI) – ta sama wielkość i znaki
            boardForAI = new Board(
                    boardForUser.getSize(),
                    boardForUser.getWaterChar(),
                    boardForUser.getShipChar(),
                    boardForUser.getHitChar(),
                    boardForUser.getMissChar()
            );
        } else {
            // Standard
            if (choice == 3) {
                // Komputer vs Komputer
                Board std = configureStandardBoard();
                boardForUser = new Board(std.getSize(), std.getWaterChar(), std.getShipChar(), std.getHitChar(), std.getMissChar());
                boardForAI = new Board(std.getSize(), std.getWaterChar(), std.getShipChar(), std.getHitChar(), std.getMissChar());
            } else {
                // PVP lub PVE – też standard
                boardForUser = configureStandardBoard();
                boardForAI = new Board(boardForUser.getSize(),
                        boardForUser.getWaterChar(),
                        boardForUser.getShipChar(),
                        boardForUser.getHitChar(),
                        boardForUser.getMissChar());
            }
        }

        // 2. Ustalamy, ile i jakie statki – zapytajmy gracza
        int[] shipSizes = askForShipSizes(); // nowa metoda

        // 3. Tworzymy player1 i player2
        Player player1, player2;
        switch (choice) {
            case 1 -> {
                // PVP
                view.displayMessage("Podaj nazwę Gracza 1:");
                String p1Name = scanner.nextLine();
                view.displayMessage("Podaj nazwę Gracza 2:");
                String p2Name = scanner.nextLine();

                player1 = new Player(
                        p1Name,
                        DifficultyLevel.EASY, // placeholder
                        boardForUser.getSize(),
                        boardForUser.getWaterChar(),
                        boardForUser.getShipChar(),
                        boardForUser.getHitChar(),
                        boardForUser.getMissChar()
                );
                player1.setBoard(boardForUser);

                player2 = new Player(
                        p2Name,
                        DifficultyLevel.EASY,
                        boardForAI.getSize(),
                        boardForAI.getWaterChar(),
                        boardForAI.getShipChar(),
                        boardForAI.getHitChar(),
                        boardForAI.getMissChar()
                );
                player2.setBoard(boardForAI);

                armorTokensPlayer1 = 2;
                armorTokensPlayer2 = 2;
            }
            case 2 -> {
                // Gracz vs Komputer
                view.displayMessage("Podaj nazwę Gracza 1:");
                String playerName = scanner.nextLine();
                DifficultyLevel aiDiff = chooseAiDifficulty();

                player1 = new Player(
                        playerName,
                        DifficultyLevel.EASY, // ludzki gracz
                        boardForUser.getSize(),
                        boardForUser.getWaterChar(),
                        boardForUser.getShipChar(),
                        boardForUser.getHitChar(),
                        boardForUser.getMissChar()
                );
                player1.setBoard(boardForUser);

                AIStrategy strategy = selectAIStrategy(aiDiff);
                player2 = new AIPlayer(
                        "Komputer",
                        aiDiff,
                        boardForAI.getSize(),
                        boardForAI.getWaterChar(),
                        boardForAI.getShipChar(),
                        boardForAI.getHitChar(),
                        boardForAI.getMissChar(),
                        strategy
                );
                player2.setBoard(boardForAI);

                armorTokensPlayer1 = 2;
                armorTokensPlayer2 = 2;
            }
            case 3 -> {
                // AI vs AI
                DifficultyLevel ai1Diff = chooseAiDifficulty("Ustaw trudność dla AI1: 1. EASY, 2. MEDIUM, 3. HARD");
                DifficultyLevel ai2Diff = chooseAiDifficulty("Ustaw trudność dla AI2: 1. EASY, 2. MEDIUM, 3. HARD");

                AIStrategy ai1Strategy = selectAIStrategy(ai1Diff);
                AIStrategy ai2Strategy = selectAIStrategy(ai2Diff);

                player1 = new AIPlayer(
                        "AI1",
                        ai1Diff,
                        boardForUser.getSize(),
                        boardForUser.getWaterChar(),
                        boardForUser.getShipChar(),
                        boardForUser.getHitChar(),
                        boardForUser.getMissChar(),
                        ai1Strategy
                );
                player1.setBoard(boardForUser);

                player2 = new AIPlayer(
                        "AI2",
                        ai2Diff,
                        boardForAI.getSize(),
                        boardForAI.getWaterChar(),
                        boardForAI.getShipChar(),
                        boardForAI.getHitChar(),
                        boardForAI.getMissChar(),
                        ai2Strategy
                );
                player2.setBoard(boardForAI);

                armorTokensPlayer1 = 2;
                armorTokensPlayer2 = 2;
            }
            default -> {
                view.displayMessage("Nieprawidłowy wybór – anuluję.");
                return;
            }
        }

        // 4. Tworzymy obiekt gry
        this.game = new BattleshipGame(player1, player2);
        // Wczytujemy statystyki do nowego obiektu
        try {
            this.game.getStatistics().loadFromFile("stats.txt");
        } catch (IOException e) {
            view.displayMessage("Brak pliku statystyk albo błąd wczytywania.");
        }

        // 5. Rozpoczęcie rozgrywki z uwzględnieniem shipSizes
        startGame(shipSizes);
    }

    /**
     * Główna pętla rozgrywki: przyjmujemy shipSizes (ile i jakie statki).
     */
    public void startGame(int[] shipSizes) {
        Player p1 = game.getPlayer1();
        Player p2 = game.getPlayer2();

        // Rozmieszczenie statków
        placeShipsForPlayer(p1, shipSizes);
        placeShipsForPlayer(p2, shipSizes);

        // Główna pętla
        game.getStatistics().incrementTotalGames();
        Player currentPlayer = p1;
        Player opponent = p2;

        while (true) {
            roundCount++;
            view.printBoards(p1, p2, determineGameMode(p1, p2));

            if (currentPlayer instanceof AIPlayer ai) {
                view.displayMessage("Tura gracza AI: " + currentPlayer.getName());
                MoveCommand cmd = ai.makeMove();
                currentPlayer.executeCommand(cmd);
            } else {
                view.displayMessage("Tura gracza: " + currentPlayer.getName());
                Position shot = getShotFromUserOrUndo(currentPlayer, opponent.getBoard());
                if (shot == null) {
                    // cofnięcie
                    continue;
                }
                Command moveCommand = new MoveCommand(opponent.getBoard(), shot);
                currentPlayer.executeCommand(moveCommand);
            }

            if (checkVictoryCondition(currentPlayer, opponent)) {
                break;
            }

            // AI vs AI => pauza
            if (currentPlayer instanceof AIPlayer && opponent instanceof AIPlayer) {
                view.displayMessage("ENTER => kolejny ruch AI vs AI...");
                scanner.nextLine();
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Zmiana tury
            Player tmp = currentPlayer;
            currentPlayer = opponent;
            opponent = tmp;
        }

        // Zapis statystyk
        try {
            game.getStatistics().saveToFile("stats.txt");
        } catch (IOException e) {
            view.displayMessage("Błąd zapisu statystyk: " + e.getMessage());
        }

        // Wyświetlamy końcowy ranking i historię
        view.displayMessage(game.getStatistics().getRanking());
        view.displayMessage(game.getStatistics().getGameHistory());
    }

    /**
     * Wczytuje od gracza liczbę statków i ich rozmiary.
     * Jeśli gracz wybierze "domyślne", zwracamy np. [4,3].
     */
    private int[] askForShipSizes() {
        view.displayMessage("Czy chcesz użyć domyślnej listy statków (4 i 3)? (tak/nie)");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("tak")) {
            return defaultShipSizes;
        } else {
            // Niestandardowe
            view.displayMessage("Ile statków chcesz rozmieszczać?");
            int count = readInt();
            int[] sizes = new int[count];
            for (int i = 0; i < count; i++) {
                view.displayMessage("Podaj rozmiar statku nr " + (i+1) + ":");
                sizes[i] = readInt();
            }
            return sizes;
        }
    }

    // -------------
    // Metody do rozgrywki
    // -------------

    private boolean checkVictoryCondition(Player currentPlayer, Player opponent) {
        if (opponent.getBoard().allShipsSunk()) {
            view.displayMessage("Wszystkie statki zatopione! Wygrywa: " + currentPlayer.getName());
            game.getStatistics().addWinForPlayer(currentPlayer.getName());
            game.getStatistics().addGameHistoryEntry(
                    new GameHistoryEntry(determineGameMode(game.getPlayer1(), game.getPlayer2()),
                            currentPlayer.getName(), roundCount)
            );
            if (roundCount <= 10) {
                unlockAchievement("Zwycięstwo w 10 ruchach!");
            }
            return true;
        }
        return false;
    }

    private String determineGameMode(Player p1, Player p2) {
        boolean p1AI = (p1 instanceof AIPlayer);
        boolean p2AI = (p2 instanceof AIPlayer);
        if (!p1AI && !p2AI) return "PVP";
        if (p1AI && p2AI)   return "AI vs AI";
        return "PVE";
    }

    private void unlockAchievement(String achievementDescription) {
        for (Achievement a : game.getAchievements()) {
            if (a.getDescription().equals(achievementDescription) && !a.isUnlocked()) {
                a.unlock();
                view.displayMessage("[Osiągnięcie] Odblokowano: " + achievementDescription);
            }
        }
    }

    private Position getShotFromUserOrUndo(Player currentPlayer, Board opponentBoard) {
        while (true) {
            view.displayMessage("Podaj pozycję do strzału (x,y) albo 'undo': ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("undo")) {
                boolean undone = currentPlayer.undoLastCommand();
                if (undone) {
                    view.displayMessage("Cofnięto ostatni ruch.");
                } else {
                    view.displayMessage("Brak ruchów do cofnięcia.");
                }
                return null;
            }
            Position pos = parsePosition(line);
            if (pos == null) {
                view.displayMessage("Zły format (x,y). Spróbuj ponownie.");
                continue;
            }
            int size = opponentBoard.getSize();
            if (pos.getRow() < 0 || pos.getRow() >= size || pos.getCol() < 0 || pos.getCol() >= size) {
                view.displayMessage("Poza planszą! Spróbuj ponownie.");
                continue;
            }
            return pos;
        }
    }

    // -------------
    // Rozmieszczanie statków
    // -------------

    private void placeShipsForPlayer(Player player, int[] shipSizes) {
        if (player instanceof AIPlayer) {
            view.displayMessage("Rozmieszczanie statków (AI): " + player.getName());
            placeShipsStandard(player, shipSizes);
        } else {
            view.displayMessage("Rozmieszczanie statków (Gracz): " + player.getName());
            view.printBoard(player.getBoard(), true);
            placeShipsManually(player, shipSizes);
        }
    }

    private void placeShipsManually(Player player, int[] shipSizes) {
        Board board = player.getBoard();

        int myArmorTokens = (player == game.getPlayer1()) ? armorTokensPlayer1 : armorTokensPlayer2;

        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                view.displayMessage("Rozmieszczanie statku o długości " + size);
                view.printBoard(board, true);

                view.displayMessage("Podaj start (x,y): ");
                Position startPos = parsePosition(scanner.nextLine().trim());
                if (startPos == null) {
                    view.displayMessage("Zły format x,y.");
                    continue;
                }

                int startRow = startPos.getRow();
                int startCol = startPos.getCol();

                if (!checkWithinBoard(board, startRow, startCol)) {
                    view.displayMessage("Poza planszą. Spróbuj ponownie.");
                    continue;
                }

                view.displayMessage("Orientacja: (h) - pozioma, (v) - pionowa");
                String orientation = scanner.nextLine().trim().toLowerCase();

                IShip ship = new Ship();
                boolean validOrientation = true;
                for (int i = 0; i < size; i++) {
                    if (orientation.equals("h")) {
                        ship.addPosition(new Position(startRow, startCol + i));
                    } else if (orientation.equals("v")) {
                        ship.addPosition(new Position(startRow + i, startCol));
                    } else {
                        view.displayMessage("Nieprawidłowa orientacja. Wpisz 'h' lub 'v'.");
                        validOrientation = false;
                        break;
                    }
                }
                if (!validOrientation) {
                    continue;
                }

                placed = board.placeShip(ship);
                if (!placed) {
                    view.displayMessage("Nie można umieścić statku w podanym miejscu.");
                } else {
                    // Pytanie o pancerz
                    if (myArmorTokens > 0) {
                        view.displayMessage("Dodać pancerz? (tak/nie). Pozostało: " + myArmorTokens);
                        String armorChoice = scanner.nextLine().trim();
                        if (armorChoice.equalsIgnoreCase("tak")) {
                            IShip armored = new ArmoredShipDecorator(ship);
                            board.getShips().remove(ship);
                            board.getShips().add(armored);
                            myArmorTokens--;
                            view.displayMessage("Dodano pancerz!");
                        }
                    }
                }
            }
        }

        if (player == game.getPlayer1()) armorTokensPlayer1 = myArmorTokens;
        else armorTokensPlayer2 = myArmorTokens;

        view.printBoard(board, true);
        view.displayMessage("Rozmieszczanie zakończone. (Enter)");
        scanner.nextLine();
    }

    private void placeShipsStandard(Player player, int[] shipSizes) {
        Board board = player.getBoard();
        int myArmorTokens = (player == game.getPlayer1()) ? armorTokensPlayer1 : armorTokensPlayer2;

        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                int row = (int) (Math.random() * board.getSize());
                int col = (int) (Math.random() * board.getSize());
                boolean horizontal = Math.random() < 0.5;

                IShip ship = new Ship();
                for (int i = 0; i < size; i++) {
                    if (horizontal) {
                        ship.addPosition(new Position(row, col + i));
                    } else {
                        ship.addPosition(new Position(row + i, col));
                    }
                }
                placed = board.placeShip(ship);
                if (placed && myArmorTokens > 0) {
                    IShip armored = new ArmoredShipDecorator(ship);
                    board.getShips().remove(ship);
                    board.getShips().add(armored);
                    myArmorTokens--;
                }
            }
        }

        if (player == game.getPlayer1()) armorTokensPlayer1 = myArmorTokens;
        else armorTokensPlayer2 = myArmorTokens;
    }

    private boolean checkWithinBoard(Board board, int row, int col) {
        return row >= 0 && row < board.getSize() && col >= 0 && col < board.getSize();
    }

    // -------------
    // Metody do budowania planszy
    // -------------

    /**
     * Konfigurowanie w trybie interaktywnym.
     * Możesz ewentualnie pominąć część pytań i dać stałe parametry.
     */
    private Board configureBoardInteractively() {
        view.displayMessage("Podaj rozmiar planszy (np. 10):");
        int size = readInt();

        view.displayMessage("Podaj znak wody (np. '.'): ");
        char waterChar = scanner.nextLine().trim().charAt(0);

        view.displayMessage("Podaj znak statku (np. 'S'): ");
        char shipChar = scanner.nextLine().trim().charAt(0);

        view.displayMessage("Podaj znak trafienia (np. 'X'): ");
        char hitChar = scanner.nextLine().trim().charAt(0);

        view.displayMessage("Podaj znak pudła (np. 'O'): ");
        char missChar = scanner.nextLine().trim().charAt(0);

        view.displayMessage("Dodać kamienie? (tak/nie)");
        String addStones = scanner.nextLine().trim();
        List<Stone> stones = null;
        if (addStones.equalsIgnoreCase("tak")) {
            stones = collectStones();
        }

        BoardBuilderInterface builder = BoardBuilder.builder();
        BoardDirector director = new BoardDirector(builder);
        return director.constructBoard(size, stones, waterChar, shipChar, hitChar, missChar);
    }

    /**
     * Konfigurowanie w trybie standardowym.
     * Stały rozmiar 10 i znaki . S X O (lub dowolnie).
     */
    private Board configureStandardBoard() {
        BoardBuilderInterface builder = BoardBuilder.builder();
        BoardDirector director = new BoardDirector(builder);
        return director.constructStandardBoard();
    }

    private List<Stone> collectStones() {
        List<Stone> stones = new ArrayList<>();
        view.displayMessage("Podaj pozycje kamieni x,y lub wpisz 'koniec':");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("koniec")) {
                break;
            }
            Position pos = parsePosition(input);
            if (pos != null) {
                stones.add(new Stone(List.of(pos)));
            } else {
                view.displayMessage("Zły format. Użyj x,y.");
            }
        }
        return stones;
    }

    /**
     * Metoda do czytania int z linii.
     */
    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                view.displayMessage("Zła liczba, spróbuj ponownie.");
            }
        }
    }

    /**
     * Metoda do parsowania formatu x,y w Position.
     */
    private Position parsePosition(String input) {
        String[] parts = input.split(",");
        if (parts.length != 2) return null;
        try {
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());
            return new Position(x, y);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // -------------
    // Metody do AI
    // -------------

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
                view.displayMessage("Nieprawidłowy wybór, przyjmuję EASY.");
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
}
