package com.example.ui;

/**
 * The {@code SpriteProvider} interface defines methods for retrieving sprite-related
 * information, including the sprite itself and its tile coordinates.
 */
public interface SpriteProvider {

	/**
	 * Retrieves the sprite associated with this provider.
	 *
	 * @return the {@code Sprite} object.
	 */
	Sprite getSprite();

	/**
	 * Retrieves the X-coordinate of the tile associated with this provider.
	 *
	 * @return the X-coordinate of the tile.
	 */
	int getTileX();

	/**
	 * Retrieves the Y-coordinate of the tile associated with this provider.
	 *
	 * @return the Y-coordinate of the tile.
	 */
	int getTileY();
}

