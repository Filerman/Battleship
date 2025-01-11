package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca statek.
 */
public class Ship {
    private List<Position> positions;
    private boolean sunk;

    public Ship() {
        this.positions = new ArrayList<>();
        this.sunk = false;
    }

    public void addPosition(Position position) {
        positions.add(position);
    }

    public List<Position> getPositions() {
        return positions;
    }

    /**
     * Sprawdza, czy dany strzał trafił w którąś z komórek statku.
     */
    public boolean isHit(Position shot) {
        for (Position position : positions) {
            if (position.equals(shot)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Oznacza statek jako zatopiony (jeśli wszystkie jego pola zostały trafione).
     */
    public void checkIfSunk(List<Position> hits) {
        for (Position pos : positions) {
            if (!hits.contains(pos)) {
                this.sunk = false;
                return;
            }
        }
        this.sunk = true;
    }

    public boolean isSunk() {
        return sunk;
    }
}
