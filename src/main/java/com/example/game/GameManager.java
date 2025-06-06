package com.example.game;

import java.util.List;

import com.example.entity.Entity;
import com.example.entity.Goblin;
import com.example.entity.Warrior;
import com.example.utils.PathFinder;
import com.example.utils.Point;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class GameManager {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final List<Entity> entities;
    private long lastTime = 0;
    private GameModel gameModel;
    private boolean paused = false;
    private double gameSpeedMultiplier = 1.0; // default speed
    private AnimationTimer gameLoop;
    private static GameManager instance;

    // Debug flag - set to true to see path visualization
    private static final boolean DEBUG_PATH = false;

    public static void initialize(Canvas canvas, List<Entity> entities, GameModel model) {
        if (instance != null) {
            instance.stop();
        }
        instance = new GameManager(canvas, entities, model);
    }

    public static GameManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GameManager has not been initialized.");
        }
        return instance;
    }

    private GameManager(Canvas canvas, List<Entity> entities, GameModel model) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.entities = entities;
        this.gameModel = model;
    }

    public void start() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double rawDt = (now - lastTime) / 1e9;
                lastTime = now;

                if (paused) {
                    lastTime = now;
                    return;
                }

                // apply your multiplier here once
                double dt = rawDt * gameSpeedMultiplier;

                // pass the _scaled_ dt to every entity
                for (Entity e : entities) {
                    e.update(dt);
                }

                // render as before…
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                if (DEBUG_PATH) drawDebugPaths();
                for (Entity e : entities) e.render(gc);
            }
        };
        gameLoop.start();
    }


    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void setGameSpeed(double multiplier) {
        if (multiplier > 0) {
            this.gameSpeedMultiplier = multiplier;
        }
    }


    private void drawDebugPaths() {
        // Draw paths for debugging
        int[][] grid = gameModel.getMap().getExpandedGrid();
        Point start = PathFinder.findRandomSpawnPoint(grid);
        Point goal = PathFinder.findCastlePoint(grid);
        List<Point> path = PathFinder.findPath(grid, start, goal);

        // Draw path
        gc.setStroke(javafx.scene.paint.Color.YELLOW);
        gc.setLineWidth(2);
        for (int i = 0; i < path.size() - 1; i++) {
            Point current = path.get(i);
            Point next = path.get(i + 1);
            gc.strokeLine(current.x(), current.y(), next.x(), next.y());
        }

        // Draw waypoints
        gc.setFill(javafx.scene.paint.Color.RED);
        for (Point p : path) {
            gc.fillOval(p.x() - 3, p.y() - 3, 6, 6);
        }
    }

    public void spawnGoblin() {
        int[][] grid = gameModel.getMap().getExpandedGrid();
        Point start = PathFinder.findRandomSpawnPoint(grid);
        Point goal = PathFinder.findCastlePoint(grid);
        List<Point> path = PathFinder.findPath(grid, start, goal);

        // Higher speed value for better visibility of movement
        Goblin goblin = new Goblin(path, 50, 100);

        this.entities.add(goblin);
    }

    public void spawnWarrior() {
        int[][] grid = gameModel.getMap().getExpandedGrid();
        Point start = PathFinder.findRandomSpawnPoint(grid);
        Point goal = PathFinder.findCastlePoint(grid);
        List<Point> path = PathFinder.findPath(grid, start, goal);

        // Higher speed value for better visibility of movement
        Warrior warrior = new Warrior(path, 40, 100);

        this.entities.add(warrior);
    }

    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public double getGameSpeedMultiplier() {
        return gameSpeedMultiplier;
    }
}