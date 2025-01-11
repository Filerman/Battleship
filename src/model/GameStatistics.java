package model;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Przykładowa klasa do zarządzania statystykami gry.
 * Możesz ją rozbudować o zapis/odczyt z pliku, bazę danych itp.
 */
public class GameStatistics {
    private int totalGames;
    private HashMap<String, Integer> winsPerPlayer; // klucz: nazwa gracza, wartość: liczba wygranych
    private List<GameHistoryEntry> gameHistory;      // historia gier

    public GameStatistics() {
        totalGames = 0;
        winsPerPlayer = new HashMap<>();
        gameHistory = new ArrayList<>();
    }

    public void incrementTotalGames() {
        totalGames++;
    }

    public void addWinForPlayer(String playerName) {
        winsPerPlayer.putIfAbsent(playerName, 0);
        winsPerPlayer.put(playerName, winsPerPlayer.get(playerName) + 1);
    }

    public void addGameHistoryEntry(GameHistoryEntry entry) {
        gameHistory.add(entry);
    }

    public String getRanking() {
        StringBuilder sb = new StringBuilder();
        sb.append("Łączna liczba gier: ").append(totalGames).append("\n");
        sb.append("Ranking graczy (wygrane):\n");
        for (String player : winsPerPlayer.keySet()) {
            sb.append(player).append(" - ").append(winsPerPlayer.get(player)).append("\n");
        }
        return sb.toString();
    }

    public String getGameHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("Historia rozgrywek:\n");
        for (GameHistoryEntry entry : gameHistory) {
            sb.append(entry.toString()).append("\n");
        }
        return sb.toString();
    }
}
