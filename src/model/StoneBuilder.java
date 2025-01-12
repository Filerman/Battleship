package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder do tworzenia obiektów Stone.
 * Kamienie są zawsze niezniszczalne.
 */
public class StoneBuilder {
    private List<Position> positions = new ArrayList<>();
    // Usuwamy flagę destructible – domyślnie kamień będzie niezniszczalny

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
     * Buduje finalny obiekt Stone.
     */
    public Stone build() {
        // Kamienie zawsze są niezniszczalne – flaga nie jest ustawiana
        return new Stone(positions);
    }
}
