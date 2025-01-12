package model;

import java.util.List;

/**
 * Klasa reprezentująca "kamień" (przeszkodę) na planszy.
 * Kamienie są zawsze niezniszczalne.
 */
public class Stone {
    private List<Position> positions; // Pola, które zajmuje kamień (może być wielopolowy)
    // Usuwamy możliwość zmiany flagi – kamienie są niezniszczalne
    private final boolean destructible = false;

    // Konstruktor pakietowy – wymusza tworzenie przez StoneBuilder
    Stone(List<Position> positions) {
        this.positions = positions;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public boolean isDestructible() {
        return destructible;
    }
}
