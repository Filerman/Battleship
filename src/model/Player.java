package model;

import java.util.Stack;

public class Player {
    private String name;
    private Board board;  // plansza gracza
    private boolean isAI; // czy to komputer
    private DifficultyLevel difficultyLevel;
    private Stack<Command> commandHistory;

    public Player(String name, boolean isAI, DifficultyLevel difficultyLevel,
                  int boardSize, char waterChar, char shipChar, char hitChar, char missChar) {
        this.name = name;
        this.isAI = isAI;
        this.difficultyLevel = difficultyLevel;
        // Domyślna inicjalizacja planszy – może być później nadpisana za pomocą setBoard()
        this.board = new Board(boardSize, waterChar, shipChar, hitChar, missChar);
        this.commandHistory = new Stack<>();
    }

    // Dodajemy setter do planszy, aby można było zmienić planszę po utworzeniu obiektu Player.
    public void setBoard(Board board) {
        this.board = board;
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

    public void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
    }

    public boolean undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            lastCommand.undo();
            return true;
        }
        return false;
    }
}
