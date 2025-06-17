package com.example.animation;

import com.example.animation.MenuWarrior;
import com.example.animation.MenuGoblin;
import com.example.utils.Point;
import com.example.ui.ImageLoader;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.*;

/**
 * Simple animator used on the main menu background. It spawns random
 * enemy sprites and various projectiles that move across the screen
 * purely for visual flair.
 */
public class MainMenuBackgroundAnimator {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final List<MenuGoblin> goblins = new ArrayList<>();
    private final List<MenuWarrior> warriors = new ArrayList<>();
    private final List<ProjectileSprite> projectiles = new ArrayList<>();
    private final Random rng = new Random();
    private AnimationTimer timer;

    public MainMenuBackgroundAnimator(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void start() {
        timer = new AnimationTimer() {
            long last = 0;
            @Override
            public void handle(long now) {
                if (last == 0) { last = now; return; }
                double dt = (now - last) / 1e9;
                last = now;
                update(dt);
                render();
            }
        };
        timer.start();
    }

    private void update(double dt) {
        if (rng.nextDouble() < dt * 0.5) spawnUnit();
        if (rng.nextDouble() < dt * 0.8) spawnProjectile();

        goblins.forEach(g -> g.update(dt));
        warriors.forEach(w -> w.update(dt));
        projectiles.forEach(p -> p.update(dt));

        goblins.removeIf(MenuGoblin::hasReachedGoal);
        warriors.removeIf(MenuWarrior::hasReachedGoal);
        projectiles.removeIf(p -> p.isOffscreen(canvas.getWidth(), canvas.getHeight()));
    }

    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        goblins.forEach(g -> g.render(gc));
        warriors.forEach(w -> w.render(gc));
        projectiles.forEach(p -> p.render(gc));
    }

    private void spawnUnit() {
        double y = 300 + rng.nextDouble() * 250;
        boolean left = rng.nextBoolean();
        List<Point> path = new ArrayList<>();
        if (left) {
            path.add(new Point(-50, (int) y));
            path.add(new Point((int) canvas.getWidth() + 50, (int) y));
        } else {
            path.add(new Point((int) canvas.getWidth() + 50, (int) y));
            path.add(new Point(-50, (int) y));
        }
        if (rng.nextBoolean()) {
            goblins.add(new MenuGoblin(path, 60));
        } else {
            warriors.add(new MenuWarrior(path, 60));
        }
    }

    private void spawnProjectile() {
        double startX, startY, endX, endY;
        boolean fromLeft = rng.nextBoolean();
        startY = rng.nextDouble() * canvas.getHeight();
        endY = rng.nextDouble() * canvas.getHeight();
        if (fromLeft) {
            startX = -20;
            endX = canvas.getWidth() + 20;
        } else {
            startX = canvas.getWidth() + 20;
            endX = -20;
        }
        String[] paths = {
                "/com/example/assets/effects/arrow.png",
                "/com/example/assets/effects/bomb.png",
                "/com/example/assets/effects/spell.png"
        };
        Image img = ImageLoader.getImage(paths[rng.nextInt(paths.length)]);
        projectiles.add(new ProjectileSprite(img, startX, startY, endX, endY));
    }

    private static class ProjectileSprite {
        private final Image image;
        private final double dirX;
        private final double dirY;
        private final double angle;
        private double x, y;
        private final double speed = 150;
        private final double scale = 0.25;
        private final boolean isBomb;
        private double spin = 0;
        private final double spinSpeed;

        ProjectileSprite(Image img, double x1, double y1, double x2, double y2) {
            this.image = img;
            this.x = x1;
            this.y = y1;
            double dx = x2 - x1;
            double dy = y2 - y1;
            double mag = Math.hypot(dx, dy);
            this.dirX = dx / mag;
            this.dirY = dy / mag;
            this.angle = Math.toDegrees(Math.atan2(dy, dx));
            this.isBomb = image.getUrl() != null && image.getUrl().contains("bomb");
            this.spinSpeed = isBomb ? 50 : 0;
        }

        void update(double dt) {
            x += dirX * speed * dt;
            y += dirY * speed * dt;
            if (isBomb) {
                spin += spinSpeed * dt * 0.1;
            }
        }

        void render(GraphicsContext gc) {
            gc.save();
            gc.translate(x, y);

            boolean movingLeft = dirX < 0;

            if (isBomb && movingLeft) {
                gc.scale(-1, 1);       // flip horizontally before rotation
            }

            gc.rotate(angle + spin);
            gc.scale(scale, scale);
            gc.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2);

            gc.restore();
        }

        boolean isOffscreen(double w, double h) {
            return x < -50 || x > w + 50 || y < -50 || y > h + 50;
        }
    }
}