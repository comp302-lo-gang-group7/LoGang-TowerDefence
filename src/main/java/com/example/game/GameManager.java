package com.example.game;

import com.example.entity.Entity;
import com.example.entity.Goblin;
import com.example.utils.PathFinder;
import com.example.utils.Point;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public class GameManager {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final List<Entity> entities;
    private long lastTime = 0;
    private GameModel gameModel;

    public GameManager(Canvas canvas, List<Entity> entities, GameModel model) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.entities = entities;
        this.gameModel = model;
    }

    public void start() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime == 0) lastTime = now;
                double dt = (now - lastTime) / 1e9;
                lastTime = now;

                // update phase
                for (Entity e : entities) {
                    e.update(dt);
                }

                // render phase
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                for (Entity e : entities) {
                    e.render(gc);
                }
            }
        }.start();
    }

    public void spawnGoblin() {
        int[][] grid = gameModel.getMap().getExpandedGrid();
        Point start = PathFinder.findSpawnPoint(grid);
        Point goal  = PathFinder.findCastlePoint(grid);
        List<Point> path = PathFinder.findPath(grid, start, goal);

        Goblin goblin = new Goblin(path, 0.1, 100);

        this.entities.add(goblin);
    }
}
