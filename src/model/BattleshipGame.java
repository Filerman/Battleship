package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Logika główna gry w statki.
 * Uwzględniamy tutaj m.in. tryb symulacji AI vs AI, osiągnięcia i odblokowywanie zawartości.
 */
public class BattleshipGame {
    private Player player1;
    private Player player2;
    private GameStatistics statistics;
    private List<Achievement> achievements;

    // Przykład: odblokowane "skórki" statków, tryby, itp.
    private boolean alternateShipSkinUnlocked;

    public BattleshipGame(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        statistics = new GameStatistics();
        achievements = new ArrayList<>();
        achievements.add(new Achievement("Pierwsze zatopienie!"));
        achievements.add(new Achievement("Zwycięstwo w 10 ruchach!"));

        alternateShipSkinUnlocked = false;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public GameStatistics getStatistics() {
        return statistics;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public boolean isAlternateShipSkinUnlocked() {
        return alternateShipSkinUnlocked;
    }

    public void setAlternateShipSkinUnlocked(boolean unlocked) {
        this.alternateShipSkinUnlocked = unlocked;
    }

    /**
     * Metoda generująca strzał AI zależnie od poziomu trudności.
     */
    public Position getAiShot(Player aiPlayer, Board enemyBoard) {
        DifficultyLevel difficulty = aiPlayer.getDifficultyLevel();
        switch (difficulty) {
            case EASY:
                return getRandomShot(enemyBoard);
            case MEDIUM:
                return getMediumShot(aiPlayer, enemyBoard);
            case HARD:
                // Można rozbudować bardziej zaawansowany algorytm
                return getMediumShot(aiPlayer, enemyBoard);
            default:
                return getRandomShot(enemyBoard);
        }
    }

    /**
     * Najprostszy losowy strzał (dla EASY).
     */
    private Position getRandomShot(Board enemyBoard) {
        Random rand = new Random();
        int size = enemyBoard.getSize();
        Position shot;
        do {
            shot = new Position(rand.nextInt(size), rand.nextInt(size));
        } while (enemyBoard.getShotsFired().contains(shot));
        return shot;
    }

    /**
     * Prosty algorytm "MEDIUM": jeśli w poprzednim ruchu trafiliśmy,
     * próbujemy strzelić w pobliżu ostatniego trafienia.
     * W innym wypadku - losowy strzał.
     */
    private Position getMediumShot(Player aiPlayer, Board enemyBoard) {
        // Znajdź ostatni trafiony punkt
        List<Position> shotsFired = enemyBoard.getShotsFired();
        if (!shotsFired.isEmpty()) {
            Position lastShot = shotsFired.get(shotsFired.size() - 1);
            // Sprawdź, czy był trafiony
            char[][] grid = enemyBoard.getGrid();
            if (grid[lastShot.getRow()][lastShot.getCol()] == enemyBoard.getHitChar()) {
                // Spróbuj strzelić obok
                List<Position> candidates = getAdjacentPositions(lastShot, enemyBoard.getSize());
                for (Position candidate : candidates) {
                    if (!shotsFired.contains(candidate)) {
                        return candidate;
                    }
                }
            }
        }
        return getRandomShot(enemyBoard);
    }

    /**
     * Zwraca listę sąsiadujących pól (góra, dół, lewo, prawo), jeśli są w planszy.
     */
    private List<Position> getAdjacentPositions(Position pos, int size) {
        List<Position> result = new ArrayList<>();
        int r = pos.getRow();
        int c = pos.getCol();
        if (r > 0)       result.add(new Position(r - 1, c));
        if (r < size-1)  result.add(new Position(r + 1, c));
        if (c > 0)       result.add(new Position(r, c - 1));
        if (c < size-1)  result.add(new Position(r, c + 1));
        return result;
    }

    public boolean undoLastMove(Player player) {
        return player.undoLastCommand();
    }
}
