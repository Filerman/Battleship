package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Builder dla obiektu Board.
 */
public class BoardBuilder implements BoardBuilderInterface {
    private int size = 10;
    private char waterChar = '.';
    private char shipChar = 'S';
    private char hitChar = 'X';
    private char missChar = 'O';
    private List<Stone> stones = new ArrayList<>();

    public static BoardBuilderInterface builder() {
        return new BoardBuilder();
    }

    @Override
    public BoardBuilderInterface reset() {
        this.size = 10;
        this.waterChar = '.';
        this.shipChar = 'S';
        this.hitChar = 'X';
        this.missChar = 'O';
        this.stones.clear();
        return this;
    }

    @Override
    public BoardBuilderInterface setSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public BoardBuilderInterface setWaterChar(char waterChar) {
        this.waterChar = waterChar;
        return this;
    }

    @Override
    public BoardBuilderInterface setShipChar(char shipChar) {
        this.shipChar = shipChar;
        return this;
    }

    @Override
    public BoardBuilderInterface setHitChar(char hitChar) {
        this.hitChar = hitChar;
        return this;
    }

    @Override
    public BoardBuilderInterface setMissChar(char missChar) {
        this.missChar = missChar;
        return this;
    }

    @Override
    public BoardBuilderInterface addStone(List<Position> positions) {
        // Tworzymy nowy kamień z podanych pozycji (kamienie są niezniszczalne)
        Stone stone = new Stone(positions);
        this.stones.add(stone);
        return this;
    }

    @Override
    public Board build() {
        Board board = new Board(size, waterChar, shipChar, hitChar, missChar);
        for (Stone s : stones) {
            board.placeStone(s);
        }
        return board;
    }
}
