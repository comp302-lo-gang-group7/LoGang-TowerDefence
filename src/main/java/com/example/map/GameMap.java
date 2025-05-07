package com.example.map;

import com.example.utils.Point;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.HashSet;
import java.util.Set;

public class GameMap {
	private final int width, height;
	private final ObservableMap<Point, TileModel> tiles = FXCollections.observableHashMap();
	private final ObservableList<Entity> entities = FXCollections.observableArrayList();
	private int[][] expandedGrid;

	private static final int TILE_SIZE = 64;

	// Categories
	private static final Set<TileEnum> PATH_TILES = Set.of(
			TileEnum.TOP_LEFT_PATH_CORNER,
			TileEnum.DOWN_CURVING_PATH,
			TileEnum.TOP_RIGHT_PATH_CORNER,
			TileEnum.VERTICAL_UPPER_PATH_END,
			TileEnum.RIGHT_CURVING_PATH,
			TileEnum.LEFT_CURVING_PATH,
			TileEnum.VERTICAL_PATH,
			TileEnum.BOTTOM_LEFT_PATH_CORNER,
			TileEnum.UP_CURVING_PATH,
			TileEnum.BOTTOM_RIGHT_PATH_CORNER,
			TileEnum.VERTICAL_LOWER_PATH_END,
			TileEnum.HORIZONTAL_LEFT_PATH_END,
			TileEnum.HORIZONTAL_PATH,
			TileEnum.HORIZONTAL_RIGHT_PATH_END
	);

	private static final Set<TileEnum> CASTLE_TILES = Set.of(
			TileEnum.CASTLE_TOP_LEFT,
			TileEnum.CASTLE_TOP_RIGHT,
			TileEnum.CASTLE_BOTTOM_LEFT,
			TileEnum.CASTLE_BOTTOM_RIGHT
	);

	public GameMap(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public GameMap(TileView[][] tileViews) {
		this.width = tileViews[0].length;
		this.height = tileViews.length;

		int pixelWidth = width * TILE_SIZE;
		int pixelHeight = height * TILE_SIZE;
		expandedGrid = new int[pixelHeight][pixelWidth];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				TileView tv = tileViews[y][x];
				TileEnum type = tv.getType();

				int value;
				if (PATH_TILES.contains(type)) {
					value = 0;
				} else if (CASTLE_TILES.contains(type)) {
					value = 2;
				} else {
					value = -1;
				}

				// Expand to 64x64 area
				for (int dy = 0; dy < TILE_SIZE; dy++) {
					for (int dx = 0; dx < TILE_SIZE; dx++) {
						expandedGrid[y * TILE_SIZE + dy][x * TILE_SIZE + dx] = value;
					}
				}
			}
		}

		// Optional: mark border path tiles as spawn points (value 3)
		for (int x = 0; x < pixelWidth; x++) {
			if (expandedGrid[0][x] == 0) expandedGrid[0][x] = 3;
			if (expandedGrid[pixelHeight - 1][x] == 0) expandedGrid[pixelHeight - 1][x] = 3;
		}
		for (int y = 0; y < pixelHeight; y++) {
			if (expandedGrid[y][0] == 0) expandedGrid[y][0] = 3;
			if (expandedGrid[y][pixelWidth - 1] == 0) expandedGrid[y][pixelWidth - 1] = 3;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public TileModel getTile(int x, int y) {
		return getTile(new Point(x, y));
	}

	public TileModel getTile(Point point) {
		if (point.x() >= 0 && point.x() < width && point.y() >= 0 && point.y() < height) {
			return tiles.get(point);
		} else {
			throw new IllegalArgumentException("Tile x or y out of map bounds");
		}
	}

	public void printExpandedGrid() {
		if (expandedGrid == null) {
			System.out.println("Expanded grid is not initialized.");
			return;
		}

		for (int y = 0; y < expandedGrid.length; y++) {
			StringBuilder row = new StringBuilder();
			for (int x = 0; x < expandedGrid[0].length; x++) {
				row.append(String.format("%2d ", expandedGrid[y][x]));
			}
			System.out.println(row);
		}
	}

	public void setTile(int x, int y, TileModel tileModel) {
		setTile(new Point(x, y), tileModel);
	}

	public void setTile(Point point, TileModel tileModel) {
		if (point.x() >= 0 && point.x() < width && point.y() >= 0 && point.y() < height) {
			tiles.put(point, tileModel);
		} else {
			throw new IllegalArgumentException("Tile x or y out of map bounds");
		}
	}

	public ObservableMap<Point, TileModel> getTiles() {
		return tiles;
	}

	public ObservableList<Entity> getEntities() {
		return entities;
	}

	public int[][] getExpandedGrid() {
		return expandedGrid;
	}
}
