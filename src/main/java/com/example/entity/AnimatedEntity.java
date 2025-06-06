package com.example.entity;

import java.util.List;

import com.example.utils.Point;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class AnimatedEntity extends Entity {
    private final Image[] frames;
    private final double frameDuration;
    private double frameTimer = 0;
    private int currentFrame = 0;

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
        super(path.get(0).x(), path.get(0).y(), hp);
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