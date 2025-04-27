package com.example.map;

import javafx.scene.image.Image;

public class Tile
{
	private final int x, y;
	private final Image sprite;

	public Tile( int x, int y, Image sprite )
	{
		this.x = x;
		this.y = y;
		this.sprite = sprite;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Image getSprite() throws NullPointerException
	{
		if (sprite == null)
			throw new NullPointerException("Sprite is not set");
		else
			return sprite;
	}
}
