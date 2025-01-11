package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca planszę do gry.
 */
public class Board {
    private final int SIZE;               // Rozmiar planszy (np. 10)
    private char[][] grid;                // wizualne odwzorowanie planszy
    private List<Ship> ships;
    private List<Position> shotsFired;    // historia oddanych strzałów

    // Znaki do personalizacji wyglądu
    private char waterChar;
    private char shipChar;
    private char hitChar;
    private char missChar;

    /**
     * Tworzy planszę z określonym rozmiarem i znakami wyglądu.
     */
    public Board(int size, char waterChar, char shipChar, char hitChar, char missChar) {
        this.SIZE = size;
        this.grid = new char[SIZE][SIZE];
        this.ships = new ArrayList<>();
        this.shotsFired = new ArrayList<>();
        this.waterChar = waterChar;
        this.shipChar = shipChar;
        this.hitChar = hitChar;
        this.missChar = missChar;
        initBoard();
    }

    private void initBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = waterChar;
            }
        }
    }

    public int getSize() {
        return SIZE;
    }

    public boolean placeShip(Ship ship) {
        // Sprawdza, czy można umieścić statek (czy nie koliduje z innym)
        for (Position p : ship.getPositions()) {
            int r = p.getRow();
            int c = p.getCol();
            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || grid[r][c] == shipChar) {
                return false;
            }
        }
        // Jeśli OK, umieszczamy statek
        for (Position p : ship.getPositions()) {
            int r = p.getRow();
            int c = p.getCol();
            grid[r][c] = shipChar;
        }
        ships.add(ship);
        return true;
    }

    /**
     * Oddanie strzału w daną pozycję.
     * Zwraca wartość true, jeżeli jest trafienie; false w przypadku pudła.
     */
    public boolean shoot(Position position) {
        int r = position.getRow();
        int c = position.getCol();

        // Strzał poza planszę
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
            System.out.println("Strzał poza planszę!");
            return false;
        }
        // Jeżeli już strzelaliśmy w to samo miejsce
        if (shotsFired.contains(position)) {
            System.out.println("Już strzelano w to miejsce!");
            return false;
        }
        shotsFired.add(position);

        // Sprawdzamy, czy trafiliśmy statek
        if (grid[r][c] == shipChar) {
            grid[r][c] = hitChar; // trafiony
            // Aktualizujemy stan statków (czy któryś nie został zatopiony?)
            for (Ship s : ships) {
                if (s.isHit(position)) {
                    s.checkIfSunk(shotsFired);
                    if (s.isSunk()) {
                        System.out.println("Zatopiłeś statek!");
                    } else {
                        System.out.println("Trafiony!");
                    }
                    return true;
                }
            }
        } else {
            // Pudło
            grid[r][c] = missChar;
            System.out.println("Pudło.");
            return false;
        }
        return false;
    }

    /**
     * Sprawdza, czy wszystkie statki na planszy są zatopione.
     */
    public boolean allShipsSunk() {
        for (Ship s : ships) {
            if (!s.isSunk()) {
                return false;
            }
        }
        return true;
    }

    public char[][] getGrid() {
        return grid;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public List<Position> getShotsFired() {
        return shotsFired;
    }

    public char getWaterChar() {
        return waterChar;
    }

    public char getShipChar() {
        return shipChar;
    }

    public char getHitChar() {
        return hitChar;
    }

    public char getMissChar() {
        return missChar;
    }
}
