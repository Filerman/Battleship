package model;

public class AIPlayer extends Player {
    private AIStrategy strategy; // Strategia AI

    public AIPlayer(String name,
                    DifficultyLevel difficultyLevel,
                    int boardSize,
                    char waterChar,
                    char shipChar,
                    char hitChar,
                    char missChar,
                    AIStrategy strategy) {
        // Wywołanie konstruktora bazowego Player (bez isAI)
        super(name, difficultyLevel, boardSize, waterChar, shipChar, hitChar, missChar);
        this.strategy = strategy;
    }

    // Ustawienie nowej strategii w trakcie gry
    public void setStrategy(AIStrategy strategy) {
        this.strategy = strategy;
    }

    // Generowanie ruchu w oparciu o strategię (np. do strzału)
    public MoveCommand makeMove() {
        return strategy.executeStrategy(this.getBoard());
    }
}
