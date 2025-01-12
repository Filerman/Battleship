package model;

import java.util.ArrayList;
import java.util.List;

public class Ship implements IShip {
    private List<Position> positions;
    private boolean sunk;

    public Ship() {
        this.positions = new ArrayList<>();
        this.sunk = false;
    }

    @Override
    public void addPosition(Position position) {
        positions.add(position);
    }

    @Override
    public List<Position> getPositions() {
        return positions;
    }

    @Override
    public boolean isHit(Position shot) {
        for (Position position : positions) {
            if (position.equals(shot)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkIfSunk(List<Position> hits) {
        for (Position pos : positions) {
            if (!hits.contains(pos)) {
                this.sunk = false;
                return;
            }
        }
        this.sunk = true;
    }

    @Override
    public boolean isSunk() {
        return sunk;
    }
}
