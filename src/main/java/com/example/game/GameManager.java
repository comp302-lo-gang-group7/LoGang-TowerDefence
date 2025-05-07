package com.example.game;

import com.example.entity.Entity;
import com.example.entity.Goblin;
import com.example.entity.Warrior;
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

    // Debug flag - set to true to see path visualization
    private static final boolean DEBUG_PATH = false;

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

                // Debug: Draw path if enabled
                if (DEBUG_PATH) {
                    drawDebugPaths();
                }

                // Draw entities
                for (Entity e : entities) {
                    e.render(gc);
                }
            }
        }.start();
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
}