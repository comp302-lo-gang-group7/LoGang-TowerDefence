package com.example.map;

import javafx.scene.image.Image;

/**
 * Represents an empty lot tile model in the map.
 * This class extends the {@link TileModel} class and provides
 * functionality for tiles that are empty lots.
 */
public class EmptyLotTileModel extends TileModel
{
	/**
	 * Constructs an {@code EmptyLotTileModel} with the specified coordinates.
	 *
	 * @param x the x-coordinate of the tile
	 * @param y the y-coordinate of the tile
	 */
	public EmptyLotTileModel(int x, int y)
	{
		super(x, y);
	}
}
