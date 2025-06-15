package com.example.entity;

import com.example.utils.Point;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Objects;

public class Warrior extends AnimatedEntity {
    private static final Image SPRITE_SHEET = new Image(
            Objects.requireNonNull(Warrior.class.getResourceAsStream("/com/example/assets/enemies/Warrior_Blue.png"))
    );

    private static final int FRAMES = 6;
    private static final int FRAME_SIZE = 192;
    private static final double FRAME_SECONDS = 0.1;
    private static final double SCALE_FACTOR = 0.5;

    public Warrior(List<Point> path,
                  double speed,
                  int hp)
    {
        super(SPRITE_SHEET, FRAMES, FRAME_SIZE, FRAME_SECONDS, path, speed, hp, SCALE_FACTOR);
    }

    @Override
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
