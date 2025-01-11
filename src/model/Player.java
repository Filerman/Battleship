package model;

public class Player {
    private String name;
    private Board board;  // plansza gracza
    private boolean isAI; // czy to komputer
    private DifficultyLevel difficultyLevel; // poziom trudności AI

    public Player(String name, boolean isAI, DifficultyLevel difficultyLevel,
                  int boardSize, char waterChar, char shipChar, char hitChar, char missChar) {
        this.name = name;
        this.isAI = isAI;
        this.difficultyLevel = difficultyLevel;
        this.board = new Board(boardSize, waterChar, shipChar, hitChar, missChar);
    }

    public String getName() {
        return name;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isAI() {
        return isAI;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }
}
