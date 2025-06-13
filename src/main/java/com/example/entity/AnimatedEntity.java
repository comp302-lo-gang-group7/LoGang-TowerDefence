package com.example.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.utils.Point;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class AnimatedEntity extends Entity {
    public enum AnimationState {
        WALKING,
        ATTACKING // New state for hitting animation
    }

    private Map<AnimationState, Image[]> animationFrames;
    private AnimationState currentAnimationState;
    private double frameTimer = 0;
    private int currentFrame = 0;
    private double rotation = 0; // New field for rotation

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
    private boolean moving = true; // New field to control movement

    public AnimatedEntity(Map<AnimationState, Image> spriteSheets,
                          Map<AnimationState, Integer> frameCounts,
                          int frameSize,
                          Map<AnimationState, Double> frameDurations,
                          List<Point> path,
                          double speed,
                          int hp,
                          double scaleFactor)
    {
        // Start at the first path point
        super(path.getFirst().x(), path.getFirst().y(), hp);
        this.path = path;
        this.speed = speed;
        this.animationFrames = new HashMap<>();

        // Initialize animation frames for each state
        spriteSheets.forEach((state, spriteSheet) -> {
            int frameCount = frameCounts.get(state);
            Image[] frames = new Image[frameCount];
            for (int i = 0; i < frameCount; i++) {
                Image raw = new WritableImage(
                        spriteSheet.getPixelReader(),
                        i * frameSize, 0,
                        frameSize, frameSize
                );
                frames[i] = scaleImage(raw, frameSize * scaleFactor, frameSize * scaleFactor);
            }
            this.animationFrames.put(state, frames);
        });

        // Set initial animation state
        setAnimationState(AnimationState.WALKING);
    }

    // Existing constructor for single animation, for backward compatibility
    public AnimatedEntity(Image spriteSheet,
                          int frameCount,
                          int frameSize,
                          double frameDuration,
                          List<Point> path,
                          double speed,
                          int hp,
                          double scaleFactor)
    {
        this(
            Map.of(AnimationState.WALKING, spriteSheet),
            Map.of(AnimationState.WALKING, frameCount),
            frameSize,
            Map.of(AnimationState.WALKING, frameDuration),
            path, speed, hp, scaleFactor
        );
    }

    public void setAnimationState(AnimationState state) {
        if (this.currentAnimationState != state) {
            this.currentAnimationState = state;
            this.currentFrame = 0;
            this.frameTimer = 0;
        }
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    // New getter for the 'moving' field
    public boolean isMoving() {
        return moving;
    }

    @Override
    public void update(double dt) {
        // 1) animation
        frameTimer += dt;
        Image[] frames = animationFrames.get(currentAnimationState);
        double frameDuration = (currentAnimationState == AnimationState.ATTACKING) ? 0.2 : 0.1; // Example: faster attack animation
        if (frameTimer >= frameDuration) {
            frameTimer -= frameDuration;
            currentFrame = (currentFrame + 1) % frames.length;
        }

        // 2) movement
        if (moving && waypointIndex < path.size()) { // Only move if 'moving' is true
            double remaining = speed * dt;

            while (remaining > 0 && waypointIndex < path.size()) {
                Point target = path.get(waypointIndex);
                double dx = target.x() - x, dy = target.y() - y;
                double dist = Math.hypot(dx, dy);

                if (dist < 1e-3) {
                    // Snap to the waypoint and advance to the next
                    x = target.x();
                    y = target.y();
                    waypointIndex++;
                    continue;
                }

                if (remaining >= dist) {
                    // Consume the entire segment and keep going with leftover distance
                    x = target.x();
                    y = target.y();
                    remaining -= dist;
                    waypointIndex++;
                } else {
                    // Move partially along the segment and finish the update
                    x += dx / dist * remaining;
                    y += dy / dist * remaining;
                    remaining = 0;
                }
            }
        }
    }

    /**
     * Returns true if this entity has traversed its entire path and reached the final
     * goal tile.
     */
    public boolean hasReachedGoal() {
        return waypointIndex >= path.size();
    }


    public Point getFuturePosition()
    {
        int futureSteps = ( int ) (1.5 * speed);
        if ( waypointIndex + futureSteps < path.size() ) {
            return path.get(waypointIndex + futureSteps);
        }
        else {
            return path.getLast();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // 1. Draw the sprite centered on entity position, with rotation
        Image currentSprite = animationFrames.get(currentAnimationState)[currentFrame];
        double spriteWidth = currentSprite.getWidth();
        double spriteHeight = currentSprite.getHeight();
        double drawX = x;
        double drawY = y;

        gc.save();
        gc.translate(drawX, drawY);
        gc.rotate(rotation);
        gc.drawImage(currentSprite, -spriteWidth / 2, -spriteHeight / 2);
        gc.restore();

        // 2. Draw smaller health bar just above the bottom of the sprite
        double barWidth = spriteWidth * 0.3;     // narrower
        double barHeight = 3;                    // thinner
        double barX = x - barWidth / 2;
        double barY = y + (spriteHeight * 0.7) / 2;  // closer to sprite bottom

        double healthRatio = Math.max(0, Math.min(1, hp / 100.0));
        double filledWidth = barWidth * healthRatio;

        // Background (dark red)
        gc.setFill(javafx.scene.paint.Color.web("#330000"));
        gc.fillRoundRect(barX, barY, barWidth, barHeight, barHeight, barHeight);

        // Foreground (green)
        gc.setFill(javafx.scene.paint.Color.web("#33cc33"));
        gc.fillRoundRect(barX, barY, filledWidth, barHeight, barHeight, barHeight);
    }



    private Image scaleImage(Image src, double targetWidth, double targetHeight) {
        javafx.scene.canvas.Canvas tempCanvas = new javafx.scene.canvas.Canvas(targetWidth, targetHeight);
        GraphicsContext gc = tempCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, targetWidth, targetHeight); // ensure it's transparent
        gc.drawImage(src, 0, 0, targetWidth, targetHeight);

        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);

        return tempCanvas.snapshot(params, null);
    }
}