package com.example.map;

import com.example.controllers.GameScreenController;
import com.example.ui.SpriteProvider;
import com.example.ui.Sprite;
import javafx.scene.image.Image;

public class Tile implements SpriteProvider
{
	private final int tileX, tileY;
	private final Sprite sprite;

	public Tile( int tileX, int tileY, Image image )
	{
		this.tileX = tileX;
		this.tileY = tileY;
		this.sprite = new Sprite(image, GameScreenController.TILE_SIZE * tileX, GameScreenController.TILE_SIZE * tileY);
		// TODO: WHICH PART IS RESPONSIBLE FOR CONVERTING TILE COORDINATES INTO GLOBAL COORDINATES?
	}

	public int getTileX()
	{
		return tileX;
	}

	public int getTileY()
	{
		return tileY;
	}

	public Sprite getSprite()
	{
		return sprite;
	}
}
