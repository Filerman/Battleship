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

    // Pancerze – tokeny pancerza na gracza
    private int armorTokensPlayer1 = 2;
    private int armorTokensPlayer2 = 2;

    // Domyślne rozmiary statków, jeśli gracz nie zmieni
    private int[] defaultShipSizes = {4, 3};

    public GameController() {
        this.view = new ConsoleView();
        this.scanner = new Scanner(System.in);
        this.roundCount = 0;
    }

    /**
     * Zwraca statystyki gry (jeśli jest już utworzony obiekt game).
     */
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

        // Czy gracz chce konfigurować planszę interaktywnie
        boolean useInteractiveBoard = false;
        if (choice == 1 || choice == 2) {
            view.displayMessage("Czy chcesz skonfigurować planszę interaktywnie? (tak/nie)");
            String interactiveConfig = scanner.nextLine().trim();
            useInteractiveBoard = interactiveConfig.equalsIgnoreCase("tak");
        }

        // Tworzymy dwie plansze (dla p1 i p2) w zależności od wyboru
        if (useInteractiveBoard) {
            boardForUser = configureBoardInteractively();
            // Druga plansza (np. AI) – ta sama wielkość i symbole
            boardForAI = new Board(
                    boardForUser.getSize(),
                    boardForUser.getWaterChar(),
                    boardForUser.getShipChar(),
                    boardForUser.getHitChar(),
                    boardForUser.getMissChar()
            );
        } else {
            if (choice == 3) {
                // AI vs AI
                Board std = configureStandardBoard();
                boardForUser = new Board(std.getSize(), std.getWaterChar(), std.getShipChar(), std.getHitChar(), std.getMissChar());
                boardForAI = new Board(std.getSize(), std.getWaterChar(), std.getShipChar(), std.getHitChar(), std.getMissChar());
            } else {
                // PVP lub PVE
                boardForUser = configureStandardBoard();
                boardForAI = new Board(boardForUser.getSize(),
                        boardForUser.getWaterChar(),
                        boardForUser.getShipChar(),
                        boardForUser.getHitChar(),
                        boardForUser.getMissChar());
            }
        }

        // Wczytujemy rozmiary statków od gracza (lub domyślne)
        int[] shipSizes = askForShipSizes();

        // Tworzymy graczy (p1 i p2) w zależności od wybranego trybu
        Player player1, player2;
        switch (choice) {
            case 1 -> {
                // Gracz vs Gracz
                view.displayMessage("Podaj nazwę Gracza 1:");
                String p1Name = scanner.nextLine();
                view.displayMessage("Podaj nazwę Gracza 2:");
                String p2Name = scanner.nextLine();

                player1 = new Player(
                        p1Name,
                        DifficultyLevel.EASY, // placeholder – to i tak gracz ludzki
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
                // Komputer vs Komputer (AI vs AI)
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

        // Tworzymy lub pobieramy instancję Singletona BattleshipGame
        this.game = BattleshipGame.getInstance(player1, player2);

        // Wczytujemy statystyki do singletona
        try {
            this.game.getStatistics().loadFromFile("stats.txt");
        } catch (IOException e) {
            view.displayMessage("Brak pliku statystyk albo błąd wczytywania.");
        }

        // Sprawdzamy, czy gracz-ludzki ma >= 3 wygranych i czy chce zmienić kolor statków
        checkAndOfferColorChange(player1);
        if (!(player2 instanceof AIPlayer)) {
            checkAndOfferColorChange(player2);
        }

        // Start właściwej rozgrywki
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

        // Zwiększamy licznik gier o 1
        game.getStatistics().incrementTotalGames();

        Player currentPlayer = p1;
        Player opponent = p2;

        // Główna pętla tury
        while (true) {
            roundCount++;
            view.printBoards(p1, p2, determineGameMode(p1, p2));

            if (currentPlayer instanceof AIPlayer ai) {
                // Ruch AI
                view.displayMessage("Tura gracza AI: " + currentPlayer.getName());
                MoveCommand cmd = ai.makeMove();
                currentPlayer.executeCommand(cmd);
            } else {
                // Ruch zwykłego gracza
                view.displayMessage("Tura gracza: " + currentPlayer.getName());
                Position shot = getShotFromUserOrUndo(currentPlayer, opponent.getBoard());
                if (shot == null) {
                    // Cofnięcie ruchu
                    continue;
                }
                Command moveCommand = new MoveCommand(opponent.getBoard(), shot);
                currentPlayer.executeCommand(moveCommand);
            }

            // Sprawdzamy warunek zwycięstwa
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

        // Zapis statystyk po zakończeniu gry
        try {
            game.getStatistics().saveToFile("stats.txt");
        } catch (IOException e) {
            view.displayMessage("Błąd zapisu statystyk: " + e.getMessage());
        }

        // Wyświetlamy końcowy ranking i historię
        view.displayMessage(game.getStatistics().getRanking());
        view.displayMessage(game.getStatistics().getGameHistory());

        // Resetujemy instancję Singletona, by umożliwić nową grę "od zera"
        BattleshipGame.resetInstance();
    }

    /**
     * Metoda wczytująca rozmiary statków od gracza.
     * Jeśli gracz wybierze "tak" – mamy domyślne [4, 3].
     * W przeciwnym razie pyta, ile statków i o rozmiary każdego z nich.
     */
    private int[] askForShipSizes() {
        view.displayMessage("Czy chcesz użyć domyślnej listy statków (4 i 3)? (tak/nie)");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("tak")) {
            return defaultShipSizes;
        } else {
            view.displayMessage("Ile statków chcesz rozmieszczać?");
            int count = readInt();
            int[] sizes = new int[count];
            for (int i = 0; i < count; i++) {
                view.displayMessage("Podaj rozmiar statku nr " + (i + 1) + ":");
                sizes[i] = readInt();
            }
            return sizes;
        }
    }

    /**
     * Sprawdza, czy dany gracz-ludzki ma >= 3 wygranych;
     * jeśli tak – może otrzymać nagrodę zmiany symbolu statków.
     */
    private void checkAndOfferColorChange(Player player) {
        if (player instanceof AIPlayer) return;  // AI ignorujemy

        // Sprawdzamy liczbę wygranych
        int wins = this.game.getStatistics().getWinsForPlayer(player.getName());
        if (wins >= 3) {
            // Oferujemy zmianę symbolu
            view.displayMessage("Graczu " + player.getName()
                    + ", wygrałeś już " + wins + " gier! Możesz zmienić symbol statków. Zrobić to? (tak/nie)");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("tak")) {
                view.displayMessage("Podaj nowy symbol dla statków (np. 'S'): ");
                String line = scanner.nextLine().trim();
                char newShipChar = line.isEmpty() ? 'S' : line.charAt(0);

                Board b = player.getBoard();
                // Zmieniamy wszystkie dotychczasowe statki (i w tablicy grid) na nowy znak
                for (IShip s : b.getShips()) {
                    for (Position pos : s.getPositions()) {
                        b.getGrid()[pos.getRow()][pos.getCol()] = newShipChar;
                    }
                }
                // Zmieniamy też docelowy 'shipChar' w Board (np. przez reflection lub setter)
                try {
                    java.lang.reflect.Field f =
                            Board.class.getDeclaredField("shipChar");
                    f.setAccessible(true);
                    f.set(b, newShipChar);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                view.displayMessage("Zmieniono symbol statku na: " + newShipChar);
            }
        }
    }

    /**
     * Sprawdza, czy przeciwnik ma wszystkie statki zatopione.
     * Jeśli tak, aktualny gracz wygrywa.
     */
    private boolean checkVictoryCondition(Player currentPlayer, Player opponent) {
        if (opponent.getBoard().allShipsSunk()) {
            view.displayMessage("Wszystkie statki zatopione! Wygrywa: " + currentPlayer.getName());
            game.getStatistics().addWinForPlayer(currentPlayer.getName());
            game.getStatistics().addGameHistoryEntry(
                    new GameHistoryEntry(determineGameMode(game.getPlayer1(), game.getPlayer2()),
                            currentPlayer.getName(), roundCount)
            );

            // Osiągnięcie: Zwycięstwo w 10 ruchach
            if (roundCount <= 10) {
                unlockAchievement("Zwycięstwo w 10 ruchach!");
            }

            // Sprawdzamy, czy odblokować achievement "3 wins - color change"
            int winsNow = game.getStatistics().getWinsForPlayer(currentPlayer.getName());
            if (winsNow >= 3) {
                unlockAchievement("3 wins - color change");
            }
            return true;
        }
        return false;
    }

    /**
     * Odblokowuje osiągnięcie (jeśli jeszcze nie jest odblokowane).
     */
    private void unlockAchievement(String achievementDescription) {
        for (Achievement a : game.getAchievements()) {
            if (a.getDescription().equals(achievementDescription) && !a.isUnlocked()) {
                a.unlock();
                view.displayMessage("[Osiągnięcie] Odblokowano: " + achievementDescription);
            }
        }
    }

    /**
     * Określa tryb gry (PVP, PVE, AI vs AI).
     */
    private String determineGameMode(Player p1, Player p2) {
        boolean p1AI = (p1 instanceof AIPlayer);
        boolean p2AI = (p2 instanceof AIPlayer);
        if (!p1AI && !p2AI) return "PVP";
        if (p1AI && p2AI)   return "AI vs AI";
        return "PVE";
    }

    /**
     * Metoda do wczytywania ruchu od gracza.
     * Zwraca pozycję albo null, jeśli gracz wpisał 'undo' i cofnięto ruch.
     */
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

    /**
     * Rozmieszcza statki dla gracza (ręcznie lub automatycznie w zależności od rodzaju gracza).
     */
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

    /**
     * Ręczne rozmieszczanie statków przez gracza.
     */
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
                    view.displayMessage("Nie można umieścić statku w podanym miejscu (zajęte / poza planszą).");
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

        if (player == game.getPlayer1()) {
            armorTokensPlayer1 = myArmorTokens;
        } else {
            armorTokensPlayer2 = myArmorTokens;
        }

        view.printBoard(board, true);
        view.displayMessage("Rozmieszczanie zakończone. (Enter, aby kontynuować)");
        scanner.nextLine();
    }

    /**
     * Automatyczne rozmieszczanie statków dla AI (lub symulacji).
     */
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

        if (player == game.getPlayer1()) {
            armorTokensPlayer1 = myArmorTokens;
        } else {
            armorTokensPlayer2 = myArmorTokens;
        }
    }

    /**
     * Sprawdza, czy wiersz i kolumna znajdują się w obszarze planszy.
     */
    private boolean checkWithinBoard(Board board, int row, int col) {
        return row >= 0 && row < board.getSize() && col >= 0 && col < board.getSize();
    }

    /**
     * Metoda do konfiguracji planszy w trybie interaktywnym.
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
     * Konfiguracja planszy w trybie standardowym (ustawienia domyślne).
     */
    private Board configureStandardBoard() {
        BoardBuilderInterface builder = BoardBuilder.builder();
        BoardDirector director = new BoardDirector(builder);
        return director.constructStandardBoard();
    }

    /**
     * Zbiera listę kamieni od użytkownika w formacie x,y.
     */
    private List<Stone> collectStones() {
        List<Stone> stones = new ArrayList<>();
        view.displayMessage("Podaj pozycje kamieni w formacie x,y lub wpisz 'koniec':");
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
     * Metoda do wczytania liczby całkowitej z linii wejścia.
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
     * Metoda do parsowania formatu x,y w obiekt Position.
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

    /**
     * Wybór poziomu trudności AI (metoda przeciążona, jeśli chcemy customowy komunikat).
     */
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

    /**
     * Wybór strategii AI w zależności od poziomu trudności.
     */
    private AIStrategy selectAIStrategy(DifficultyLevel difficulty) {
        return switch (difficulty) {
            case EASY -> new RandomStrategy();
            case MEDIUM -> new HuntStrategy();
            case HARD -> new SystematicStrategy();
        };
    }
}
