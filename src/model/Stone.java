package model;

import java.util.List;

/**
 * Klasa reprezentująca "kamień" (przeszkodę) na planszy.
 */
public class Stone {
    private List<Position> positions; // Pola, które zajmuje kamień (może być wielopolowy)
    private boolean destructible;     // Czy kamień można zniszczyć?

    // Konstruktor pakietowy – wymusza tworzenie przez StoneBuilder
    Stone(List<Position> positions, boolean destructible) {
        this.positions = positions;
        this.destructible = destructible;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public boolean isDestructible() {
        return destructible;
    }
}
