package com.example.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a view for displaying sprites using an {@link ImageView}.
 * Provides functionality to bind sprite properties and update the displayed image dynamically.
 */
public class SpriteView {
	private final ImageView imageView = new ImageView();

	private final ChangeListener<Image> imageChangeListener =
			( _, _, newImage ) -> imageView.setImage(newImage);

	/**
	 * Constructs a new {@code SpriteView}.
	 */
	public SpriteView() {
	}

	/**
	 * Sets the sprite provider for this view. Binds the sprite's properties to the {@link ImageView}.
	 *
	 * @param provider the {@link SpriteProvider} to set, or {@code null} to clear the current sprite.
	 */
	public void setSpriteProvider(SpriteProvider provider) {
		if (provider == null) {
			imageView.setImage(null);
			return;
		}

		imageView.setImage(provider.getSprite().getImage());
		provider.getSprite().imageProperty().addListener(imageChangeListener);

		imageView.setX(provider.getSprite().getX());
		imageView.setY(provider.getSprite().getY());
		imageView.xProperty().bind(provider.getSprite().xProperty());
		imageView.yProperty().bind(provider.getSprite().yProperty());
	}

	/**
	 * Replaces the current sprite provider with a new one. Unbinds properties from the old provider
	 * and binds properties from the new provider.
	 *
	 * @param newProvider the new {@link SpriteProvider} to set.
	 * @param oldProvider the old {@link SpriteProvider} to remove bindings from, or {@code null} if none.
	 */
	public void replaceSpriteProvider(SpriteProvider newProvider, SpriteProvider oldProvider) {
		if (oldProvider != null) {
			oldProvider.getSprite().imageProperty().removeListener(imageChangeListener);
			imageView.xProperty().unbind();
			imageView.yProperty().unbind();
		}
		setSpriteProvider(newProvider);
	}

	/**
	 * Returns the {@link ImageView} used to display the sprite.
	 *
	 * @return the {@link ImageView} instance.
	 */
	public ImageView getImageView() {
		return imageView;
	}
}
