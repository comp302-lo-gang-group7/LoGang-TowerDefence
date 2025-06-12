package com.example.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Stores the player's current gold and lives during a game session.
 */
public class PlayerState {
    private final IntegerProperty goldProperty = new SimpleIntegerProperty(), livesProperty = new SimpleIntegerProperty();
    private final int maxLives;

    public PlayerState(int startingGold, int startingLives) {
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

    public void addGold(int amount) {
        goldProperty.set(getGold() + amount);
    }

    public void spendGold(int amount) {
        goldProperty.set(getGold() - amount);
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