package com.example.game;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.example.controllers.GameScreenController;
import com.example.entity.AnimatedEntity;
import com.example.entity.Entity;
import com.example.entity.Goblin;
import com.example.entity.Projectile;
import com.example.entity.Tower;
import com.example.entity.Warrior;
import com.example.utils.PathFinder;
import com.example.utils.Point;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class GameManager {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final List<Entity> entities, delayedAdd = new LinkedList<>(), delayedRemove = new LinkedList<>();
    private final List<AnimatedEntity> enemies = new LinkedList<>();
    private List<int[]> waves = new LinkedList<>();
    private final IntegerProperty currentWaveProperty = new SimpleIntegerProperty(0);
    private long lastTime = 0;
    private GameModel gameModel;
    private PlayerState playerState;
    private boolean paused = false;
    private double gameSpeedMultiplier = 1.0; // default speed
    private AnimationTimer gameLoop;
    private static GameManager instance;
    private int castleHP = 1000; // Initial castle HP
    private Point castlePoint; // Store castle central point

    // Debug flag - set to true to see path visualization
    private static final boolean DEBUG_PATH = false;

    public static void initialize(Canvas canvas, List<Entity> entities, GameModel model, PlayerState state) {
        if (instance != null) {
            instance.stop();
        }
        instance = new GameManager(canvas, entities, model, state);
    }

    public void setWaves(List<int[]> waves) {
        this.waves = waves != null ? waves : new LinkedList<>();
        this.currentWaveProperty.set(0);
    }

    public static GameManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GameManager has not been initialized.");
        }
        return instance;
    }

    private GameManager(Canvas canvas, List<Entity> entities, GameModel model, PlayerState state) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.entities = entities;
        this.gameModel = model;
        this.playerState = state;
        // Find castle point once at initialization
        this.castlePoint = PathFinder.findCastlePoint(gameModel.getMap().getExpandedGrid());
    }

    public void start() {
        gameLoop = new AnimationTimer() {
            private final double WARRIOR_ATTACK_RANGE = GameScreenController.TILE_SIZE * 0.75; // Example range
            private final double WARRIOR_ATTACK_COOLDOWN = 1.0; // Seconds
            private double warriorAttackTimer = 0;

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

                // handle enemy deaths or reaching the goal, and warrior attacking castle
                for (AnimatedEntity enemy : new LinkedList<>(enemies)) {
                    if (enemy.getHP() <= 0) {
                        delayedRemove.add(enemy);
                        enemies.remove(enemy);
                        playerState.addGold(10);
                    } else if (enemy.hasReachedGoal()) {
                        // Check if the entity is a Warrior and if it's close to the castle
                        if (enemy instanceof Warrior) {
                            double distanceToCastle = enemy.distanceTo(castlePoint);
                            if (distanceToCastle <= WARRIOR_ATTACK_RANGE) {
                                // Warrior is in attack range
                                ((Warrior) enemy).setAnimationState(AnimatedEntity.AnimationState.ATTACKING);
                                ((Warrior) enemy).setMoving(false);

                                warriorAttackTimer += dt;
                                if (warriorAttackTimer >= WARRIOR_ATTACK_COOLDOWN) {
                                    castleHP -= 10; // Example damage
                                    System.out.println("Castle HP: " + castleHP);
                                    warriorAttackTimer = 0;
                                    if (castleHP <= 0) {
                                        System.out.println("Game Over! Castle destroyed.");
                                        gameLoop.stop(); // Stop the game
                                        // TODO: Implement game over screen or logic
                                    }
                                }
                            } else {
                                // Warrior is not in attack range, continue walking
                                ((Warrior) enemy).setAnimationState(AnimatedEntity.AnimationState.WALKING);
                                ((Warrior) enemy).setMoving(true);
                                // Reset rotation to default for walking if needed, or handle based on pathfinding direction
                            }
                        } else {
                            // Non-warrior enemies still cause life loss when reaching goal
                            delayedRemove.add(enemy);
                            enemies.remove(enemy);
                            playerState.loseLife();
                        }
                    }
                }

                entities.addAll(delayedAdd);
                entities.removeAll(delayedRemove);

                // if wave cleared, spawn next
                if (enemies.isEmpty() && currentWaveProperty.get() < waves.size()) {
                    spawnWave(waves.get(currentWaveProperty.get()));
                    currentWaveProperty.set(currentWaveProperty.get() + 1);
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

    public void attackEntity( Tower tower, AnimatedEntity e )
    {
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

    public int getGold() {
        return playerState.getGold();
    }

    public int getLives() {
        return playerState.getLives();
    }

    public int getMaxLives() {
        return playerState.getMaxLives();
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public IntegerProperty getCurrentWaveProperty() {
        return currentWaveProperty;
    }
}