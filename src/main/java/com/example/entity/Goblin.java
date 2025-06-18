package com.example.entity;

import com.example.utils.Point;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Objects;

/**
 * Represents a Goblin enemy in the game, which animates using a horizontal sprite sheet.
 * This entity follows a predefined path and interacts with towers in the game.
 */
public class Goblin extends AnimatedEntity {
    private static final Image SPRITE_SHEET = new Image(
            Objects.requireNonNull(Goblin.class.getResourceAsStream("/com/example/assets/enemies/Goblin_Red.png"))
    );

    private static final int FRAMES = 6;
    private static final int FRAME_SIZE = 192;
    private static final double FRAME_SECONDS = 0.1;
    private static final double SCALE_FACTOR = 0.5;

    /**
     * Constructs a Goblin entity with the specified path, speed, and health points.
     *
     * @param path  The path the Goblin will follow.
     * @param speed The movement speed of the Goblin.
     * @param hp    The health points of the Goblin.
     */
    public Goblin(List<Point> path,
                  double speed,
                  int hp)
    {
        super(SPRITE_SHEET, FRAMES, FRAME_SIZE, FRAME_SECONDS, path, speed, hp, SCALE_FACTOR);
    }

    /**
     * Modifies the damage dealt to the Goblin based on the type of tower attacking it.
     *
     * @param source The tower attacking the Goblin.
     * @param base   The base damage dealt by the tower.
     * @return The modified damage value after applying type-specific adjustments.
     */
    @Override
    public int modifyDamage(Tower source, int base) {
        if (source instanceof ArcherTower) {
            return (int) Math.round(base * 1.5);
        }
        if (source instanceof MageTower) {
            return (int) Math.round(base * 0.5);
        }
        return base;
    }
}