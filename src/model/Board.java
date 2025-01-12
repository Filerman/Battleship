package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca planszę do gry.
 */
public class Board {
    private final int SIZE;               // Rozmiar planszy (np. 10)
    private char[][] grid;                // Wizualne odwzorowanie planszy
    private List<Ship> ships;
    private List<Position> shotsFired;    // Historia oddanych strzałów

    // Lista kamieni (przeszkód) na planszy
    private List<Stone> stones;

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
        this.stones = new ArrayList<>();

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

    /**
     * Umieszcza statek na planszy (rozmieszczanie statków odbywa się ręcznie).
     */
    public boolean placeShip(Ship ship) {
        // Sprawdzamy kolizje z innymi statkami
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
     * Umieszczanie kamienia (przeszkody) na planszy.
     */
    public boolean placeStone(Stone stone) {
        for (Position p : stone.getPositions()) {
            int r = p.getRow();
            int c = p.getCol();

            // Sprawdzamy, czy pozycja mieści się w planszy
            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
                return false;
            }
            // Sprawdzamy, czy pole nie jest już zajęte przez statek lub inny kamień ('K')
            if (grid[r][c] == shipChar || grid[r][c] == 'K') {
                return false;
            }
        }

        // Umieszczamy kamień – oznaczamy pole znakiem 'K'
        for (Position p : stone.getPositions()) {
            grid[p.getRow()][p.getCol()] = 'K';
        }
        stones.add(stone);
        return true;
    }

    /**
     * Oddanie strzału w daną pozycję.
     * Zwraca true, jeżeli trafiono w statek; false w przypadku pudła lub trafienia w kamień.
     */
    public boolean shoot(Position position) {
        int r = position.getRow();
        int c = position.getCol();

        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
            System.out.println("Strzał poza planszę!");
            return false;
        }
        if (shotsFired.contains(position)) {
            System.out.println("Już strzelano w to miejsce!");
            return false;
        }
        shotsFired.add(position);

        // Obsługa trafienia w kamień
        if (grid[r][c] == 'K') {
            System.out.println("Trafiłeś w kamień!");
            for (Stone stone : stones) {
                if (stone.getPositions().contains(position)) {
                    if (stone.isDestructible()) {
                        grid[r][c] = waterChar;
                        System.out.println("Kamień został zniszczony!");
                    } else {
                        System.out.println("Kamień jest niezniszczalny!");
                    }
                    break;
                }
            }
            return false;
        }

        // Sprawdzamy trafienie w statek
        if (grid[r][c] == shipChar) {
            grid[r][c] = hitChar;
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
