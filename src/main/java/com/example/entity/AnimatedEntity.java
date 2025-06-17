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
 * Class AnimatedEntity
 */
public class AnimatedEntity extends Entity {
    private final Image[] frames;
    private final double frameDuration;
    private double frameTimer = 0;
    private int currentFrame = 0;
    private double speedModifier = 1.0;
    private double slowTimer = 0;
    /**
     * TODO
     */
    private static final Image SNOWFLAKE = ImageLoader.getImage("/com/example/assets/effects/snowflake.png");
    /**
     * TODO
     */
    protected final Deque<Image> effectStack = new ArrayDeque<>();


    private final List<Point> path;

    private final double speed;
    private int waypointIndex = 0;

    public AnimatedEntity(Image spriteSheet,
                          int frameCount,
                          int frameSize,
                          double frameDuration,
                          List<Point> path,
                          double speed,
                          int hp,
                          double scaleFactor)
    {

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

    @Override
    /**
     * TODO
     */
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
     * TODO
     */
    public boolean hasReachedGoal() {
        return waypointIndex >= path.size();
    }


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

    /**
     * TODO
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

    @Override
    /**
     * TODO
     */
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
     * TODO
     */
    public double getSpeedModifier() {
        return speedModifier;
    }


    /**
     * TODO
     */
    public int modifyDamage(Tower source, int base) {
        return base;
    }


    /**
     * TODO
     */
    public double getSpeed() {
        return speed;
    }


    /**
     * TODO
     */
    protected double getSpriteWidth() {
        return frames[currentFrame].getWidth();
    }


    /**
     * TODO
     */
    protected double getSpriteHeight() {
        return frames[currentFrame].getHeight();
    }


    /**
     * TODO
     */
    public void resetToStart() {
        Point start = path.getFirst();
        this.x = start.x();
        this.y = start.y();
        this.waypointIndex = 0;
    }


    /**
     * TODO
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