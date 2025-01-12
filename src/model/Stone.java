package model;

import java.util.List;

public class Stone {
    private List<Position> positions;
    private final boolean destructible = false; // Kamienie są zawsze niezniszczalne

    public Stone(List<Position> positions) {
        this.positions = positions;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public boolean isDestructible() {
        return destructible;
    }
}
