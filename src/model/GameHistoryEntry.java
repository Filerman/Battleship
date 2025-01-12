package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameHistoryEntry {
    private LocalDateTime dateTime;
    private String gameMode;
    private String winnerName;
    private int roundsPlayed;

    public GameHistoryEntry(String gameMode, String winnerName, int roundsPlayed) {
        this.dateTime = LocalDateTime.now();
        this.gameMode = gameMode;
        this.winnerName = winnerName;
        this.roundsPlayed = roundsPlayed;
    }

    // Ewentualnie konstruktor z parametrem dateTime
    public GameHistoryEntry(LocalDateTime dateTime, String gameMode, String winnerName, int roundsPlayed) {
        this.dateTime = dateTime;
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

    /**
     * Próba sparsowania linii w formacie:
     *   [2025-01-12T21:33:15.032312200] Tryb: PVP, Zwycięzca: FajnyGracz, Liczba tur: 74
     */
    public static GameHistoryEntry fromString(String line) {
        try {
            // Szukamy fragmentu w nawiasach kwadratowych
            // np. "[2025-01-12T21:33]" ...
            int idx1 = line.indexOf('[');
            int idx2 = line.indexOf(']');
            if (idx1 == -1 || idx2 == -1) {
                return null;
            }
            String dtText = line.substring(idx1+1, idx2).trim();
            LocalDateTime dt = LocalDateTime.parse(dtText); // może rzucić wyjątek

            // Reszta: " Tryb: PVP, Zwycięzca: FajnyGracz, Liczba tur: 74"
            String rest = line.substring(idx2+1).trim();
            // np. "Tryb: PVP, Zwycięzca: FajnyGracz, Liczba tur: 74"

            // Proste parsowanie
            String mode = "???";
            String winner = "???";
            int rounds = 0;

            // "Tryb: PVP," -> we find "Tryb:"
            int trybIdx = rest.indexOf("Tryb:");
            if (trybIdx != -1) {
                int commaIdx = rest.indexOf(',', trybIdx);
                if (commaIdx == -1) commaIdx = rest.length();
                String part = rest.substring(trybIdx + 5, commaIdx).trim();
                // np. "PVP"
                mode = part;
                if (mode.startsWith(":")) {
                    mode = mode.substring(1).trim();
                }
            }

            // "Zwycięzca:"
            int zwIdx = rest.indexOf("Zwycięzca:");
            if (zwIdx != -1) {
                int commaIdx = rest.indexOf(',', zwIdx);
                if (commaIdx == -1) commaIdx = rest.length();
                String part = rest.substring(zwIdx + 10, commaIdx).trim();
                winner = part;
                if (winner.startsWith(":")) {
                    winner = winner.substring(1).trim();
                }
            }

            // "Liczba tur:"
            int ltIdx = rest.indexOf("Liczba tur:");
            if (ltIdx != -1) {
                int commaIdx = rest.indexOf(',', ltIdx);
                if (commaIdx == -1) commaIdx = rest.length();
                String part = rest.substring(ltIdx + 11, commaIdx).trim();
                rounds = Integer.parseInt(part);
            }

            return new GameHistoryEntry(dt, mode, winner, rounds);
        } catch (Exception e) {
            // Jakikolwiek błąd parsowania – zwracamy null
            return null;
        }
    }
}
