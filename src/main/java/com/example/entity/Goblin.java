package com.example.entity;

import java.util.List;
import java.util.Objects;

import com.example.utils.Point;

import javafx.scene.image.Image;

/**
 * A goblin enemy that animates from a horizontal sprite sheet.
 */
public class Goblin extends AnimatedEntity {
    private static final Image SPRITE_SHEET = new Image(
            Objects.requireNonNull(Goblin.class.getResourceAsStream("/com/example/assets/enemies/Goblin_Red.png"))
    );

    private static final int FRAMES = 6;
    private static final int FRAME_SIZE = 192;
    private static final double FRAME_SECONDS = 0.1;
    private static final double SCALE_FACTOR = 0.5;

    public Goblin(List<Point> path,
                  double speed,
                  int hp)
    {
        super(SPRITE_SHEET, SPRITE_SHEET, FRAMES, FRAMES, 
              FRAME_SIZE, FRAME_SECONDS, path, speed, hp, SCALE_FACTOR);
    }
}