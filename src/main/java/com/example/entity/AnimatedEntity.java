package com.example.entity;

import java.util.List;

import com.example.utils.Point;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class AnimatedEntity extends Entity {
    private final Image[] walkFrames;
    private final Image[] attackFrames;
    private final double frameDuration;
    private double frameTimer = 0;
    private int currentFrame = 0;
    private boolean isFirstAttackFrame = true;

    // path + movement
    private final List<Point> path;
    /**
     * Movement speed in pixels per second. The value itself remains constant
     * for an entity but the actual distance covered each frame is scaled by the
     * {@code dt} provided from {@link com.example.game.GameManager} which
     * already applies any global game speed multiplier.
     */
    private final double speed;
    private int waypointIndex = 0;  // Start at first waypoint
    
    // Attack animation state
    private boolean isAttacking = false;
    private static final double ATTACK_RANGE = 50.0; // Distance to start attacking
    private static final double ATTACK_DURATION = 0.8; // Time for one attack sequence
    private double attackTimer = 0;

    public AnimatedEntity(Image walkSpriteSheet,
                         Image attackSpriteSheet,
                         int walkFrameCount,
                         int attackFrameCount,
                         int frameSize,
                         double frameDuration,
                         List<Point> path,
                         double speed,
                         int hp,
                         double walkScaleFactor,
                         double attackScaleFactor)
    {
        // Start at the first path point
        super(path.get(0).x(), path.get(0).y(), hp);
        this.path = path;
        this.speed = speed;
        this.frameDuration = frameDuration;
        
        // Initialize walking frames
        this.walkFrames = new Image[walkFrameCount];
        for (int i = 0; i < walkFrameCount; i++) {
            Image raw = new WritableImage(
                    walkSpriteSheet.getPixelReader(),
                    i * frameSize, 0,
                    frameSize, frameSize
            );
            walkFrames[i] = scaleImage(raw, frameSize * walkScaleFactor, frameSize * walkScaleFactor);
        }

        // Initialize attack frames
        this.attackFrames = new Image[attackFrameCount];
        for (int i = 0; i < attackFrameCount; i++) {
            Image raw = new WritableImage(
                    attackSpriteSheet.getPixelReader(),
                    i * frameSize, 0,
                    frameSize, frameSize
            );
            attackFrames[i] = scaleImage(raw, frameSize * attackScaleFactor, frameSize * attackScaleFactor);
        }
    }

    @Override
    public void update(double dt) {
        // First check distance to castle
        Point castle = path.get(path.size() - 1); // Castle position
        double dx = castle.x() - x;
        double dy = castle.y() - y;
        double distToCastle = Math.hypot(dx, dy);

        // If we're in attack range of the castle, stop and attack
        if (distToCastle <= ATTACK_RANGE) {
            if (!isAttacking) {
                isAttacking = true;
                currentFrame = 0;  // Start with standing frame
                isFirstAttackFrame = true;
                attackTimer = 0;
            }

            attackTimer += dt;
            if (attackTimer >= frameDuration) {
                attackTimer = 0;
                
                if (isFirstAttackFrame) {
                    // After showing the first frame (standing) for one duration,
                    // move to the attack sequence
                    currentFrame = 1;
                    isFirstAttackFrame = false;
                } else {
                    // Cycle through attack frames (1-4), then back to standing (0)
                    currentFrame++;
                    if (currentFrame >= attackFrames.length) {
                        currentFrame = 0;  // Back to standing frame
                        isFirstAttackFrame = true;  // Reset the sequence
                    }
                }
            }
            return;
        }

        // If we're not attacking, handle movement
        isAttacking = false;
        frameTimer += dt;
        if (frameTimer >= frameDuration) {
            frameTimer -= frameDuration;
            currentFrame = (currentFrame + 1) % walkFrames.length;
        }

        // Continue normal movement
        double remaining = speed * dt;
        while (remaining > 0 && waypointIndex < path.size()) {
            Point target = path.get(waypointIndex);
            dx = target.x() - x;
            dy = target.y() - y;
            double dist = Math.hypot(dx, dy);

            if (dist < 1e-3) {
                x = target.x();
                y = target.y();
                waypointIndex++;
                continue;
            }

            if (remaining >= dist) {
                x = target.x();
                y = target.y();
                remaining -= dist;
                waypointIndex++;
            } else {
                x += dx / dist * remaining;
                y += dy / dist * remaining;
                remaining = 0;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Save the current state
        javafx.scene.effect.BlendMode oldBlendMode = gc.getGlobalBlendMode();
        
        // Set blend mode for proper transparency
        gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);

        // Get the current frame based on state
        Image currentSprite;
        if (isAttacking) {
            currentSprite = attackFrames[currentFrame];
        } else {
            currentSprite = walkFrames[currentFrame];
        }

        // Draw the sprite centered on entity position
        double spriteWidth = currentSprite.getWidth();
        double spriteHeight = currentSprite.getHeight();
        double drawX = x - spriteWidth / 2;
        double drawY = y - spriteHeight / 2;

        gc.drawImage(currentSprite, drawX, drawY);

        // Draw health bar
        double barWidth = spriteWidth * 0.3;
        double barHeight = 3;
        double barX = drawX + (spriteWidth - barWidth) / 2;
        double barY = drawY + (spriteHeight * 0.7);

        double healthRatio = Math.max(0, Math.min(1, hp / 100.0));
        double filledWidth = barWidth * healthRatio;

        // Background (dark red)
        gc.setFill(javafx.scene.paint.Color.web("#330000"));
        gc.fillRoundRect(barX, barY, barWidth, barHeight, barHeight, barHeight);

        // Foreground (green)
        gc.setFill(javafx.scene.paint.Color.web("#33cc33"));
        gc.fillRoundRect(barX, barY, filledWidth, barHeight, barHeight, barHeight);

        // Restore the original blend mode
        gc.setGlobalBlendMode(oldBlendMode);
    }

    private Image scaleImage(Image src, double targetWidth, double targetHeight) {
        // Create a canvas with transparent background
        javafx.scene.canvas.Canvas tempCanvas = new javafx.scene.canvas.Canvas(targetWidth, targetHeight);
        GraphicsContext gc = tempCanvas.getGraphicsContext2D();
        
        // Clear with transparent background
        gc.clearRect(0, 0, targetWidth, targetHeight);
        
        // Set global blend mode to handle transparency correctly
        gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
        
        // Draw the image with preserved transparency
        gc.drawImage(src, 0, 0, targetWidth, targetHeight);

        // Create snapshot parameters with transparent background
        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
        
        // Force transparency in the resulting image
        params.setFill(null);

        return tempCanvas.snapshot(params, null);
    }
}