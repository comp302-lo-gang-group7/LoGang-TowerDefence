package com.example.entity;

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

    // path + movement
    private final List<Point> path;
    private final double speed;
    private int waypointIndex = 1;

    public AnimatedEntity(Image spriteSheet,
                          int frameCount,
                          int frameSize,
                          double frameDuration,
                          List<Point> path,
                          double speed,
                          int hp)
    {
        // start x,y at the first path point:
        super(path.get(0).x(), path.get(0).y(), hp);
        this.path          = path;
        this.speed         = speed;
        this.frameDuration = frameDuration;
        this.frames        = new Image[frameCount];

        // slice sprite‐sheet
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new WritableImage(
                    spriteSheet.getPixelReader(),
                    i * frameSize, 0,
                    frameSize, frameSize
            );
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
            if (dist <= speed * dt) {
                x = target.x();
                y = target.y();
                waypointIndex++;
            } else {
                x += dx / dist * speed * dt;
                y += dy / dist * speed * dt;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(frames[currentFrame], x, y);
    }
}
