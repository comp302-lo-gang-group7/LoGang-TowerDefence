package com.example.utils;

import com.example.map.TileEnum;
import com.example.map.TileView;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;

/**
 * Utility class for validating road connections in a tile-based map.
 * This class provides methods to identify disconnected road tiles and validate
 * connections between tiles based on their types and orientations.
 */
public class RoadValidator {
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;

    /**
     * Identifies and returns a list of road tiles that are disconnected from
     * their adjacent tiles in the given map.
     *
     * @param mapTileViews A 2D array of {@link TileView} representing the map.
     * @return A list of {@link Point2D} objects representing the coordinates of
     *         disconnected road tiles.
     */
    public static List<Point2D> findDisconnectedRoads(TileView[][] mapTileViews) {
        List<Point2D> disconnectedRoads = new ArrayList<>();
        int rows = mapTileViews.length;
        int cols = mapTileViews[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TileEnum tileType = mapTileViews[row][col].getType();

                if (!isRoadTile(tileType)) {
                    continue;
                }

                if (hasConnectionIn(tileType, UP) && row > 0) {
                    TileEnum upTile = mapTileViews[row - 1][col].getType();
                    if (!isValidConnection(tileType, UP, upTile)) {
                        disconnectedRoads.add(new Point2D(col, row));
                        continue;
                    }
                }

                if (hasConnectionIn(tileType, RIGHT) && col < cols - 1) {
                    TileEnum rightTile = mapTileViews[row][col + 1].getType();
                    if (!isValidConnection(tileType, RIGHT, rightTile)) {
                        disconnectedRoads.add(new Point2D(col, row));
                        continue;
                    }
                }

                if (hasConnectionIn(tileType, DOWN) && row < rows - 1) {
                    TileEnum downTile = mapTileViews[row + 1][col].getType();
                    if (!isValidConnection(tileType, DOWN, downTile)) {
                        disconnectedRoads.add(new Point2D(col, row));
                        continue;
                    }
                }

                if (hasConnectionIn(tileType, LEFT) && col > 0) {
                    TileEnum leftTile = mapTileViews[row][col - 1].getType();
                    if (!isValidConnection(tileType, LEFT, leftTile)) {
                        disconnectedRoads.add(new Point2D(col, row));
                    }
                }
            }
        }

        return disconnectedRoads;
    }

    /**
     * Determines if a tile can connect in the specified direction.
     *
     * @param tileType  The type of the tile.
     * @param direction The direction to check (UP, RIGHT, DOWN, LEFT).
     * @return {@code true} if the tile can connect in the specified direction,
     *         {@code false} otherwise.
     */
    private static boolean hasConnectionIn(TileEnum tileType, int direction) {
        String tileName = tileType.name();

        if (tileName.contains("TOP_LEFT") && tileName.contains("CORNER")) {
            return direction == RIGHT || direction == DOWN;
        } else if (tileName.contains("TOP_RIGHT") && tileName.contains("CORNER")) {
            return direction == LEFT || direction == DOWN;
        } else if (tileName.contains("BOTTOM_LEFT") && tileName.contains("CORNER")) {
            return direction == RIGHT || direction == UP;
        } else if (tileName.contains("BOTTOM_RIGHT") && tileName.contains("CORNER")) {
            return direction == LEFT || direction == UP;
        } else if (tileName.contains("HORIZONTAL") && !tileName.contains("END")) {
            return direction == LEFT || direction == RIGHT;
        } else if (tileName.contains("VERTICAL") && !tileName.contains("END")) {
            return direction == UP || direction == DOWN;
        } else if (tileName.contains("UP_CURVING")) {
            return direction == LEFT || direction == UP;
        } else if (tileName.contains("DOWN_CURVING")) {
            return direction == LEFT || direction == DOWN;
        } else if (tileName.contains("LEFT_CURVING")) {
            return direction == UP || direction == LEFT;
        } else if (tileName.contains("RIGHT_CURVING")) {
            return direction == UP || direction == RIGHT;
        } else if (tileName.contains("HORIZONTAL_LEFT") && tileName.contains("END")) {
            return direction == RIGHT;
        } else if (tileName.contains("HORIZONTAL_RIGHT") && tileName.contains("END")) {
            return direction == LEFT;
        } else if (tileName.contains("VERTICAL_UPPER") && tileName.contains("END")) {
            return direction == DOWN;
        } else if (tileName.contains("VERTICAL_LOWER") && tileName.contains("END")) {
            return direction == UP;
        }

        return false;
    }

    /**
     * Validates if a connection between two tiles is valid in the specified
     * direction.
     *
     * @param sourceTile The type of the source tile.
     * @param direction  The direction of the connection (UP, RIGHT, DOWN, LEFT).
     * @param targetTile The type of the target tile.
     * @return {@code true} if the connection is valid, {@code false} otherwise.
     */
    private static boolean isValidConnection(TileEnum sourceTile, int direction, TileEnum targetTile) {
        if (TileEnum.CASTLE_TILES.contains(targetTile)) {
            if (targetTile.name().contains("TOP_LEFT") && (direction == RIGHT || direction == DOWN)) {
                return true;
            } else if (targetTile.name().contains("TOP_RIGHT") && (direction == LEFT || direction == DOWN)) {
                return true;
            } else if (targetTile.name().contains("BOTTOM_LEFT") && (direction == RIGHT || direction == UP)) {
                return true;
            } else if (targetTile.name().contains("BOTTOM_RIGHT") && (direction == LEFT || direction == UP)) {
                return true;
            }
        }

        switch (direction) {
            case UP:
                return hasConnectionIn(targetTile, DOWN);
            case RIGHT:
                return hasConnectionIn(targetTile, LEFT);
            case DOWN:
                return hasConnectionIn(targetTile, UP);
            case LEFT:
                return hasConnectionIn(targetTile, RIGHT);
            default:
                return false;
        }
    }

    /**
     * Checks if the given tile is a road tile.
     *
     * @param tileType The type of the tile.
     * @return {@code true} if the tile is a road tile, {@code false} otherwise.
     */
    private static boolean isRoadTile(TileEnum tileType) {
        String name = tileType.name();
        return name.contains("PATH") || name.contains("CORNER");
    }
}