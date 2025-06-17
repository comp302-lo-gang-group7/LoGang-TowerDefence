package com.example.game;

import com.example.controllers.GameScreenController;
import com.example.entity.*;
import com.example.player.PlayerState;
import com.example.ui.AudioManager;
import com.example.ui.ImageLoader;
import com.example.utils.PathFinder;
import com.example.utils.Point;
import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Class GameManager
 */
public class GameManager {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final List<Entity> entities;
    /**
     * TODO
     */
    private final List<Entity> delayedAdd = new LinkedList<>();
    /**
     * TODO
     */
    private final List<Entity> delayedRemove = new LinkedList<>();
    /**
     * TODO
     */
    private final List<AnimatedEntity> enemies = new LinkedList<>();
    /**
     * TODO
     */
    private final IntegerProperty currentWaveProperty = new SimpleIntegerProperty(0);
    private long lastTime = 0;
    private GameModel gameModel;
    private PlayerState playerState;
    private boolean paused = false;
    private double gameSpeedMultiplier = 1.0;
    private AnimationTimer gameLoop;
    private static GameManager instance;
    /**
     * TODO
     */
    private final List<Wave> waves = new ArrayList<>();
    private int currentWaveIndex = 0;
    private double timeUntilNextWave = 0;
    private boolean waveInProgress = false;
    /**
     * TODO
     */
    private final Random rng = new Random();
    private static final int LEVEL1_ARCHER_COST = 100;


    private static final boolean DEBUG_PATH = false;

    /**
     * TODO
     */
    public static void initialize(Canvas canvas, List<Entity> entities, GameModel model, PlayerState state) {
        if (instance != null) {
            instance.stop();
        }
        instance = new GameManager(canvas, entities, model, state);
    }


    /**
     * TODO
     */
    public void setWaves(List<int[]> waveConfigs) {
        List<Wave> converted = new ArrayList<>();
        if (waveConfigs != null) {
            for (int[] cfg : waveConfigs) {
                int goblins = cfg.length > 0 ? cfg[0] : 0;
                int warriors = cfg.length > 1 ? cfg[1] : 0;
                converted.add(new Wave(new EntityGroup(goblins, warriors, 0)));
            }
        }
        setWavesFromGroups(converted);
    }


    /**
     * TODO
     */
    public void setWavesFromGroups(List<Wave> newWaves) {
        this.waves.clear();
        if (newWaves != null) this.waves.addAll(newWaves);
        this.currentWaveProperty.set(0);
        this.currentWaveIndex = 0;
        this.waveInProgress = false;
        this.timeUntilNextWave = 4;
    }

