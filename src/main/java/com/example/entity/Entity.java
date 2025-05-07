package com.example.entity;

import javafx.scene.canvas.GraphicsContext;


public abstract class Entity {
    protected double x, y;
    protected int hp;

    public Entity(double x, double y, int hp) {
        this.x = x;
        this.y = y;
        this.hp = hp;
    }

    public abstract void update(double dt);

    public abstract void render(GraphicsContext gc);

    public void onClick() {}

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
}
