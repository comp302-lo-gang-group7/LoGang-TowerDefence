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
            Objects.requireNonNull(Warrior.class.getResourceAsStream("/com/example/assets/enemies/Hit_Animation.png"))
        );
    }

    private static final int WALK_FRAMES = 6;
    private static final int ATTACK_FRAMES = 6;  // 6 frames: standing + 4 attack + back to standing
    private static final int FRAME_SIZE = 192;
    private static final double FRAME_SECONDS = 0.1;  // Normal walking speed
    private static final double ATTACK_FRAME_SECONDS = 0.15;  // Slightly slower for attack animation
    private static final double WALK_SCALE_FACTOR = 0.5;
    private static final double ATTACK_SCALE_FACTOR = 0.5;  // Match the walk animation size

    public Warrior(List<Point> path,
                  double speed,
                  int hp)
    {
        super(WALK_SPRITE_SHEET, ATTACK_SPRITE_SHEET, WALK_FRAMES, ATTACK_FRAMES, 
              FRAME_SIZE, ATTACK_FRAME_SECONDS, path, speed, hp, WALK_SCALE_FACTOR, ATTACK_SCALE_FACTOR);
    }
}
