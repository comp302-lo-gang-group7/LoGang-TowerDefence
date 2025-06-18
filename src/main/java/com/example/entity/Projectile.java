package com.example.entity;

import com.example.controllers.GameScreenController;
import com.example.game.GameManager;
import com.example.ui.ImageLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Represents a projectile fired by a tower to deliver damage to a target.
 */
public class Projectile extends Entity {
    private final Image image;
    private double x1, y1, x2, y2;
    private double x, y;
    private double dx, dy;
    private double dirx, diry, magnitude;
    private double speed = 10;
    private double angle;
    private double spin;
    private double spinSpeed;
    private boolean active;
    private AnimatedEntity target;
    private Tower parent;
    private double scaleFactor;

    /**
     * Constructs a projectile fired from the specified tower toward an enemy.
     *
     * @param parent The tower that spawned the projectile.
     * @param x1 The starting x position in pixels.
     * @param y1 The starting y position in pixels.
     * @param target The entity the projectile should track.
     */
    public Projectile(Tower parent, double x1, double y1, AnimatedEntity target) {
        super(0, 0, 0);
        switch (parent) {
            case ArcherTower _ -> {
                image = ImageLoader.getImage("/com/example/assets/effects/arrow.png");
                scaleFactor = 0.15;
            }
            case MageTower m -> {
                Image base = ImageLoader.getImage("/com/example/assets/effects/spell.png");
                if (m.upgradeLevel >= 2) {
                    image = tintImage(base, Color.CYAN);
                } else {
                    image = base;
                }
                scaleFactor = 0.25;
            }
            case ArtilleryTower _ -> {
                image = ImageLoader.getImage("/com/example/assets/effects/bomb.png");
                scaleFactor = 0.15;
            }
            default -> {
                image = null;
                throw new IllegalArgumentException();
            }
        }

        this.active = true;
        this.x1 = x1;
        this.y1 = y1;
        this.x = x1;
        this.y = y1;
        this.target = target;
        this.parent = parent;
        this.x2 = target.getFuturePosition().x();
        this.y2 = target.getFuturePosition().y();

        dx = x2 - x1;
        dy = y2 - y1;
        magnitude = Math.hypot(dx, dy);
        dirx = dx / magnitude;
        diry = dy / magnitude;

        angle = Math.toDegrees(Math.atan2(dy, dx));

        if (parent instanceof ArtilleryTower) {
            spinSpeed = 50;
        } else {
            spinSpeed = 0;
        }
        spin = 0;
    }

    /**
     * Updates the projectile's position and applies damage when it reaches its target.
     *
     * @param dt The time step for the update.
     */
    @Override
    public void update(double dt) {
        if (active) {
            if (Math.abs(x - x2) + Math.abs(y - y2) > 1) {
                x += dirx * speed * dt;
                y += diry * speed * dt;
                if (spinSpeed != 0) {
                    spin += spinSpeed * dt;
                }
            } else {
                this.active = false;
                if (parent instanceof ArtilleryTower) {
                    double radius = GameScreenController.TILE_SIZE;
                    int baseDmg = parent.baseDamage;

                    for (AnimatedEntity enemy : GameManager.getInstance().enemiesWithinRadius(x, y, radius)) {
                        if (enemy == target) {
                            int dmg = enemy.modifyDamage(parent, baseDmg);
                            enemy.applyDamage(dmg);
                        } else {
                            int aoeDmg = (int) (baseDmg / 3.0);
                            int dmg = enemy.modifyDamage(parent, aoeDmg);
                            enemy.applyDamage(dmg);
                        }
                    }
                } else {
                    int dmg = target.modifyDamage(parent, parent.baseDamage);
                    target.applyDamage(dmg);

                    if (parent instanceof MageTower m && m.upgradeLevel >= 2 && target instanceof AnimatedEntity a) {
                        a.applySlow(0.8, 4.0);
                    }

                    if (parent instanceof MageTower && Math.random() < 0.03 && target.getHP() > 0) {
                        target.resetToStart();
                    }
                }

                GameManager.getInstance().removeEntity(this);
                GameManager.getInstance().spawnEffect(parent, x, y);
            }
        }
    }

    /**
     * Renders the projectile with its current rotation and scaling.
     *
     * @param gc The graphics context used for rendering.
     */
    public void render(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(angle + spin);
        gc.scale(scaleFactor, scaleFactor);
        gc.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2);
        gc.restore();
    }

    /**
     * Returns a tinted copy of the source image used for spell upgrades.
     *
     * @param src The source image to tint.
     * @param tint The color tint to apply.
     * @return A tinted copy of the source image.
     */
    private static Image tintImage(Image src, Color tint) {
        int w = (int) src.getWidth();
        int h = (int) src.getHeight();
        WritableImage out = new WritableImage(w, h);
        PixelReader pr = src.getPixelReader();
        PixelWriter pw = out.getPixelWriter();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = pr.getArgb(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                r = (int) Math.min(255, r * tint.getRed());
                g = (int) Math.min(255, g * tint.getGreen());
                b = (int) Math.min(255, b * tint.getBlue());
                int outArgb = (a << 24) | (r << 16) | (g << 8) | b;
                pw.setArgb(x, y, outArgb);
            }
        }
        return out;
    }
}
