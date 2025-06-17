package com.example.entity;

import com.example.controllers.GameScreenController;
import com.example.game.GameManager;
import com.example.utils.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Objects;

/**
 * Class Warrior
 */
public class Warrior extends AnimatedEntity {
    private static final Image SPRITE_SHEET = new Image(
            Objects.requireNonNull(Warrior.class.getResourceAsStream("/com/example/assets/enemies/Warrior_Blue.png"))
    );

    private static final int FRAMES = 6;
    private static final int FRAME_SIZE = 192;
    private static final double FRAME_SECONDS = 0.1;
    private static final double SCALE_FACTOR = 0.5;

    private static final Image THUNDER_ICON = new Image(
            Objects.requireNonNull(Warrior.class.getResourceAsStream("/com/example/assets/effects/thunder.png"))
    );

    private static final double GOBLIN_SPEED = 50;

    private final double baseSpeed;
    private boolean speedBoost = false;

    public Warrior(List<Point> path,
                   double speed,
                   int hp)
    {
        super(SPRITE_SHEET, FRAMES, FRAME_SIZE, FRAME_SECONDS, path, speed, hp, SCALE_FACTOR);
        this.baseSpeed = speed;
    }

    @Override
    /**
     * TODO
     */
    public void update(double dt) {
        GameManager gm = GameManager.getInstance();
        Goblin nearest = gm.nearestGoblin(this);
        boolean close = false;
        if (nearest != null) {
            double dist = Math.hypot(nearest.getX() - getX(), nearest.getY() - getY());
            close = dist <= GameScreenController.TILE_SIZE;
        }

        if (close) {
            if (!speedBoost && !effectStack.contains(THUNDER_ICON)) {
                effectStack.addLast(THUNDER_ICON);
            }
            speedBoost = true;
            double boosted = (baseSpeed + GOBLIN_SPEED) / 2.0;
            super.update(dt * boosted / baseSpeed);
        } else {
            if (speedBoost) {
                effectStack.remove(THUNDER_ICON);
            }
            speedBoost = false;
            super.update(dt);
        }
    }

    @Override
    /**
     * TODO
     */
    public void render(GraphicsContext gc) {
        super.render(gc);
    }


    @Override
    /**
     * TODO
     */
    public int modifyDamage(Tower source, int base) {
        if (source instanceof ArcherTower) {
            return (int) Math.round(base * 0.5);
        }
        if (source instanceof MageTower) {
            return (int) Math.round(base * 1.5);
        }
        return base;
    }
}