    /**
     * TODO
     */
    public static GameManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GameManager has not been initialized.");
        }
        return instance;
    }

    /**
     * TODO
     */
    private GameManager(Canvas canvas, List<Entity> entities, GameModel model, PlayerState state) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.entities = entities;
        this.gameModel = model;
        this.playerState = state;
    }

    /**
     * TODO
     */
    private void spawnGroup(EntityGroup cfg) {
        AudioManager.playSoundEffect("/com/example/assets/audio/wave-starting.mp3");
        int goblins = cfg.goblins;
        int warriors = cfg.warriors;
        for (int i = 0; i < goblins; i++) spawnGoblin();
        for (int i = 0; i < warriors; i++) spawnWarrior();
    }


    public Goblin nearestGoblin( AnimatedEntity entity )
    {
        return enemies.stream()
                .filter(e -> e instanceof Goblin)
                .map(e -> (Goblin) e)
                .min(Comparator.comparing(g ->
                        Math.pow(g.getX() - entity.getX(), 2) + Math.pow(g.getY() - entity.getY(), 2)))
                .orElse(null);
    }

    /**
     * TODO
     */
    public void start() {
        gameLoop = new AnimationTimer() {
            @Override
            /**
             * TODO
             */
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


                double dt = rawDt * gameSpeedMultiplier;


                for (Entity e : entities) {
                    e.update(dt);
                }


                for (AnimatedEntity enemy : new LinkedList<>(enemies)) {
                    if (enemy.getHP() <= 0) {
                        delayedRemove.add(enemy);
                        enemies.remove(enemy);
                        spawnGoldBag(enemy.getX(), enemy.getY());
                    } else if (enemy.hasReachedGoal()) {
                        delayedRemove.add(enemy);
                        enemies.remove(enemy);
                        playerState.loseLife();
                    }
                }

                entities.addAll(delayedAdd);
                entities.removeAll(delayedRemove);

                timeUntilNextWave -= dt;
                if (timeUntilNextWave <= 0 && currentWaveIndex < waves.size()) {
                    Wave wave = waves.get(currentWaveIndex);
                    if (!waveInProgress) {
                        currentWaveProperty.set(currentWaveIndex + 1);
                        waveInProgress = true;

                        EntityGroup grp = wave.group;
                        spawnGroup(grp);
                        timeUntilNextWave = grp.delayAfter;
                    }
                }

                if (waveInProgress && enemies.isEmpty()) {
                    currentWaveIndex++;
                    waveInProgress = false;
                    timeUntilNextWave = 5;
                }


                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                if (DEBUG_PATH) drawDebugPaths();
                for (Entity e : entities) e.render(gc);
            }
        };
        gameLoop.start();
    }

    /**
     * TODO
     */
    public boolean isLevelCompleted() {
        return currentWaveIndex >= waves.size() && enemies.isEmpty() && !waveInProgress;
    }

    /**
     * TODO
     */
    public void pause() {
        paused = true;
    }

    /**
     * TODO
     */
    public void resume() {
        paused = false;
    }


    /**
     * TODO
     */
    public List<AnimatedEntity> enemiesWithinRadius(double x, double y, double radius) {
        List<AnimatedEntity> hits = new LinkedList<>();
        for (AnimatedEntity e : enemies) {
            double dx = e.getX() - x;
            double dy = e.getY() - y;
            if (Math.hypot(dx, dy) <= radius) {
                hits.add(e);
            }
        }
        return hits;
    }


    /**
     * TODO
     */
    public void setGameSpeed(double multiplier) {
        if (multiplier > 0) {
            this.gameSpeedMultiplier = multiplier;
        }
    }


    /**
     * TODO
     */
    private void drawDebugPaths() {

        int[][] grid = gameModel.getMap().getExpandedGrid();
        Point start = PathFinder.findRandomSpawnPoint(grid);
        Point goal = PathFinder.findCastlePoint(grid);
        List<Point> path = PathFinder.findPath(grid, start, goal);


        gc.setStroke(javafx.scene.paint.Color.YELLOW);
        gc.setLineWidth(2);
        for (int i = 0; i < path.size() - 1; i++) {
            Point current = path.get(i);
            Point next = path.get(i + 1);
            gc.strokeLine(current.x(), current.y(), next.x(), next.y());
        }


        gc.setFill(javafx.scene.paint.Color.RED);
        for (Point p : path) {
            gc.fillOval(p.x() - 3, p.y() - 3, 6, 6);
        }
    }

    /**
     * TODO
     */
    public void spawnGoblin() {
        spawnGoblin(50, 100);
    }

    /**
     * TODO
     */
    public void spawnGoblin(double speed, int hp) {
        int[][] grid = gameModel.getMap().getExpandedGrid();
        Point start = PathFinder.findRandomSpawnPoint(grid);
        Point goal = PathFinder.findCastlePoint(grid);
        List<Point> path = PathFinder.findPath(grid, start, goal);

        Goblin goblin = new Goblin(path, speed, hp);

        this.entities.add(goblin);
        enemies.add(goblin);
    }

    /**
     * TODO
     */
    public void spawnWarrior() {
        spawnWarrior(40, 100);
    }

    /**
     * TODO
     */
    public void spawnWarrior(double speed, int hp) {
        int[][] grid = gameModel.getMap().getExpandedGrid();
        Point start = PathFinder.findRandomSpawnPoint(grid);
        Point goal = PathFinder.findCastlePoint(grid);
        List<Point> path = PathFinder.findPath(grid, start, goal);

        Warrior warrior = new Warrior(path, speed, hp);

        this.entities.add(warrior);
        enemies.add(warrior);
    }

    /**
     * TODO
     */
    private void spawnWave(int[] cfg) {
        int goblins = cfg.length > 0 ? cfg[0] : 0;
        int warriors = cfg.length > 1 ? cfg[1] : 0;
        for(int i=0;i<goblins;i++) spawnGoblin();
        for(int i=0;i<warriors;i++) spawnWarrior();
    }

    /**
     * TODO
     */
    public void attackEntity( Tower tower, AnimatedEntity e ) {
        if (e != null)
        {
            if (tower instanceof ArcherTower) {
                AudioManager.playRandomSoundEffect(
                        "/com/example/assets/audio/archer-tower1.mp3",
                        "/com/example/assets/audio/archer-tower2.mp3",
                        "/com/example/assets/audio/archer-tower3.mp3",
                        "/com/example/assets/audio/archer-tower4.mp3");
            } else if (tower instanceof MageTower) {
                AudioManager.playSoundEffect(
                        "/com/example/assets/audio/spell-tower.mp3");
            } else if (tower instanceof ArtilleryTower) {
                AudioManager.playSoundEffect(
                        "/com/example/assets/audio/artillery-tower.mp3");
            }
            Projectile p = new Projectile(
                    tower,
                    tower.getX() * GameScreenController.TILE_SIZE + 32,
                    tower.getY() * GameScreenController.TILE_SIZE + 32,
                    e);
            this.delayedAdd.add(p);
        }
    }

    /**
     * TODO
     */
    private void spawnGoldBag(double x, double y) {
        int amount = 2 + rng.nextInt(LEVEL1_ARCHER_COST / 2 - 1);
        GoldBag bag = new GoldBag(x, y, amount);
        this.delayedAdd.add(bag);
    }

    /**
     * TODO
     */
    public boolean handleClick(double x, double y) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            Entity e = entities.get(i);
            if (e instanceof GoldBag bag && bag.contains(x, y)) {
                bag.onClick();
                return true;
            }
        }
        return false;
    }

	public void spawnEffect( Tower parent, double x, double y )
	{
		if ( parent != null )
		{
			Image spriteSheet;
            double scaleFactor = 0.75, frameDuration;
            int frameSize, frameCount;
			switch ( parent )
			{
				case ArcherTower _:
					spriteSheet = ImageLoader.getImage("/com/example/assets/effects/Explosions2.png");
                    scaleFactor = 0.25;
                    frameDuration = 0.05;
                    frameSize = 192;
                    frameCount = 6;
					break;
				case MageTower _:
					spriteSheet = ImageLoader.getImage("/com/example/assets/effects/Fire.png");
                    frameSize = 128;
                    scaleFactor = 0.5;
                    frameDuration = 0.2;
                    frameCount = 7;
					break;
				case ArtilleryTower _:
					spriteSheet = ImageLoader.getImage("/com/example/assets/effects/Explosions2.png");
                    frameSize = 192;
                    frameCount = 6;
                    frameDuration = 0.4;
					break;
				default:
					throw new IllegalArgumentException();
			}
			Effect e = new Effect(spriteSheet, frameCount, frameSize, frameDuration, scaleFactor, x, y);
			this.delayedAdd.add(e);
		}
	}

    /**
     * TODO
     */
    public AnimatedEntity nearestEnemy(Tower tower) {
        double range = tower.getRange() * GameScreenController.TILE_SIZE;
        double towerCenterX = (tower.getX() + 0.5) * GameScreenController.TILE_SIZE;
        double towerCenterY = (tower.getY() + 0.5) * GameScreenController.TILE_SIZE;

        return enemies.stream()
                .filter(e -> {
                    double dx = towerCenterX - e.getX();
                    double dy = towerCenterY - e.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    return distance <= range;
                })
                .max(Comparator.comparingDouble(AnimatedEntity::getPathProgress))
                .orElse(null);
    }


    public void removeEntity( Entity p )
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

    /**
     * TODO
     */
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    /**
     * TODO
     */
    public double getGameSpeedMultiplier() {
        return gameSpeedMultiplier;
    }

    /**
     * TODO
     */
    public int getGold() {
        return playerState.getGold();
    }

    /**
     * TODO
     */
    public int getLives() {
        return playerState.getLives();
    }

    /**
     * TODO
     */
    public int getMaxLives() {
        return playerState.getMaxLives();
    }

    /**
     * TODO
     */
    public PlayerState getPlayerState() {
        return playerState;
    }

    /**
     * TODO
     */
    public IntegerProperty getCurrentWaveProperty() {
        return currentWaveProperty;
    }
}