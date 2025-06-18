package com.example.entity;

import com.example.game.GameManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Objects;

/**
 * Represents a Gold Bag entity that provides gold to the player when clicked.
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
     * Constructs a Gold Bag entity.
     *
     * @param x The x-coordinate of the Gold Bag.
     * @param y The y-coordinate of the Gold Bag.
     * @param amount The amount of gold contained in the Gold Bag.
     */
    public GoldBag(double x, double y, int amount) {
        super(x, y, 1);
        this.amount = amount;
    }

    /**
     * Updates the Gold Bag's state, including its lifetime.
     *
     * @param dt The time step for the update.
     */
    @Override
    public void update(double dt) {
        timer -= dt;
        if (timer <= 0) {
            GameManager.getInstance().removeEntity(this);
        }
    }

    /**
     * Renders the Gold Bag on the game screen.
     *
     * @param gc The graphics context used for rendering.
     */
    @Override
    public void render(GraphicsContext gc) {
        double drawWidth = FRAME_WIDTH * SCALE;
        double drawHeight = FRAME_HEIGHT * SCALE;
        double drawX = x - drawWidth / 2;
        double drawY = y - drawHeight / 2;

        gc.drawImage(STATIC_FRAME, drawX, drawY, drawWidth, drawHeight);
    }

    /**
     * Handles the click event on the Gold Bag, adding gold to the player's state.
     */
    @Override
    public void onClick() {
        GameManager.getInstance().getPlayerState().addGold(amount);
        GameManager.getInstance().removeEntity(this);
    }

    /**
     * Checks if the specified point is within the Gold Bag's bounds.
     *
     * @param px The x-coordinate of the point.
     * @param py The y-coordinate of the point.
     * @return True if the point is within the bounds, false otherwise.
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
