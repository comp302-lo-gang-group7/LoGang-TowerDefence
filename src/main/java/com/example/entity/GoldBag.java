package com.example.entity;

import com.example.game.GameManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Objects;

/**
 * Class GoldBag
 */
public class GoldBag extends Entity {
    private static final Image SPRITE_SHEET = new Image(Objects.requireNonNull(
            GoldBag.class.getResourceAsStream("/com/example/assets/items/gold_bag.png")));

    private static final int FRAME_WIDTH = 64;
    private static final int FRAME_HEIGHT = 64;
    private static final double SCALE = 0.55;

    private final int amount;
    private double timer = 1000.0;

    private static final WritableImage STATIC_FRAME = new WritableImage(
            SPRITE_SHEET.getPixelReader(),
            0, 0,
            FRAME_WIDTH, FRAME_HEIGHT
    );

    /**
     * TODO
     */
    public GoldBag(double x, double y, int amount) {
        super(x, y, 1);
        this.amount = amount;
    }

    @Override
    /**
     * TODO
     */
    public void update(double dt) {
        timer -= dt;
        if (timer <= 0) {
            GameManager.getInstance().removeEntity(this);
        }
    }

    @Override
    /**
     * TODO
     */
    public void render(GraphicsContext gc) {
        double drawWidth = FRAME_WIDTH * SCALE;
        double drawHeight = FRAME_HEIGHT * SCALE;
        double drawX = x - drawWidth / 2;
        double drawY = y - drawHeight / 2;

        gc.drawImage(STATIC_FRAME, drawX, drawY, drawWidth, drawHeight);
    }

    @Override
    /**
     * TODO
     */
    public void onClick() {
        GameManager.getInstance().getPlayerState().addGold(amount);
        GameManager.getInstance().removeEntity(this);
    }

    /**
     * TODO
     */
    public boolean contains(double px, double py) {
        double drawWidth = FRAME_WIDTH * SCALE;
        double drawHeight = FRAME_HEIGHT * SCALE;
        double left = x - drawWidth / 2;
        double top = y - drawHeight / 2;
        return px >= left && px <= left + drawWidth &&
                py >= top && py <= top + drawHeight;
    }
}
