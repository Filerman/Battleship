package model;

public class Achievement {
    private String description;
    private boolean unlocked;
    private boolean rewardsColorChange; // nowy atrybut – czy to osiągnięcie daje nagrodę zmiany koloru

    /**
     * Konstruktor podstawowy (bez nagrody koloru).
     */
    public Achievement(String desc) {
        this.description = desc;
        this.unlocked = false;
        this.rewardsColorChange = false;
    }

    /**
     * Konstruktor z flagą, czy to osiągnięcie daje nagrodę w postaci zmiany koloru.
     */
    public Achievement(String desc, boolean rewardsColorChange) {
        this.description = desc;
        this.unlocked = false;
        this.rewardsColorChange = rewardsColorChange;
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

    public boolean rewardsColorChange() {
        return rewardsColorChange;
    }
}
