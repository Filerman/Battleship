package model;

import java.util.List;

public interface IShip {
    void addPosition(Position position);
    List<Position> getPositions();
    boolean isHit(Position shot);
    void checkIfSunk(List<Position> hits);
    boolean isSunk();
}
