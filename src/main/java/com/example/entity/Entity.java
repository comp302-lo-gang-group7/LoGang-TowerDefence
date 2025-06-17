package com.example.entity;

import com.example.utils.HP;
import javafx.scene.canvas.GraphicsContext;


public abstract class Entity {
    protected double x, y;
    protected int hp;

    public double getY()
    {
        return y;
    }

    public int getHp()
    {
        return hp;
    }

    public double getX()
    {
        return x;
    }

    /**
     * TODO
     */
    public Entity( double x, double y, int hp) {
        this.x = x;
        this.y = y;
        this.hp = hp;
    }

    /**
     * TODO
     */
    public abstract void update(double dt);

    /**
     * TODO
     */
    public abstract void render(GraphicsContext gc);

    /**
     * TODO
     */
    public void onClick() {}

    /**
     * TODO
     */
    public int getHP() {
        return hp;
    }

    /**
     * TODO
     */
    public void applyDamage(int amount) {
        hp -= amount;
    }
}
