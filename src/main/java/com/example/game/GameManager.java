package com.example.game;

import com.example.controllers.GameScreenController;
import com.example.entity.*;
import com.example.utils.PathFinder;
import com.example.utils.Point;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class GameManager {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final List<Entity> entities, delayedAdd = new LinkedList<>(), delayedRemove = new LinkedList<>();
    private final List<AnimatedEntity> enemies = new LinkedList<>();
    private List<int[]> waves = new LinkedList<>();
    private int currentWave = 0;
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

    public void setWaves(List<int[]> waves) {
        this.waves = waves != null ? waves : new LinkedList<>();
        this.currentWave = 0;
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

                // remove dead enemies
                for (AnimatedEntity enemy : new LinkedList<>(enemies)) {
                    if (enemy.getHP() <= 0) {
                        delayedRemove.add(enemy);
                        enemies.remove(enemy);
                    }
                }

                entities.addAll(delayedAdd);
                entities.removeAll(delayedRemove);

                // if wave cleared, spawn next
                if (enemies.isEmpty() && currentWave < waves.size()) {
                    spawnWave(waves.get(currentWave));
                    currentWave++;
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
        spawnGoblin(50, 100);
    }

    public void spawnGoblin(double speed, int hp) {
        int[][] grid = gameModel.getMap().getExpandedGrid();
        Point start = PathFinder.findRandomSpawnPoint(grid);
        Point goal = PathFinder.findCastlePoint(grid);
        List<Point> path = PathFinder.findPath(grid, start, goal);

        Goblin goblin = new Goblin(path, speed, hp);

        this.entities.add(goblin);
        enemies.add(goblin);
    }

    public void spawnWarrior() {
        spawnWarrior(40, 100);
    }

    public void spawnWarrior(double speed, int hp) {
        int[][] grid = gameModel.getMap().getExpandedGrid();
        Point start = PathFinder.findRandomSpawnPoint(grid);
        Point goal = PathFinder.findCastlePoint(grid);
        List<Point> path = PathFinder.findPath(grid, start, goal);

        Warrior warrior = new Warrior(path, speed, hp);

        this.entities.add(warrior);
        enemies.add(warrior);
    }

    private void spawnWave(int[] cfg) {
        int goblins = cfg.length > 0 ? cfg[0] : 0;
        int warriors = cfg.length > 1 ? cfg[1] : 0;
        for(int i=0;i<goblins;i++) spawnGoblin();
        for(int i=0;i<warriors;i++) spawnWarrior();
    }

    public void attackEntity( Tower tower, AnimatedEntity e ) {
        if (e != null)
        {
            Point pos = e.getFuturePosition();
            Projectile p = new Projectile(
                    tower,
                    tower.getX() * GameScreenController.TILE_SIZE + 32,
                    tower.getY() * GameScreenController.TILE_SIZE + 32,
                    e);
            this.delayedAdd.add(p);
        }
    }

    public AnimatedEntity nearestEnemy( Tower tower )
    {
        return enemies.stream()
                .min(Comparator.comparing(e ->
                        Math.pow(e.getX() - tower.getX(), 2) + Math.pow(e.getY() - tower.getY(), 2)))
                .orElse(null);
    }

    public void removeProjectile( Projectile p )
    {
        this.delayedRemove.add(p);
    }

    public void placeTower( Tower tower)
    {
        this.entities.add(tower);
    }

    public void removeTower( Tower tower )
    {
        this.entities.remove(tower);
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