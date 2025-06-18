package com.example.entity;

import com.example.ui.ImageLoader;
import com.example.utils.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import javafx.scene.image.WritableImage;
import java.util.List;

/**
 * Represents an animated entity that traverses a predefined path while displaying sprite animations.
 * This class handles movement, animation, and status effects.
 */
public class AnimatedEntity extends Entity {
    private final Image[] frames;
    private final double frameDuration;
    private double frameTimer = 0;
    private int currentFrame = 0;
    private double speedModifier = 1.0;
    private double slowTimer = 0;
    private static final Image SNOWFLAKE = ImageLoader.getImage("/com/example/assets/effects/snowflake.png");
    protected final Deque<Image> effectStack = new ArrayDeque<>();
    private final List<Point> path;
    private final double speed;
    private int waypointIndex = 0;

    /**
     * Constructs an AnimatedEntity with the specified parameters.
     *
     * @param spriteSheet  the sprite sheet containing animation frames
     * @param frameCount   the number of frames in the sprite sheet
     * @param frameSize    the width and height of each frame in pixels
     * @param frameDuration the duration each frame is displayed in seconds
     * @param path         the list of points defining the entity's movement path
     * @param speed        the base movement speed in pixels per second
     * @param hp           the initial hit points of the entity
     * @param scaleFactor  the scaling factor for rendering the frames
     */
    public AnimatedEntity(Image spriteSheet,
                          int frameCount,
                          int frameSize,
                          double frameDuration,
                          List<Point> path,
                          double speed,
                          int hp,
                          double scaleFactor) {
        super(path.getFirst().x(), path.getFirst().y(), hp);
        this.path = path;
        this.speed = speed;
        this.frameDuration = frameDuration;
        this.frames = new Image[frameCount];
        for (int i = 0; i < frameCount; i++) {
            Image raw = new WritableImage(
                    spriteSheet.getPixelReader(),
                    i * frameSize, 0,
                    frameSize, frameSize
            );
            frames[i] = scaleImage(raw, frameSize * scaleFactor, frameSize * scaleFactor);
        }
    }

    /**
     * Updates the entity's animation and movement based on the elapsed time.
     *
     * @param dt the time delta in seconds
     */
    @Override
    public void update(double dt) {
        frameTimer += dt;
        if (frameTimer >= frameDuration) {
            frameTimer -= frameDuration;
            currentFrame = (currentFrame + 1) % frames.length;
        }
        if (waypointIndex < path.size()) {
            if (slowTimer > 0) {
                slowTimer -= dt;
                if (slowTimer <= 0) {
                    speedModifier = 1.0;
                    effectStack.remove(SNOWFLAKE);
                }
            }
            double remaining = speed * speedModifier * dt;
            while (remaining > 0 && waypointIndex < path.size()) {
                Point target = path.get(waypointIndex);
                double dx = target.x() - x, dy = target.y() - y;
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
    }

    /**
     * Checks if the entity has reached the final point in its path.
     *
     * @return true if the entity has reached the goal, false otherwise
     */
    public boolean hasReachedGoal() {
        return waypointIndex >= path.size();
    }

    /**
     * Calculates the entity's progress along its path.
     *
     * @return a numeric value representing the progress along the path
     */
    public double getPathProgress() {
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

    /**
     * Predicts the entity's future position along its path.
     *
     * @return the predicted future position as a Point
     */
    public Point getFuturePosition() {
        int futureSteps = (int) (0.75 * speed);
        if (waypointIndex + futureSteps < path.size()) {
            return path.get(waypointIndex + futureSteps);
        } else {
            return path.getLast();
        }
    }

    /**
     * Applies a temporary slow effect to the entity.
     *
     * @param factor   the speed multiplier while slowed
     * @param duration the duration of the slow effect in seconds
     */
    public void applySlow(double factor, double duration) {
        if (slowTimer <= 0) {
            speedModifier = factor;
            if (SNOWFLAKE != null && !effectStack.contains(SNOWFLAKE)) {
                effectStack.addLast(SNOWFLAKE);
            }
        }
        slowTimer = duration;
    }

    /**
     * Renders the entity's current animation frame and status effects.
     *
     * @param gc the graphics context to draw to
     */
    @Override
    public void render(GraphicsContext gc) {
        double spriteWidth = frames[currentFrame].getWidth();
        double spriteHeight = frames[currentFrame].getHeight();
        double drawX = x - spriteWidth / 2;
        double drawY = y - spriteHeight / 2;
        gc.drawImage(frames[currentFrame], drawX, drawY);
        double barWidth = spriteWidth * 0.3;
        double barHeight = 3;
        double barX = drawX + (spriteWidth - barWidth) / 2;
        double barY = drawY + (spriteHeight * 0.7);
        double healthRatio = Math.max(0, Math.min(1, hp / 100.0));
        double filledWidth = barWidth * healthRatio;
        gc.setFill(javafx.scene.paint.Color.web("#330000"));
        gc.fillRoundRect(barX, barY, barWidth, barHeight, barHeight, barHeight);
        gc.setFill(javafx.scene.paint.Color.web("#33cc33"));
        gc.fillRoundRect(barX, barY, filledWidth, barHeight, barHeight, barHeight);
        double iconSize = 15;
        double stackX = barX;
        double stackY = barY - iconSize - 2;
        List<Image> icons = new ArrayList<>(effectStack);
        for (Image icon : icons) {
            gc.drawImage(icon, stackX, stackY, iconSize, iconSize);
            stackX += iconSize + 2;
        }
    }

    /**
     * Gets the current speed modifier applied to the entity.
     *
     * @return the speed modifier
     */
    public double getSpeedModifier() {
        return speedModifier;
    }

    /**
     * Modifies the damage taken by the entity based on the attacking tower.
     *
     * @param source the tower dealing the damage
     * @param base   the raw damage amount
     * @return the adjusted damage value
     */
    public int modifyDamage(Tower source, int base) {
        return base;
    }

    /**
     * Gets the base movement speed of the entity.
     *
     * @return the base speed in pixels per second
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Gets the width of the currently displayed sprite frame.
     *
     * @return the width of the sprite frame in pixels
     */
    protected double getSpriteWidth() {
        return frames[currentFrame].getWidth();
    }

    /**
     * Gets the height of the currently displayed sprite frame.
     *
     * @return the height of the sprite frame in pixels
     */
    protected double getSpriteHeight() {
        return frames[currentFrame].getHeight();
    }

    /**
     * Resets the entity's position to the start of its path.
     */
    public void resetToStart() {
        Point start = path.getFirst();
        this.x = start.x();
        this.y = start.y();
        this.waypointIndex = 0;
    }

    /**
     * Scales an image to the specified width and height.
     *
     * @param src          the original image
     * @param targetWidth  the desired width of the scaled image
     * @param targetHeight the desired height of the scaled image
     * @return the scaled image
     */
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
