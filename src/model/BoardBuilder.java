package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder służący do konfigurowania obiektu Board:
 *  - rozmiar planszy
 *  - wygląd znaków (waterChar, shipChar, hitChar, missChar)
 *  - dodawanie kamieni (Stone)
 */
public class BoardBuilder {
    private int size = 10;         // domyślny rozmiar planszy
    private char waterChar = '.';  // domyślny znak wody
    private char shipChar = 'S';   // domyślny znak statku
    private char hitChar = 'X';    // domyślny znak trafienia
    private char missChar = 'O';   // domyślny znak pudła

    // Lista kamieni, które chcemy umieścić na planszy
    private List<Stone> stones = new ArrayList<>();

    // Metoda statyczna do zainicjowania buildera
    public static BoardBuilder builder() {
        return new BoardBuilder();
    }

    public BoardBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public BoardBuilder setWaterChar(char waterChar) {
        this.waterChar = waterChar;
        return this;
    }

    public BoardBuilder setShipChar(char shipChar) {
        this.shipChar = shipChar;
        return this;
    }

    public BoardBuilder setHitChar(char hitChar) {
        this.hitChar = hitChar;
        return this;
    }

    public BoardBuilder setMissChar(char missChar) {
        this.missChar = missChar;
        return this;
    }

    /**
     * Dodaje kamień, który zostanie umieszczony na planszy.
     */
    public BoardBuilder addStone(Stone stone) {
        this.stones.add(stone);
        return this;
    }

    /**
     * Buduje finalny obiekt Board i rozmieszcza w nim kamienie.
     */
    public Board build() {
        Board board = new Board(size, waterChar, shipChar, hitChar, missChar);
        for (Stone s : stones) {
            board.placeStone(s);
        }
        return board;
    }
}
