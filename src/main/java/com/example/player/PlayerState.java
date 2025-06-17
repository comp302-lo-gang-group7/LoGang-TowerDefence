package com.example.player;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


/**
 * Class PlayerState
 */
public class PlayerState {
    /**
     * TODO
     */
    private final IntegerProperty goldProperty = new SimpleIntegerProperty(), livesProperty = new SimpleIntegerProperty();
    private final int maxLives;
    private final int initialGold;
    private int goldEarned = 0;
    private int goldSpent = 0;

    /**
     * TODO
     */
    public PlayerState(int startingGold, int startingLives) {
        this.initialGold = startingGold;
        this.goldProperty.set(startingGold);
        this.livesProperty.set(startingLives);
        this.maxLives = startingLives;
    }

    /**
     * TODO
     */
    public int getGold() {
        return goldProperty.get();
    }

    /**
     * TODO
     */
    public int getLives() {
        return livesProperty.get();
    }

    /**
     * TODO
     */
    public int getMaxLives() {
        return maxLives;
    }

    /**
     * TODO
     */
    public int getInitialGold() {
        return initialGold;
    }

    /**
     * TODO
     */
    public int getGoldEarned() {
        return goldEarned;
    }

    /**
     * TODO
     */
    public int getGoldSpent() {
        return goldSpent;
    }

    /**
     * TODO
     */
    public void addGold(int amount) {
        goldProperty.set(getGold() + amount);
        goldEarned += amount;
    }

    /**
     * TODO
     */
    public void spendGold(int amount) {
        goldProperty.set(getGold() - amount);
        goldSpent += amount;
    }

    /**
     * TODO
     */
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