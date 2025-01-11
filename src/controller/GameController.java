package controller;

import model.*;
import view.ConsoleView;

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

    public GameController(BattleshipGame game, ConsoleView view) {
        this.game = game;
        this.view = view;
        this.scanner = new Scanner(System.in);
        this.roundCount = 0;
    }

    /**
     * Rozpoczęcie rozgrywki: rozmieszczenie statków, następnie tury strzałów.
     */
    public void startGame() {
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        // Rozmieszczanie statków dla obu graczy
        view.displayMessage("Rozmieszczanie statków dla Gracza 1 (" + player1.getName() + ")");
        if (!player1.isAI()) {
            placeShipsManually(player1);
        } else {
            placeShipsStandard(player1);
        }

        view.displayMessage("Rozmieszczanie statków dla Gracza 2 (" + player2.getName() + ")");
        if (!player2.isAI()) {
            placeShipsManually(player2);
        } else {
            placeShipsStandard(player2);
        }

        // Główna pętla gry
        game.getStatistics().incrementTotalGames();
        Player currentPlayer = player1;
        Player opponent = player2;

        while (true) {
            roundCount++;
            view.printBoards(player1, player2);

            view.displayMessage("Tura gracza: " + currentPlayer.getName());

            Board opponentBoard = opponent.getBoard();
            Position shot;

            if (!currentPlayer.isAI()) {
                // Ruch gracza
                shot = getShotFromUser();
            } else {
                // Ruch AI
                shot = game.getAiShot(currentPlayer, opponentBoard);
                view.displayMessage("AI wybrało strzał: " + shot);
            }

            boolean hit = opponentBoard.shoot(shot);

            // Sprawdzanie osiągnięć
            checkAchievementsHitOrSunk(opponentBoard);

            // Sprawdzanie warunku zwycięstwa
            if (opponentBoard.allShipsSunk()) {
                view.displayMessage("Wszystkie statki zatopione! Wygrywa: " + currentPlayer.getName());
                game.getStatistics().addWinForPlayer(currentPlayer.getName());
                game.getStatistics().addGameHistoryEntry(
                        new GameHistoryEntry(determineGameMode(player1, player2), currentPlayer.getName(), roundCount)
                );
                if (roundCount <= 10) {
                    unlockAchievement("Zwycięstwo w 10 ruchach!");
                }
                break;
            }

            // Zmiana kolejki
            Player temp = currentPlayer;
            currentPlayer = opponent;
            opponent = temp;
        }

        // Wyświetlanie wyników gry
        view.displayMessage(game.getStatistics().getRanking());
        view.displayMessage(game.getStatistics().getGameHistory());
    }

    /**
     * Ręczne rozmieszczanie statków przez gracza.
     */
    private void placeShipsManually(Player player) {
        Board board = player.getBoard();
        int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                view.displayMessage("Rozmieszczanie statku o długości " + size);
                view.displayMessage("Podaj wiersz początkowy (0-" + (board.getSize() - 1) + "): ");
                int startRow = readInt();
                view.displayMessage("Podaj kolumnę początkową (0-" + (board.getSize() - 1) + "): ");
                int startCol = readInt();
                view.displayMessage("Wybierz orientację: 1. Pozioma (h), 2. Pionowa (v)");
                String orientation = scanner.next().toLowerCase();

                Ship ship = new Ship();
                for (int i = 0; i < size; i++) {
                    if (orientation.equals("1") || orientation.equals("h")) {
                        ship.addPosition(new Position(startRow, startCol + i));
                    } else if (orientation.equals("2") || orientation.equals("v")) {
                        ship.addPosition(new Position(startRow + i, startCol));
                    } else {
                        view.displayMessage("Nieprawidłowa orientacja. Spróbuj ponownie.");
                        continue;
                    }
                }

                placed = board.placeShip(ship);
                if (!placed) {
                    view.displayMessage("Nie można umieścić statku w podanym miejscu. Spróbuj ponownie.");
                }
            }

            view.printBoard(board, true);
        }
    }

    /**
     * Automatyczne rozmieszczanie statków (dla AI).
     */
    private void placeShipsStandard(Player player) {
        Board board = player.getBoard();
        int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

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

    /**
     * Pobiera współrzędne strzału od gracza.
     */
    private Position getShotFromUser() {
        view.displayMessage("Podaj wiersz (0-9): ");
        int row = readInt();
        view.displayMessage("Podaj kolumnę (0-9): ");
        int col = readInt();
        return new Position(row, col);
    }

    /**
     * Wczytuje liczbę całkowitą od użytkownika.
     */
    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                view.displayMessage("Nieprawidłowa liczba. Spróbuj ponownie.");
            }
        }
    }

    /**
     * Sprawdza osiągnięcia związane z trafieniami lub zatopieniem statków.
     */
    private void checkAchievementsHitOrSunk(Board opponentBoard) {
        for (Ship s : opponentBoard.getShips()) {
            if (s.isSunk()) {
                unlockAchievement("Pierwsze zatopienie!");
            }
        }
    }

    /**
     * Odblokowuje osiągnięcie o podanej nazwie.
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
     * Określa tryb gry na podstawie flag AI graczy.
     */
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
