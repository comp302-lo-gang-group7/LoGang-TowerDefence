package com.example.map;

import com.example.ui.Sprite;
import com.example.ui.SpriteProvider;

public class Entity implements SpriteProvider
{
	private final Sprite sprite;

	public Entity( double x, double y, String spritePath )
	{
		this.sprite = new Sprite(spritePath, x, y);
	}

	@Override
	public Sprite getSprite()
	{
		return sprite;
	}
}
