package com.example.game;

/**
 * Stores the player's current gold and lives during a game session.
 */
public class PlayerState {
    private int gold;
    private int lives;
    private final int maxLives;

    public PlayerState(int startingGold, int startingLives) {
        this.gold = startingGold;
        this.lives = startingLives;
        this.maxLives = startingLives;
    }

    public int getGold() {
        return gold;
    }

    public int getLives() {
        return lives;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void spendGold(int amount) {
        gold -= amount;
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }
}