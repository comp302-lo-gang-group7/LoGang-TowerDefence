package com.example.map;


/**
 * Class GameMap
 */
public class GameMap {
	private final int width, height;
	private int[][] expandedGrid;

	private static final int TILE_SIZE = 64;

	private static final int PEAK_WEIGHT  = TILE_SIZE / 2;
	private static final int SPAWN_WEIGHT = PEAK_WEIGHT * 2;
	private static final int GOAL_WEIGHT  = PEAK_WEIGHT * 3;



	/**
	 * TODO
	 */
	public GameMap(TileView[][] tileViews) {
		this.height = tileViews.length;
		this.width  = tileViews[0].length;
		int pixelW = width * TILE_SIZE;
		int pixelH = height * TILE_SIZE;
		expandedGrid = new int[pixelH][pixelW];


		for (int ty = 0; ty < height; ty++) {
			for (int tx = 0; tx < width; tx++) {
				TileEnum type = tileViews[ty][tx].getType();
				int originX = tx * TILE_SIZE;
				int originY = ty * TILE_SIZE;

				if (TileEnum.PATH_TILES.contains(type)) {
					int mid = TILE_SIZE / 2;


					for (int dx = 0; dx < TILE_SIZE; dx++) {
						expandedGrid[originY + mid][originX + dx] = PEAK_WEIGHT;
					}


					for (int dy = 0; dy < TILE_SIZE; dy++) {
						expandedGrid[originY + dy][originX + mid] = PEAK_WEIGHT;
					}


					for (int d = 0; d < TILE_SIZE; d++) {

						expandedGrid[originY + d][originX + d] = PEAK_WEIGHT;


						expandedGrid[originY + d][originX + (TILE_SIZE - 1 - d)] = PEAK_WEIGHT;
					}


					for (int dy = 0; dy < TILE_SIZE; dy++) {
						for (int dx = 0; dx < TILE_SIZE; dx++) {
							int y = originY + dy, x = originX + dx;
							if (expandedGrid[y][x] == 0) {
								expandedGrid[y][x] = 1;
							}
						}
					}
				} else if (TileEnum.CASTLE_TILES.contains(type)) {
					for (int dy = 0; dy < TILE_SIZE; dy++) {
						for (int dx = 0; dx < TILE_SIZE; dx++) {
							int y = originY + dy;
							int x = originX + dx;
							if (dy < TILE_SIZE / 2) {

								expandedGrid[y][x] = -1;
							} else {

								expandedGrid[y][x] = GOAL_WEIGHT;
							}
						}
					}
				} else {

					for (int dy = 0; dy < TILE_SIZE; dy++) {
						for (int dx = 0; dx < TILE_SIZE; dx++) {
							expandedGrid[originY + dy][originX + dx] = -1;
						}
					}
				}
			}
		}


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


		int minCx = width, minCy = height, maxCx = -1, maxCy = -1;

		for (int ty = 0; ty < height; ty++) {
			for (int tx = 0; tx < width; tx++) {
				if (TileEnum.CASTLE_TILES.contains(tileViews[ty][tx].getType())) {
					minCx = Math.min(minCx, tx);
					maxCx = Math.max(maxCx, tx);
					minCy = Math.min(minCy, ty);
					maxCy = Math.max(maxCy, ty);
				}
			}
		}
		if (minCx <= maxCx && minCy <= maxCy) {
			int castleX = minCx * TILE_SIZE;
			int castleY = minCy * TILE_SIZE;
			int castleW = (maxCx - minCx + 1) * TILE_SIZE;
			int castleH = (maxCy - minCy + 1) * TILE_SIZE;

			for (int dy = castleH/2; dy < castleH; dy++) {
				for (int dx = 0; dx < castleW; dx++) {
					expandedGrid[castleY + dy][castleX + dx] = GOAL_WEIGHT;
				}
			}
		}
	}


	/**
	 * TODO
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * TODO
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * TODO
	 */
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



	/**
	 * TODO
	 */
	public int[][] getExpandedGrid() {
		return expandedGrid;
	}
}
