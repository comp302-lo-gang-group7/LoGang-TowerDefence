package com.example.map;

import com.example.ui.SpriteProvider;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GameMap
{
	private final int width, height;
	private final ObjectProperty<SpriteProvider>[][] tiles;

	public GameMap( int width, int height )
	{
		this.width = width;
		this.height = height;
		this.tiles = new ObjectProperty[height][];
		for ( int i = 0; i < height; i++ )
		{
			this.tiles[i] = new ObjectProperty[width];
			for ( int j = 0; j < width; j++ )
			{
				this.tiles[i][j] = new SimpleObjectProperty<>();
			}
		}
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public Tile getTile(int x, int y)
	{
		return ( Tile ) tiles[y][x].get();
	}

	public ObjectProperty<SpriteProvider> getTileProperty( int x, int y)
	{
		return tiles[y][x];
	}

	public void setTile(int x, int y, Tile tile)
	{
		tiles[y][x].set(tile);
	}

	public ObjectProperty<SpriteProvider>[][] getTiles()
	{
		return tiles;
	}
}
