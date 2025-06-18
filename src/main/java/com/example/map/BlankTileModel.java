package com.example.map;

import javafx.scene.image.Image;

/**
 * Represents a blank tile model in the map.
 * This class extends {@link TileModel} and provides functionality for a tile with no specific properties.
 */
public class BlankTileModel extends TileModel
{
	/**
	 * Constructs a new {@code BlankTileModel} with the specified coordinates.
	 *
	 * @param x The x-coordinate of the tile.
	 * @param y The y-coordinate of the tile.
	 */
	public BlankTileModel(int x, int y)
	{
		super(x, y);
	}
}
