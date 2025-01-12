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
     * Rozpoczęcie rozgrywki: najpierw interaktywnie dodajemy kamienie (dla gracza, jeżeli nie jest AI),
     * następnie rozmieszczamy statki, a następnie tury strzałów.
     */
    public void startGame() {
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();


        // Dla gracza AI (komputera) możemy użyć domyślnego układu (builder już ustawił kamienie).

        // Następnie rozmieszczamy statki
        placeShipsForPlayer(player1);
        placeShipsForPlayer(player2);

        // Po rozmieszczeniu statków czyścimy bufor wejścia
        scanner.nextLine();

        // Główna pętla gry
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

            // W trybie AI vs AI czekamy na Enter, w innych automatycznie 2 sekundy
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

            Player temp = currentPlayer;
            currentPlayer = opponent;
            opponent = temp;
        }

        view.displayMessage(game.getStatistics().getRanking());
        view.displayMessage(game.getStatistics().getGameHistory());
    }




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

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                view.displayMessage("Nieprawidłowa liczba. Spróbuj ponownie.");
            }
        }
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