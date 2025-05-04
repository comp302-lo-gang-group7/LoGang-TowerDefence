package com.example.map;

import com.example.utils.Point;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class GameMap
{
	private final int width, height;
	private final ObservableMap<Point, Tile> tiles = FXCollections.observableHashMap();
	private final ObservableList<Entity> entities = FXCollections.observableArrayList();

	public GameMap( int width, int height )
	{
		this.width = width;
		this.height = height;
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
		return getTile(new Point(x, y));
	}

	public Tile getTile(Point point)
	{
		if ( point.x() >= 0 && point.x() < width && point.y() >= 0 && point.y() < height )
		{
			return tiles.get(point);
		}
		else
		{
			throw new IllegalArgumentException("Tile x or y out of map bounds");
		}
	}

	public void setTile( int x, int y, Tile tile )
	{
		setTile(new Point(x, y), tile);
	}

	public void setTile(Point point, Tile tile)
	{
		if ( point.x() >= 0 && point.x() < width && point.y() >= 0 && point.y() < height )
		{
			tiles.put(point, tile);
		}
		else
		{
			throw new IllegalArgumentException("Tile x or y out of map bounds");
		}
	}

	public ObservableMap<Point, Tile> getTiles()
	{
		return tiles;
	}

	public ObservableList<Entity> getEntities()
	{
		return entities;
	}
}
