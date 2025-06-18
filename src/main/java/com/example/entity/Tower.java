package com.example.entity;

import com.example.controllers.GameScreenController;
import com.example.game.GameManager;
import javafx.scene.canvas.GraphicsContext;

/**
 * Represents a generic tower entity in the game, capable of attacking enemies within range.
 */
public abstract class Tower extends Entity {
    public int baseDamage;
    public int goldCost;
    public int upgradeLevel;

    protected double timerTime = 0;
    protected double attackCooldown = 0.5;
    protected double range = 2;

    /**
     * Constructs a Tower entity.
     *
     * @param x The x-coordinate of the tower.
     * @param y The y-coordinate of the tower.
     * @param baseHp The base health points of the tower.
     * @param baseDamage The base damage dealt by the tower.
     * @param goldCost The gold cost to build the tower.
     * @param upgradeLevel The upgrade level of the tower.
     */
    public Tower(int x, int y, int baseHp, int baseDamage, int goldCost, int upgradeLevel) {
        super(x, y, baseHp);
        this.baseDamage = baseDamage;
        this.goldCost = goldCost;
        this.upgradeLevel = upgradeLevel;
    }

    /**
     * Gets the range of the tower.
     *
     * @return The range of the tower.
     */
    public double getRange() {
        return range;
    }

    /**
     * Sets the range of the tower.
     *
     * @param range The new range value.
     */
    public void setRange(int range) {
        this.range = range;
    }

    /**
     * Sets the attack cooldown of the tower.
     *
     * @param cooldown The new attack cooldown value.
     */
    public void setAttackCooldown(double cooldown) {
        this.attackCooldown = cooldown;
    }

    /**
     * Updates the tower's state, including attacking enemies within range.
     *
     * @param dt The time step for the update.
     */
    @Override
    public void update(double dt) {
        if (timerTime < attackCooldown) {
            timerTime += dt;
        } else {
            AnimatedEntity nearestEnemy = GameManager.getInstance().nearestEnemy(this);
            if (nearestEnemy != null) {
                double dx = getX() * GameScreenController.TILE_SIZE - nearestEnemy.getX();
                double dy = getY() * GameScreenController.TILE_SIZE - nearestEnemy.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= range * GameScreenController.TILE_SIZE) {
                    timerTime = 0;
                    GameManager.getInstance().attackEntity(this, nearestEnemy);
                }
            }
        }
    }

    /**
     * Renders the tower on the game screen.
     *
     * @param gc The graphics context used for rendering.
     */
    @Override
    public void render(GraphicsContext gc) {}
}
