package com.example.map;

import com.example.ui.SpriteProvider;
import com.example.ui.Sprite;

public class Tile implements SpriteProvider
{
	private final int x, y;
	private final Sprite sprite = new Sprite();

	public Tile ( int x, int y )
	{
		this.x = x;
		this.y = y;
	}

	public Tile( int x, int y, String spritePath )
	{
		this.x = x;
		this.y = y;
		sprite.setImagePath(spritePath);
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Sprite getSprite()
	{
		return sprite;
	}
}
