package model;

import java.util.List;

/**
 * Abstrakcyjny dekorator - implementuje interfejs IShip i przechowuje referencję
 * do dekorowanego obiektu (np. do zwykłego statku).
 */
public abstract class ShipDecorator implements IShip {
    protected IShip decoratedShip;  // Obiekt, który dekorujemy

    public ShipDecorator(IShip decoratedShip) {
        this.decoratedShip = decoratedShip;
    }

    @Override
    public void addPosition(Position position) {
        decoratedShip.addPosition(position);
    }

    @Override
    public List<Position> getPositions() {
        return decoratedShip.getPositions();
    }

    @Override
    public boolean isHit(Position shot) {
        return decoratedShip.isHit(shot);
    }

    @Override
    public void checkIfSunk(List<Position> hits) {
        decoratedShip.checkIfSunk(hits);
    }

    @Override
    public boolean isSunk() {
        return decoratedShip.isSunk();
    }
}
