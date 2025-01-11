package model;

/**
 * Przykładowa klasa do zarządzania osiągnięciami.
 * Możesz zdefiniować własne progi, nazwy osiągnięć, itp.
 */
public class Achievement {
    private String description;
    private boolean unlocked;

    public Achievement(String description) {
        this.description = description;
        this.unlocked = false;
    }

    public void unlock() {
        this.unlocked = true;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public String getDescription() {
        return description;
    }
}
