package com.example.entity;

import com.example.ui.ImageLoader;
import com.example.utils.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.List;

public class AnimatedEntity extends Entity {
    private final Image[] frames;
    private final double frameDuration;
    private double frameTimer = 0;
    private int currentFrame = 0;
    private double speedModifier = 1.0;
    private double slowTimer = 0;
    private static final Image SNOWFLAKE = ImageLoader.getImage("/com/example/assets/effects/snowflake.png");
    
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

    public AnimatedEntity(Image spriteSheet,
                          int frameCount,
                          int frameSize,
                          double frameDuration,
                          List<Point> path,
                          double speed,
                          int hp,
                          double scaleFactor)
    {
        // Start at the first path point
        super(path.getFirst().x(), path.getFirst().y(), hp);
        this.path = path;
        this.speed = speed;
        this.frameDuration = frameDuration;
        this.frames = new Image[frameCount];

        // slice sprite‐sheet + scaling
        for (int i = 0; i < frameCount; i++) {
            Image raw = new WritableImage(
                    spriteSheet.getPixelReader(),
                    i * frameSize, 0,
                    frameSize, frameSize
            );
            frames[i] = scaleImage(raw, frameSize * scaleFactor, frameSize * scaleFactor);
        }
    }

    @Override
    public void update(double dt) {
        // 1) animation
        frameTimer += dt;
        if (frameTimer >= frameDuration) {
            frameTimer -= frameDuration;
            currentFrame = (currentFrame + 1) % frames.length;
        }

        // 2) movement
        if (waypointIndex < path.size()) {
            if (slowTimer > 0) {
                slowTimer -= dt;
                if (slowTimer <= 0) {
                    speedModifier = 1.0;
                }
            }

            double remaining = speed * speedModifier * dt;

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

    /**
     * Returns a numeric progress value representing how far this entity has
     * advanced along its path. Higher values mean further progression.
     */
    public double getPathProgress()
    {
        if (waypointIndex <= 0)
            return 0;
        if (waypointIndex >= path.size())
            return path.size();

        int prevIndex = waypointIndex - 1;
        Point prev = path.get(prevIndex);
        Point next = path.get(waypointIndex);

        double segmentLength = Math.hypot(next.x() - prev.x(), next.y() - prev.y());
        if (segmentLength < 1e-6)
            return waypointIndex;

        double distFromPrev = Math.hypot(x - prev.x(), y - prev.y());
        return prevIndex + Math.min(1.0, distFromPrev / segmentLength);
    }

    public Point getFuturePosition()
    {
        int futureSteps = ( int ) (0.75 * speed);
        if ( waypointIndex + futureSteps < path.size() ) {
            return path.get(waypointIndex + futureSteps);
        }
        else {
            return path.getLast();
        }
    }

    public void applySlow(double factor, double duration) {
        if (slowTimer <= 0) {
            speedModifier = factor;
        }
        slowTimer = duration;
    }

    @Override
    public void render(GraphicsContext gc) {
        // 1. Draw the sprite centered on entity position
        double spriteWidth = frames[currentFrame].getWidth();
        double spriteHeight = frames[currentFrame].getHeight();
        double drawX = x - spriteWidth / 2;
        double drawY = y - spriteHeight / 2;

        gc.drawImage(frames[currentFrame], drawX, drawY);

        // 2. Draw smaller health bar just above the bottom of the sprite
        double barWidth = spriteWidth * 0.3;     // narrower
        double barHeight = 3;                    // thinner
        double barX = drawX + (spriteWidth - barWidth) / 2;
        double barY = drawY + (spriteHeight * 0.7);  // closer to sprite bottom

        double healthRatio = Math.max(0, Math.min(1, hp / 100.0));
        double filledWidth = barWidth * healthRatio;

        // Background (dark red)
        gc.setFill(javafx.scene.paint.Color.web("#330000"));
        gc.fillRoundRect(barX, barY, barWidth, barHeight, barHeight, barHeight);

        // Foreground (green)
        gc.setFill(javafx.scene.paint.Color.web("#33cc33"));
        gc.fillRoundRect(barX, barY, filledWidth, barHeight, barHeight, barHeight);

        double iconSize = 10;
        double iconX = drawX + (spriteWidth * 0.8) - iconSize;
        double iconY = drawY + (spriteHeight * 0.75) - iconSize;

        if (speedModifier < 1.0 && SNOWFLAKE != null) {
            gc.drawImage(SNOWFLAKE, iconX + 10, iconY, iconSize, iconSize);
        }
    }

    public double getSpeedModifier() {
        return speedModifier;
    }

    /**
     * Allows subclasses to modify incoming damage based on the attacking tower
     * type. By default no modification is made.
     *
     * @param source the tower dealing the damage
     * @param base   the raw damage amount
     * @return the adjusted damage this entity should take
     */
    public int modifyDamage(Tower source, int base) {
        return base;
    }

    /** Accessor for subclasses that need the base movement speed. */
    public double getSpeed() {
        return speed;
    }

    /** Width of the currently displayed sprite frame. */
    protected double getSpriteWidth() {
        return frames[currentFrame].getWidth();
    }

    /** Height of the currently displayed sprite frame. */
    protected double getSpriteHeight() {
        return frames[currentFrame].getHeight();
    }

    /**
     * Resets this entity back to the first point of its path without
     * altering hit points or any other state.
     */
    public void resetToStart() {
        Point start = path.getFirst();
        this.x = start.x();
        this.y = start.y();
        this.waypointIndex = 0;
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