package com.example.entity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.utils.Point;

import javafx.scene.image.Image;

public class Warrior extends AnimatedEntity {
    private static final Image SPRITE_SHEET_WALK = new Image(
            Objects.requireNonNull(Warrior.class.getResourceAsStream("/com/example/assets/enemies/Warrior_Blue.png"))
    );
    private static final Image SPRITE_SHEET_ATTACK = new Image(
            Objects.requireNonNull(Warrior.class.getResourceAsStream("/com/example/assets/enemies/Warrior_Hit.png"))
    );

    private static final int FRAMES_WALK = 6;
    private static final int FRAMES_ATTACK = 6; // Assuming 6 frames for attack animation from the image
    private static final int FRAME_SIZE = 192;
    private static final double FRAME_SECONDS_WALK = 0.1;
    private static final double FRAME_SECONDS_ATTACK = 0.1; // Adjust as needed for attack animation speed
    private static final double SCALE_FACTOR = 0.5;

    public Warrior(List<Point> path,
                  double speed,
                  int hp)
    {
        super(
                Map.of(
                        AnimationState.WALKING, SPRITE_SHEET_WALK,
                        AnimationState.ATTACKING, SPRITE_SHEET_ATTACK
                ),
                Map.of(
                        AnimationState.WALKING, FRAMES_WALK,
                        AnimationState.ATTACKING, FRAMES_ATTACK
                ),
                FRAME_SIZE,
                Map.of(
                        AnimationState.WALKING, FRAME_SECONDS_WALK,
                        AnimationState.ATTACKING, FRAME_SECONDS_ATTACK
                ),
                path, speed, hp, SCALE_FACTOR
        );
    }
}
