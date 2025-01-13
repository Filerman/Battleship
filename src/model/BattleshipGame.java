package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Logika główna gry w statki.
 * Uwzględniamy tutaj m.in. tryb symulacji AI vs AI, osiągnięcia i odblokowywanie zawartości.
 */
public class BattleshipGame {
    private static BattleshipGame instance;  // Singleton

    private Player player1;
    private Player player2;
    private GameStatistics statistics;
    private List<Achievement> achievements;

    // Przykład: odblokowane "skórki" statków, tryby, itp.
    private boolean alternateShipSkinUnlocked;

    /**
     * Prywatny konstruktor – wzorzec Singleton.
     */
    private BattleshipGame(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        statistics = new GameStatistics();
        achievements = new ArrayList<>();

        // Dodajemy kilka przykładowych osiągnięć:
        achievements.add(new Achievement("Pierwsze zatopienie!"));
        achievements.add(new Achievement("Zwycięstwo w 10 ruchach!"));
        achievements.add(new Achievement("3 wins - color change", true)); // to daje nagrodę (zmiana koloru)

        alternateShipSkinUnlocked = false;
    }

    /**
     * Uzyskanie instancji Singleton (lub stworzenie, jeśli nie istnieje).
     */
    public static BattleshipGame getInstance(Player p1, Player p2) {
        if (instance == null) {
            instance = new BattleshipGame(p1, p2);
        }
        return instance;
    }

    /**
     * Reset instancji – pozwala utworzyć nową grę "od zera" w kolejnej rozgrywce.
     */
    public static void resetInstance() {
        instance = null;
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

}
