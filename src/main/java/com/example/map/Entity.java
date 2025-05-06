package com.example.map;

import com.example.ui.Sprite;
import com.example.ui.SpriteProvider;
import javafx.scene.image.Image;

public class Entity implements SpriteProvider {
	private final Sprite sprite;

	public Entity(double x, double y, Image image) {
		this.sprite = new Sprite(image, x, y);
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public int getTileX() {
		return (int) sprite.getX();
	}

	@Override
	public int getTileY() {
		return (int) sprite.getY();
	}
}
