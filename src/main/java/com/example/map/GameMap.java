package com.example.map;

import com.example.utils.Point;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.HashSet;
import java.util.Set;

import java.util.Set;

public class GameMap {
	private final int width, height;
	private final ObservableMap<Point, TileModel> tiles = FXCollections.observableHashMap();
	private final ObservableList<Entity> entities = FXCollections.observableArrayList();
	private int[][] expandedGrid;

	private static final int TILE_SIZE = 64;
	// peak weight at path center (half tile)
	private static final int PEAK_WEIGHT  = TILE_SIZE / 2;       // 32
	private static final int SPAWN_WEIGHT = PEAK_WEIGHT * 2;     // 64
	private static final int GOAL_WEIGHT  = PEAK_WEIGHT * 3;     // 96

	// Walkable path enums
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

	// Castle tile enums
	private static final Set<TileEnum> CASTLE_TILES = Set.of(
			TileEnum.CASTLE_TOP_LEFT,
			TileEnum.CASTLE_TOP_RIGHT,
			TileEnum.CASTLE_BOTTOM_LEFT,
			TileEnum.CASTLE_BOTTOM_RIGHT
	);

	/**
	 * Builds a heat-map grid for pathfinding:
	 * - Path tiles: weights taper from center (max PEAK_WEIGHT) to 1 at edges.
	 * - Castle tiles: weights grow downward from top (1) to max at lower-center.
	 * - Obstacles: -1
	 * - Spawn: any positive weight border pixel is set to PEAK_WEIGHT
	 * - Goal: each castle tile's lower-center pixel set to PEAK_WEIGHT
	 */
	public GameMap(TileView[][] tileViews) {
		this.height = tileViews.length;
		this.width  = tileViews[0].length;
		int pixelW = width * TILE_SIZE;
		int pixelH = height * TILE_SIZE;
		expandedGrid = new int[pixelH][pixelW];

		// 1) Compute base heat values
		for (int ty = 0; ty < height; ty++) {
			for (int tx = 0; tx < width; tx++) {
				TileEnum type = tileViews[ty][tx].getType();
				int originX = tx * TILE_SIZE;
				int originY = ty * TILE_SIZE;

				if (PATH_TILES.contains(type)) {
					int mid = TILE_SIZE / 2;  // 32 for a 64×64 tile

					// 1) Draw the center row at peak weight
					for (int dx = 0; dx < TILE_SIZE; dx++) {
						expandedGrid[originY + mid][originX + dx] = PEAK_WEIGHT;
					}
					// 2) Draw the center column at peak weight
					for (int dy = 0; dy < TILE_SIZE; dy++) {
						expandedGrid[originY + dy][originX + mid] = PEAK_WEIGHT;
					}

					// 3) Optionally fill the rest of the tile's walkable area with a minimal weight (e.g., 1)
					for (int dy = 0; dy < TILE_SIZE; dy++) {
						for (int dx = 0; dx < TILE_SIZE; dx++) {
							int y = originY + dy, x = originX + dx;
							if (expandedGrid[y][x] == 0) {       // untouched
								expandedGrid[y][x] = 1;          // bare‐minimum walkable
							}
						}
					}
				} else if (CASTLE_TILES.contains(type)) {
					// vertical gradient: deeper (higher dy) => larger weight
					double centerX = (TILE_SIZE - 1) / 2.0;
					for (int dy = 0; dy < TILE_SIZE; dy++) {
						for (int dx = 0; dx < TILE_SIZE; dx++) {
							double weightX = Math.max(0, GOAL_WEIGHT - Math.abs(dx - centerX));
							double factorY = dy / (double)(TILE_SIZE - 1);
							int w = (int)Math.max(1, Math.round(weightX * factorY));
							expandedGrid[originY + dy][originX + dx] = w;
						}
					}
				} else {
					// obstacle
					for (int dy = 0; dy < TILE_SIZE; dy++) {
						for (int dx = 0; dx < TILE_SIZE; dx++) {
							expandedGrid[originY + dy][originX + dx] = -1;
						}
					}
				}
			}
		}

		// 2) Mark spawn points on any border pixel that has positive weight
		for (int x = 0; x < pixelW; x++) {
			if (expandedGrid[0][x] > 0) {
				expandedGrid[0][x] = SPAWN_WEIGHT;
			};
			if (expandedGrid[pixelH - 1][x] > 0) {
				expandedGrid[pixelH - 1][x] = SPAWN_WEIGHT;
			};
		}
		for (int y = 0; y < pixelH; y++) {
			if (expandedGrid[y][0] > 0) {
				expandedGrid[y][0] = SPAWN_WEIGHT;
			};
			if (expandedGrid[y][pixelW - 1] > 0) {
				expandedGrid[y][pixelW - 1] = SPAWN_WEIGHT;
			};
		}

		// 3) Mark goal at lower-center of each castle tile
		for (int ty = 0; ty < height; ty++) {
			for (int tx = 0; tx < width; tx++) {
				if (CASTLE_TILES.contains(tileViews[ty][tx].getType())) {
					int cx = tx * TILE_SIZE + TILE_SIZE/2;
					int cy = ty * TILE_SIZE + (int)(TILE_SIZE * 0.75);
					expandedGrid[cy][cx] = GOAL_WEIGHT;
				}
			}
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
