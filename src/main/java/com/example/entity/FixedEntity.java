package com.example.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a fixed entity in the game, such as a static tower.
 * This entity does not animate and uses a single static texture.
 */
public class FixedEntity extends Entity {
	private final Image image;

	/**
	 * Constructs a FixedEntity with the specified image, position, and health points.
	 *
	 * @param image the image representing the entity
	 * @param x the x-coordinate of the entity's position
	 * @param y the y-coordinate of the entity's position
	 * @param hp the health points of the entity
	 */
	public FixedEntity(Image image, double x, double y, int hp) {
		super(x, y, hp);
		this.image = image;
	}

	/**
	 * Updates the entity's state.
	 * 
	 * @param dt the time elapsed since the last update
	 */
	@Override
	public void update(double dt) {
		// any tower logic (targeting, cooldown)...
	}

	/**
	 * Renders the entity on the provided graphics context.
	 *
	 * @param gc the graphics context used for rendering
	 */
	@Override
	public void render(GraphicsContext gc) {
		gc.drawImage(image, x, y);
	}

	/**
	 * Gets the x-coordinate of the entity's position on the tile grid.
	 *
	 * @return the x-coordinate of the entity's position
	 */
	public double getTileX() {
		return super.x;
	}

	/**
	 * Gets the y-coordinate of the entity's position on the tile grid.
	 *
	 * @return the y-coordinate of the entity's position
	 */
	public double getTileY() {
		return super.y;
	}

	/**
	 * Gets the sprite representation of the entity as an ImageView.
	 *
	 * @return an ImageView containing the entity's image
	 */
	public ImageView getSprite() {
		return new ImageView(this.image);
	}
}
