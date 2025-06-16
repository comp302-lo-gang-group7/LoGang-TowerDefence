package com.example.player;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Stores the player's current gold and lives during a game session.
 */
public class PlayerState {
    private final IntegerProperty goldProperty = new SimpleIntegerProperty(), livesProperty = new SimpleIntegerProperty();
    private final int maxLives;
    private final int initialGold;
    private int goldEarned = 0;
    private int goldSpent = 0;

    public PlayerState(int startingGold, int startingLives) {
        this.initialGold = startingGold;
        this.goldProperty.set(startingGold);
        this.livesProperty.set(startingLives);
        this.maxLives = startingLives;
    }

    public int getGold() {
        return goldProperty.get();
    }

    public int getLives() {
        return livesProperty.get();
    }

    public int getMaxLives() {
        return maxLives;
    }

    public int getInitialGold() {
        return initialGold;
    }

    public int getGoldEarned() {
        return goldEarned;
    }

    public int getGoldSpent() {
        return goldSpent;
    }

    public void addGold(int amount) {
        goldProperty.set(getGold() + amount);
        goldEarned += amount;
    }

    public void spendGold(int amount) {
        goldProperty.set(getGold() - amount);
        goldSpent += amount;
    }

    public void loseLife() {
        if (getLives() > 0) {
            livesProperty.set(getLives() - 1);
        }
    }

    public IntegerProperty getGoldProperty()
    {
        return goldProperty;
    }

    public IntegerProperty getLivesProperty()
    {
        return livesProperty;
    }
}