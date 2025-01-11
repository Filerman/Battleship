package model;

import java.time.LocalDateTime;

/**
 * Reprezentuje wpis w historii gier (do statystyk).
 */
public class GameHistoryEntry {
    private LocalDateTime dateTime;
    private String gameMode;   // np. "PVP", "PVE", "AI vs AI"
    private String winnerName;
    private int roundsPlayed;

    public GameHistoryEntry(String gameMode, String winnerName, int roundsPlayed) {
        this.dateTime = LocalDateTime.now();
        this.gameMode = gameMode;
        this.winnerName = winnerName;
        this.roundsPlayed = roundsPlayed;
    }

    @Override
    public String toString() {
        return "[" + dateTime + "] Tryb: " + gameMode
                + ", Zwycięzca: " + winnerName
                + ", Liczba tur: " + roundsPlayed;
    }
}
