package model;

import java.util.List;

/**
 * Klasa Director odpowiedzialna za kierowanie budową obiektów Board.
 */
public class BoardDirector {
    private final BoardBuilderInterface builder;

    public BoardDirector(BoardBuilderInterface builder) {
        this.builder = builder;
    }

    public Board constructBoard(int size, List<Stone> stones, char waterChar, char shipChar, char hitChar, char missChar) {
        builder.reset()
                .setSize(size)
                .setWaterChar(waterChar)
                .setShipChar(shipChar)
                .setHitChar(hitChar)
                .setMissChar(missChar);

        if (stones != null) {
            for (Stone stone : stones) {
                builder.addStone(stone.getPositions());
            }
        }

        return builder.build();
    }


    public Board constructStandardBoard() {
        return builder.reset()
                .setSize(10)
                .setWaterChar('.')
                .setShipChar('S')
                .setHitChar('X')
                .setMissChar('O')
                .build();
    }
}
