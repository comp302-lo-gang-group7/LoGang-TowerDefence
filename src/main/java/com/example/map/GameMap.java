package com.example.map;

public class GameMap
{
	private final int width, height;
	private final Tile[][] tiles;

	public GameMap( int width, int height )
	{
		this.width = width;
		this.height = height;
		tiles = new Tile[height][width];
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
		return tiles[y][x];
	}

	public void setTile(int x, int y, Tile tile)
	{
		tiles[y][x] = tile;
	}

	public Tile[][] getTiles()
	{
		return tiles;
	}
}
