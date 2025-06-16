package com.example.animation;

import com.example.ui.ImageLoader;
import com.example.utils.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.List;

public class MenuGoblin {
    private static final Image SPRITE_SHEET = ImageLoader.getImage("/com/example/assets/enemies/Goblin_Red.png");
    private static final int FRAMES = 6;
    private static final int FRAME_SIZE = 192;
    private static final double FRAME_SECONDS = 0.1;
    private static final double SCALE = 0.5;

    private final Image[] frames = new Image[FRAMES];
    private double frameTimer = 0;
    private int currentFrame = 0;

    private final List<Point> path;
    private final double speed;
    private int waypointIndex = 0;
    private double x, y;

    public MenuGoblin(List<Point> path, double speed) {
        this.path = path;
        this.speed = speed;
        Point start = path.get(0);
        this.x = start.x();
        this.y = start.y();

        for (int i = 0; i < FRAMES; i++) {
            WritableImage raw = new WritableImage(
                    SPRITE_SHEET.getPixelReader(),
                    i * FRAME_SIZE, 0,
                    FRAME_SIZE, FRAME_SIZE
            );
            frames[i] = scaleImage(raw, FRAME_SIZE * SCALE, FRAME_SIZE * SCALE);
        }
    }

    public void update(double dt) {
        // Animation
        frameTimer += dt;
        if (frameTimer >= FRAME_SECONDS) {
            frameTimer -= FRAME_SECONDS;
            currentFrame = (currentFrame + 1) % FRAMES;
        }

        // Movement
        if (waypointIndex >= path.size()) return;

        double remaining = speed * dt;
        while (remaining > 0 && waypointIndex < path.size()) {
            Point target = path.get(waypointIndex);
            double dx = target.x() - x;
            double dy = target.y() - y;
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

    public void render(GraphicsContext gc) {
        Image img = frames[currentFrame];
        double drawX = x;
        double drawY = y;

        gc.save();
        gc.translate(drawX, drawY);

        boolean movingLeft = isMovingLeft();
        if (movingLeft) {
            gc.scale(-1, 1); // flip horizontally
        }

        double offsetX = img.getWidth() / 2;
        double offsetY = img.getHeight() / 2;
        gc.drawImage(img, movingLeft ? -offsetX : -offsetX, -offsetY);

        gc.restore();
    }

    private boolean isMovingLeft() {
        if (waypointIndex <= 0 || waypointIndex >= path.size()) return false;
        Point prev = path.get(waypointIndex - 1);
        Point next = path.get(waypointIndex);
        return next.x() < prev.x();
    }

    public boolean hasReachedGoal() {
        return waypointIndex >= path.size();
    }

    private Image scaleImage(Image src, double targetWidth, double targetHeight) {
        javafx.scene.canvas.Canvas tempCanvas = new javafx.scene.canvas.Canvas(targetWidth, targetHeight);
        GraphicsContext gc = tempCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, targetWidth, targetHeight); // ensure it's transparent
        gc.drawImage(src, 0, 0, targetWidth, targetHeight);

        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT); // <=== KEY LINE

        return tempCanvas.snapshot(params, null);
    }
}
