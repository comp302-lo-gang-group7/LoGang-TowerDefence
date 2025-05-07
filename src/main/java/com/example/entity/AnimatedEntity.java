package com.example.entity;

import com.example.utils.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.List;
import java.util.Collections;

public class AnimatedEntity extends Entity {
    private final Image[] frames;
    private final double frameDuration;
    private double frameTimer = 0;
    private int currentFrame = 0;

    // path + movement
    private final List<Point> path;
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
            Point target = path.get(waypointIndex);
            double dx = target.x() - x, dy = target.y() - y;
            double dist = Math.hypot(dx, dy);

            // If very close to target, consider it reached
            if (dist < 1) {
                x = target.x();
                y = target.y();
                waypointIndex++;
            } else {
                // Scale movement by dt and speed
                double moveDistance = speed * dt;
                if (moveDistance > dist) moveDistance = dist;

                x += dx / dist * moveDistance;
                y += dy / dist * moveDistance;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Get the sprite dimensions
        double spriteWidth = frames[currentFrame].getWidth();
        double spriteHeight = frames[currentFrame].getHeight();

        // Center the sprite on the entity position
        double drawX = x - spriteWidth / 2;
        double drawY = y - spriteHeight / 2;

        gc.drawImage(frames[currentFrame], drawX, drawY);
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