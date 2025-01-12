package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder do tworzenia obiektów Stone.
 */
public class StoneBuilder {
    private List<Position> positions = new ArrayList<>();
    private boolean destructible = false; // domyślnie kamień jest niezniszczalny

    // Metoda statyczna inicjująca buildera
    public static StoneBuilder builder() {
        return new StoneBuilder();
    }

    /**
     * Dodaje pozycję, którą kamień zajmuje.
     */
    public StoneBuilder addPosition(int row, int col) {
        positions.add(new Position(row, col));
        return this;
    }

    /**
     * Ustawia, czy kamień jest podatny na zniszczenie.
     */
    public StoneBuilder setDestructible(boolean destructible) {
        this.destructible = destructible;
        return this;
    }

    /**
     * Buduje finalny obiekt Stone.
     */
    public Stone build() {
        return new Stone(positions, destructible);
    }
}
