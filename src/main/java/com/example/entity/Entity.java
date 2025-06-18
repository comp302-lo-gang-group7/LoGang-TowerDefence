package com.example.entity;

import com.example.utils.HP;
import javafx.scene.canvas.GraphicsContext;

/**
 * Represents an abstract entity in the game with position and health attributes.
 */
public abstract class Entity {
    protected double x, y;
    protected int hp;

    /**
     * Gets the Y-coordinate of the entity.
     *
     * @return The Y-coordinate of the entity.
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the health points (HP) of the entity.
     *
     * @return The health points of the entity.
     */
    public int getHp() {
        return hp;
    }

    /**
     * Gets the X-coordinate of the entity.
     *
     * @return The X-coordinate of the entity.
     */
    public double getX() {
        return x;
    }

    /**
     * Constructs an Entity with the specified position and health points.
     *
     * @param x  The X-coordinate of the entity.
     * @param y  The Y-coordinate of the entity.
     * @param hp The initial health points of the entity.
     */
    public Entity(double x, double y, int hp) {
        this.x = x;
        this.y = y;
        this.hp = hp;
    }

    /**
     * Updates the state of the entity.
     *
     * @param dt The time delta since the last update.
     */
    public abstract void update(double dt);

    /**
     * Renders the entity on the provided graphics context.
     *
     * @param gc The graphics context used for rendering.
     */
    public abstract void render(GraphicsContext gc);

    /**
     * Handles click events on the entity.
     */
    public void onClick() {}

    /**
     * Gets the health points (HP) of the entity.
     *
     * @return The health points of the entity.
     */
    public int getHP() {
        return hp;
    }

    /**
     * Applies damage to the entity by reducing its health points.
     *
     * @param amount The amount of damage to apply.
     */
    public void applyDamage(int amount) {
        hp -= amount;
    }
}
