package com.example.animation;

import com.example.ui.ImageLoader;
import com.example.utils.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.List;

/**
 * Represents a warrior character in the menu that animates and moves along a predefined path.
 */
public class MenuWarrior {
    private static final Image SPRITE_SHEET = ImageLoader.getImage("/com/example/assets/enemies/Warrior_Blue.png");
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

    /**
     * Constructs a MenuWarrior with a specified path and movement speed.
     *
     * @param path  The list of points representing the path the warrior will follow.
     * @param speed The speed at which the warrior moves along the path.
     */
    public MenuWarrior(List<Point> path, double speed) {
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

    /**
     * Updates the animation frame and position of the warrior based on the elapsed time.
     *
     * @param dt The time elapsed since the last update, in seconds.
     */
    public void update(double dt) {
        frameTimer += dt;
        if (frameTimer >= FRAME_SECONDS) {
            frameTimer -= FRAME_SECONDS;
            currentFrame = (currentFrame + 1) % FRAMES;
        }

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

    /**
     * Renders the warrior on the provided graphics context.
     *
     * @param gc The graphics context used for rendering the warrior.
     */
    public void render(GraphicsContext gc) {
        Image img = frames[currentFrame];
        double drawX = x;
        double drawY = y;

        gc.save();
        gc.translate(drawX, drawY);

        boolean movingLeft = isMovingLeft();
        if (movingLeft) {
            gc.scale(-1, 1);
        }

        double offsetX = img.getWidth() / 2;
        double offsetY = img.getHeight() / 2;
        gc.drawImage(img, movingLeft ? -offsetX : -offsetX, -offsetY);

        gc.restore();
    }

    /**
     * Determines whether the warrior is moving left based on its current and previous waypoints.
     *
     * @return True if the warrior is moving left, false otherwise.
     */
    private boolean isMovingLeft() {
        if (waypointIndex <= 0 || waypointIndex >= path.size()) return false;
        Point prev = path.get(waypointIndex - 1);
        Point next = path.get(waypointIndex);
        return next.x() < prev.x();
    }

    /**
     * Checks if the warrior has reached the final waypoint in its path.
     *
     * @return True if the warrior has reached the goal, false otherwise.
     */
    public boolean hasReachedGoal() {
        return waypointIndex >= path.size();
    }

    /**
     * Scales an image to the specified width and height.
     *
     * @param src          The source image to be scaled.
     * @param targetWidth  The desired width of the scaled image.
     * @param targetHeight The desired height of the scaled image.
     * @return The scaled image.
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
