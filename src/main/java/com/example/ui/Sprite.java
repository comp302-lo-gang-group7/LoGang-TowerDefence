package com.example.ui;

import javafx.beans.property.*;
import javafx.scene.image.Image;

/**
 * Represents a graphical sprite with position and image properties.
 */
public class Sprite {
	private final DoubleProperty x = new SimpleDoubleProperty();
	private final DoubleProperty y = new SimpleDoubleProperty();
	private final ObjectProperty<Image> image = new SimpleObjectProperty<>();

	/**
	 * Constructs a Sprite with default values.
	 * The image is set to null, and the position is set to (0.0, 0.0).
	 */
	public Sprite() {
		image.set(null);
		x.set(0.0);
		y.set(0.0);
	}

	/**
	 * Constructs a Sprite with the specified image and position.
	 *
	 * @param image the image to be associated with the sprite
	 * @param x     the x-coordinate of the sprite
	 * @param y     the y-coordinate of the sprite
	 */
	public Sprite(Image image, double x, double y) {
		this.image.set(image);
		this.x.set(x);
		this.y.set(y);
	}

	/**
	 * Gets the x-coordinate of the sprite.
	 *
	 * @return the x-coordinate of the sprite
	 */
	public double getX() {
		return x.get();
	}

	/**
	 * Gets the x-coordinate property of the sprite.
	 *
	 * @return the x-coordinate property
	 */
	public DoubleProperty xProperty() {
		return x;
	}

	/**
	 * Gets the y-coordinate of the sprite.
	 *
	 * @return the y-coordinate of the sprite
	 */
	public double getY() {
		return y.get();
	}

	/**
	 * Gets the y-coordinate property of the sprite.
	 *
	 * @return the y-coordinate property
	 */
	public DoubleProperty yProperty() {
		return y;
	}

	/**
	 * Unbinds all properties of the sprite.
	 * This method should be called when the sprite is no longer needed.
	 */
	public void unbind() {
		image.unbind();
		x.unbind();
		y.unbind();
	}

	/**
	 * Gets the image associated with the sprite.
	 *
	 * @return the image of the sprite
	 */
	public Image getImage() {
		return image.get();
	}

	/**
	 * Sets the image associated with the sprite.
	 *
	 * @param image the new image to be associated with the sprite
	 */
	public void setImage(Image image) {
		this.image.set(image);
	}

	/**
	 * Gets the image property of the sprite.
	 *
	 * @return the image property
	 */
	public ObjectProperty<Image> imageProperty() {
		return image;
	}
}
