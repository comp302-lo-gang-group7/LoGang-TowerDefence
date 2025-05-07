package com.example.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A tower that never animates—just one static texture.
 */
public class FixedEntity extends Entity {
	private final Image image;

	public FixedEntity(Image image, double x, double y, int hp) {
		super(x, y, hp);
		this.image = image;
	}

	@Override
	public void update(double dt) {
		// any tower logic (targeting, cooldown)...
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.drawImage(image, x, y);
	}

	public double getTileX() {
		return super.x;
	}

	public double getTileY() {
		return super.y;
	}

	public ImageView getSprite() {
		return new ImageView(this.image);
	}
}
