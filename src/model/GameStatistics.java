package model;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class GameStatistics {
    private int totalGames;
    private HashMap<String, Integer> winsPerPlayer;
    private List<GameHistoryEntry> gameHistory;

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
            sb.append(entry).append("\n");
        }
        return sb.toString();
    }

    /**
     * Zapis statystyk do pliku (np. stats.txt)
     */
    public void saveToFile(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Zapis totalGames
            writer.write(String.valueOf(totalGames));
            writer.newLine();
            // Zapis wygranych
            for (Map.Entry<String, Integer> e : winsPerPlayer.entrySet()) {
                writer.write("WIN:" + e.getKey() + ":" + e.getValue());
                writer.newLine();
            }
            // Zapis historii
            for (GameHistoryEntry gh : gameHistory) {
                writer.write("GAMEHIST:" + gh.toString());
                writer.newLine();
            }
        }
    }

    /**
     * Odczyt statystyk z pliku
     */
    public void loadFromFile(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            // Brak pliku
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            // 1. Pierwsza linia: totalGames
            String line = br.readLine();
            if (line != null) {
                try {
                    totalGames = Integer.parseInt(line.trim());
                } catch (NumberFormatException e) {
                    // w razie błędu zostaw 0
                }
            }
            // 2. Kolejne linie
            while ((line = br.readLine()) != null) {
                if (line.startsWith("WIN:")) {
                    // form. WIN:player:liczbaWygranych
                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        String playerName = parts[1];
                        int wins = Integer.parseInt(parts[2]);
                        winsPerPlayer.put(playerName, wins);
                    }
                } else if (line.startsWith("GAMEHIST:")) {
                    // Parsujemy historię
                    String histText = line.substring("GAMEHIST:".length());
                    GameHistoryEntry ghe = GameHistoryEntry.fromString(histText);
                    if (ghe != null) {
                        gameHistory.add(ghe);
                    }
                }
            }
        }
    }

    /**
     * Zwraca liczbę wygranych dla danego gracza.
     */
    public int getWinsForPlayer(String playerName) {
        return winsPerPlayer.getOrDefault(playerName, 0);
    }
}
