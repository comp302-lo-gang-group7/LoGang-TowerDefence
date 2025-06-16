package com.example.entity;

import com.example.game.GameManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Objects;

public class GoldBag extends Entity {
    private static final Image SPRITE_SHEET = new Image(Objects.requireNonNull(
            GoldBag.class.getResourceAsStream("/com/example/assets/items/gold_bag.png")));

    private static final int FRAME_WIDTH = 64;
    private static final int FRAME_HEIGHT = 64;
    private static final double SCALE = 0.7;
    private static final double SWITCH_INTERVAL = 1.0;

    private final int amount;
    private double timer = 1000.0;
    private double animTimer = 0.0;
    private int frameIndex = 0;

    public GoldBag(double x, double y, int amount) {
        super(x, y, 1);
        this.amount = amount;
    }

    @Override
    public void update(double dt) {
        animTimer += dt;
        if (animTimer >= SWITCH_INTERVAL) {
            animTimer = 0;
            frameIndex = (frameIndex + 1) % 2; // toggle between 0 and 1
        }

        timer -= dt;
        if (timer <= 0) {
            GameManager.getInstance().removeEntity(this);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Extract current frame from spritesheet
        WritableImage frame = new WritableImage(
                SPRITE_SHEET.getPixelReader(),
                frameIndex * FRAME_WIDTH, 0,
                FRAME_WIDTH, FRAME_HEIGHT
        );

        double drawWidth = FRAME_WIDTH * SCALE;
        double drawHeight = FRAME_HEIGHT * SCALE;
        double drawX = x - drawWidth / 2;
        double drawY = y - drawHeight / 2;

        gc.drawImage(frame, drawX, drawY, drawWidth, drawHeight);
    }

    @Override
    public void onClick() {
        GameManager.getInstance().getPlayerState().addGold(amount);
        GameManager.getInstance().removeEntity(this);
    }

    public boolean contains(double px, double py) {
        double drawWidth = FRAME_WIDTH * SCALE;
        double drawHeight = FRAME_HEIGHT * SCALE;
        double left = x - drawWidth / 2;
        double top = y - drawHeight / 2;
        return px >= left && px <= left + drawWidth &&
                py >= top && py <= top + drawHeight;
    }
}
