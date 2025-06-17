package com.example.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * Class FixedEntity
 */
public class FixedEntity extends Entity {
	private final Image image;

	/**
	 * TODO
	 */
	public FixedEntity(Image image, double x, double y, int hp) {
		super(x, y, hp);
		this.image = image;
	}

	@Override
	/**
	 * TODO
	 */
	public void update(double dt) {

	}

	@Override
	/**
	 * TODO
	 */
	public void render(GraphicsContext gc) {
		gc.drawImage(image, x, y);
	}

	/**
	 * TODO
	 */
	public double getTileX() {
		return super.x;
	}

	/**
	 * TODO
	 */
	public double getTileY() {
		return super.y;
	}

	/**
	 * TODO
	 */
	public ImageView getSprite() {
		return new ImageView(this.image);
	}
}
