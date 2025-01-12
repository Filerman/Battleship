package model;

public class Achievement {
    private String description;
    private boolean unlocked;

    public Achievement(String desc) {
        this.description = desc;
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
