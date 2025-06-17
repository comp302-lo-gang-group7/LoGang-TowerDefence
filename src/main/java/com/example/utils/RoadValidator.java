package com.example.utils;

import com.example.map.TileEnum;
import com.example.map.TileView;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;


/**
 * Class RoadValidator
 */
public class RoadValidator {
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;


    /**
     * TODO
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
     * TODO
     */
    public static List<Point2D> findIsolatedTowerTiles(TileView[][] mapTileViews) {
        List<Point2D> isolatedTowerTiles = new ArrayList<>();
        int rows = mapTileViews.length;
        int cols = mapTileViews[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TileEnum tileType = mapTileViews[row][col].getType();

                if (tileType == TileEnum.EMPTY_TOWER_TILE) {
                    boolean hasAdjacentPath = false;


                    if (row > 0 && isRoadTile(mapTileViews[row-1][col].getType())) {
                        hasAdjacentPath = true;
                    } else if (col < cols-1 && isRoadTile(mapTileViews[row][col+1].getType())) {
                        hasAdjacentPath = true;
                    } else if (row < rows-1 && isRoadTile(mapTileViews[row+1][col].getType())) {
                        hasAdjacentPath = true;
                    } else if (col > 0 && isRoadTile(mapTileViews[row][col-1].getType())) {
                        hasAdjacentPath = true;
                    }

                    if (!hasAdjacentPath) {
                        isolatedTowerTiles.add(new Point2D(col, row));
                    }
                }
            }
        }

        return isolatedTowerTiles;
    }


    /**
     * TODO
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
     * TODO
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
     * TODO
     */
    public static boolean isRoadTile(TileEnum tileType) {
        String name = tileType.name();
        return name.contains("PATH") || name.contains("CORNER");
    }
}