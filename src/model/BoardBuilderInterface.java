package model;

import java.util.List;

public interface BoardBuilderInterface {
    BoardBuilderInterface reset();
    BoardBuilderInterface setSize(int size);
    BoardBuilderInterface setWaterChar(char waterChar);
    BoardBuilderInterface setShipChar(char shipChar);
    BoardBuilderInterface setHitChar(char hitChar);
    BoardBuilderInterface setMissChar(char missChar);
    // Dodanie kamienia – przekazujemy listę pozycji, które kamień zajmuje
    BoardBuilderInterface addStone(List<Position> positions);
    Board build();
}
