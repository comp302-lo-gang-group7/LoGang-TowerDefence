package com.example.entity;

import java.util.List;
import java.util.Objects;

import com.example.utils.Point;

import javafx.scene.image.Image;

public class Warrior extends AnimatedEntity {
    private static final Image WALK_SPRITE_SHEET;
    private static final Image ATTACK_SPRITE_SHEET;
    
    static {
        // Load images with transparency enabled
        WALK_SPRITE_SHEET = new Image(
            Objects.requireNonNull(Warrior.class.getResourceAsStream("/com/example/assets/enemies/Warrior_Blue.png"))
        );
        ATTACK_SPRITE_SHEET = new Image(
            Objects.requireNonNull(Warrior.class.getResourceAsStream("/com/example/assets/enemies/Hitting_Animation.png"))
        );
    }

    private static final int WALK_FRAMES = 6;
    private static final int ATTACK_FRAMES = 6;  // Assuming 6 frames in Hitting_Animation.png
    private static final int COMMON_FRAME_SIZE = 192; // Assuming both sprite sheets now have 192x192 frames
    private static final double FRAME_SECONDS = 0.1;  // Normal walking speed
    private static final double ATTACK_FRAME_SECONDS = 0.15;  // Slightly slower for attack animation
    private static final double COMMON_SCALE_FACTOR = 0.5; // Apply a single scale factor for both

    public Warrior(List<Point> path,
                  double speed,
                  int hp)
    {
        super(WALK_SPRITE_SHEET, ATTACK_SPRITE_SHEET, WALK_FRAMES, ATTACK_FRAMES, 
              COMMON_FRAME_SIZE, ATTACK_FRAME_SECONDS, path, speed, hp, COMMON_SCALE_FACTOR);
    }
}
