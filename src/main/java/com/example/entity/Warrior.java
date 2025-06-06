package com.example.entity;

import java.util.List;
import java.util.Objects;

import com.example.utils.Point;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Warrior extends AnimatedEntity {
    private static final Image WALK_SPRITE_SHEET = new Image(
            Objects.requireNonNull(Warrior.class.getResourceAsStream("/com/example/assets/enemies/Warrior_Blue.png"))
    );
    
    private static final Image ATTACK_SPRITE_SHEET = new Image(
            Objects.requireNonNull(Warrior.class.getResourceAsStream("/com/example/assets/enemies/Warrior_Blue_Attack.png"))
    );

    // Animation states
    private static final int WALK_FRAMES = 6;
    private static final int ATTACK_FRAMES = 2; // Two frames: sword up and down
    private static final int FRAME_SIZE = 192;
    private static final double WALK_FRAME_DURATION = 0.1;
    private static final double ATTACK_FRAME_DURATION = 0.3; // Slower attack animation
    private static final double SCALE_FACTOR = 0.5;
    private static final double ATTACK_RANGE = 50.0; // Distance at which warrior starts attacking castle
    private static final int ATTACK_DAMAGE = 5;
    private static final double VERTICAL_OFFSET = -32; // Offset to center the sprite vertically

    private boolean isAttacking = false;
    private double attackTimer = 0;
    private int attackFrame = 0;
    private double attackCooldown = 0;
    private static final double ATTACK_COOLDOWN_DURATION = 1.0; // 1 second between attacks

    public Warrior(List<Point> path, double speed, int hp) {
        super(WALK_SPRITE_SHEET, WALK_FRAMES, FRAME_SIZE, WALK_FRAME_DURATION, path, speed, hp, SCALE_FACTOR);
    }

    @Override
    public void update(double dt) {
        if (isAttacking) {
            // Handle attack animation
            attackTimer += dt;
            if (attackTimer >= ATTACK_FRAME_DURATION) {
                attackTimer = 0;
                attackFrame = (attackFrame + 1) % ATTACK_FRAMES;
                
                // Deal damage when sword swings down (on second frame)
                if (attackFrame == 1 && attackCooldown <= 0) {
                    // Deal damage to castle
                    // TODO: Add castle HP system and deal damage here
                    attackCooldown = ATTACK_COOLDOWN_DURATION;
                }
            }

            // Handle attack cooldown
            if (attackCooldown > 0) {
                attackCooldown -= dt;
            }
        } else {
            // Check if we've reached the castle (end of path)
            if (waypointIndex >= path.size() - 1) {
                isAttacking = true;
                attackFrame = 0;
                attackTimer = 0;
            } else {
                // Normal walking behavior
                super.update(dt);
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        double drawX = x - (FRAME_SIZE * SCALE_FACTOR) / 2;
        double drawY = y - (FRAME_SIZE * SCALE_FACTOR) / 2 + VERTICAL_OFFSET;

        if (isAttacking) {
            // Render attack animation
            int sx = attackFrame * FRAME_SIZE;
            int sy = 0;
            gc.drawImage(
                ATTACK_SPRITE_SHEET,
                sx, sy, FRAME_SIZE, FRAME_SIZE,
                drawX, drawY,
                FRAME_SIZE * SCALE_FACTOR, FRAME_SIZE * SCALE_FACTOR
            );
        } else {
            // Render walking animation
            int sx = currentFrame * FRAME_SIZE;
            int sy = 0;
            gc.drawImage(
                WALK_SPRITE_SHEET,
                sx, sy, FRAME_SIZE, FRAME_SIZE,
                drawX, drawY,
                FRAME_SIZE * SCALE_FACTOR, FRAME_SIZE * SCALE_FACTOR
            );
        }

        // Draw health bar above the sprite
        double barWidth = FRAME_SIZE * SCALE_FACTOR * 0.3;
        double barHeight = 3;
        double barX = x - barWidth / 2;
        double barY = y - (FRAME_SIZE * SCALE_FACTOR) / 2 + VERTICAL_OFFSET - 5; // Position above the sprite

        double healthRatio = Math.max(0, Math.min(1, hp / 100.0));
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(barX, barY, barWidth, barHeight);
        gc.setFill(javafx.scene.paint.Color.GREEN);
        gc.fillRect(barX, barY, barWidth * healthRatio, barHeight);
    }

    private Image scaleImage(Image src, double targetWidth, double targetHeight) {
        javafx.scene.canvas.Canvas tempCanvas = new javafx.scene.canvas.Canvas(targetWidth, targetHeight);
        GraphicsContext gc = tempCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, targetWidth, targetHeight);
        gc.drawImage(src, 0, 0, targetWidth, targetHeight);

        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);

        return tempCanvas.snapshot(params, null);
    }
}
