package model;

import java.util.List;

/**
 * Dekorator, który dodaje pancerz do statku.
 * Załóżmy, że pancerz absorbuje pierwsze trafienie.
 */
public class ArmoredShipDecorator extends ShipDecorator {
    private boolean armorIntact = true; // pancerz jest początkowo nienaruszony

    public ArmoredShipDecorator(IShip decoratedShip) {
        super(decoratedShip);
    }

    @Override
    public boolean isHit(Position shot) {
        // Jeśli statek jest trafiony i pancerz jest jeszcze nienaruszony,
        // to niszczymy pancerz, ale nie przekazujemy trafienia dalej w tym momencie.
        boolean hit = super.isHit(shot);
        if (hit) {
            if (armorIntact) {
                System.out.println("Pancerz został trafiony i zniszczony!");
                armorIntact = false;
                // Pierwszy strzał trafia w pancerz, ale NIE liczymy tego jeszcze jako trafienie statku
                return false;
            } else {
                // Jeżeli pancerz jest już zniszczony, to normalnie „przepuszczamy” trafienie w statek
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkIfSunk(List<Position> hits) {
        // Po zniszczeniu pancerza zachowanie jest takie samo, jak w statku bazowym
        super.checkIfSunk(hits);
    }
}
