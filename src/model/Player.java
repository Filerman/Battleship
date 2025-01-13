package model;

import java.util.Stack;

public class Player {
    private String name;
    private Board board;  // plansza gracza
    private DifficultyLevel difficultyLevel;
    private Stack<Command> commandHistory;

    public Player(String name,
                  DifficultyLevel difficultyLevel,
                  int boardSize,
                  char waterChar,
                  char shipChar,
                  char hitChar,
                  char missChar) {
        this.name = name;
        this.difficultyLevel = difficultyLevel;
        // Domyślna inicjalizacja planszy – może być później nadpisana za pomocą setBoard()
        this.board = new Board(boardSize, waterChar, shipChar, hitChar, missChar);
        this.commandHistory = new Stack<>();
    }

    // Setter do planszy
    public void setBoard(Board board) {
        this.board = board;
    }

    public String getName() {
        return name;
    }

    public Board getBoard() {
        return board;
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
