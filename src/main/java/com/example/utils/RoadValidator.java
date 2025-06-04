package com.example.utils;

import com.example.map.TileEnum;
import com.example.map.TileView;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
public class RoadValidator {
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    
    /**
     * Returns a list of disconnected road tiles in the map
     */
    public static List<Point2D> findDisconnectedRoads(TileView[][] mapTileViews) {
        List<Point2D> disconnectedRoads = new ArrayList<>();
        int rows = mapTileViews.length;
        int cols = mapTileViews[0].length;

        // Check each tile in the map
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TileEnum tileType = mapTileViews[row][col].getType();
                
                // Skip non-road tiles
                if (!isRoadTile(tileType)) {
                    continue;
                }
                
                // Check adjacent tiles for valid connections
                if (hasConnectionIn(tileType, UP) && row > 0) {
                    TileEnum upTile = mapTileViews[row-1][col].getType();
                    if (!isValidConnection(tileType, UP, upTile)) {
                        disconnectedRoads.add(new Point2D(col, row));
                        continue; // Skip checking other directions if already disconnected
                    }
                }
                
                if (hasConnectionIn(tileType, RIGHT) && col < cols - 1) {
                    TileEnum rightTile = mapTileViews[row][col+1].getType();
                    if (!isValidConnection(tileType, RIGHT, rightTile)) {
                        disconnectedRoads.add(new Point2D(col, row));
                        continue;
                    }
                }
                
                if (hasConnectionIn(tileType, DOWN) && row < rows - 1) {
                    TileEnum downTile = mapTileViews[row+1][col].getType();
                    if (!isValidConnection(tileType, DOWN, downTile)) {
                        disconnectedRoads.add(new Point2D(col, row));
                        continue;
                    }
                }
                
                if (hasConnectionIn(tileType, LEFT) && col > 0) {
                    TileEnum leftTile = mapTileViews[row][col-1].getType();
                    if (!isValidConnection(tileType, LEFT, leftTile)) {
                        disconnectedRoads.add(new Point2D(col, row));
                    }
                }
            }
        }
        
        return disconnectedRoads;
    }
    
    /**
     * Determines if a tile can connect in the specified direction
     */
    private static boolean hasConnectionIn(TileEnum tileType, int direction) {
        // Get enum name to compare
        String tileName = tileType.name();
        
        if (tileName.contains("TOP_LEFT") && tileName.contains("CORNER")) {
            return direction == RIGHT || direction == DOWN;
        }
        else if (tileName.contains("TOP_RIGHT") && tileName.contains("CORNER")) {
            return direction == LEFT || direction == DOWN;
        }
        else if (tileName.contains("BOTTOM_LEFT") && tileName.contains("CORNER")) {
            return direction == RIGHT || direction == UP;
        }
        else if (tileName.contains("BOTTOM_RIGHT") && tileName.contains("CORNER")) {
            return direction == LEFT || direction == UP;
        }
        else if (tileName.contains("HORIZONTAL") && !tileName.contains("END")) {
            return direction == LEFT || direction == RIGHT;
        }
        else if (tileName.contains("VERTICAL") && !tileName.contains("END")) {
            return direction == UP || direction == DOWN;
        }
        else if (tileName.contains("UP_CURVING")) {
            return direction == LEFT || direction == UP;
        }
        else if (tileName.contains("DOWN_CURVING")) {
            return direction == LEFT || direction == DOWN;
        }
        else if (tileName.contains("LEFT_CURVING")) {
            return direction == UP || direction == LEFT;
        }
        else if (tileName.contains("RIGHT_CURVING")) {
            return direction == UP || direction == RIGHT;
        }
        else if (tileName.contains("HORIZONTAL_LEFT") && tileName.contains("END")) {
            return direction == RIGHT;
        }
        else if (tileName.contains("HORIZONTAL_RIGHT") && tileName.contains("END")) {
            return direction == LEFT;
        }
        else if (tileName.contains("VERTICAL_UPPER") && tileName.contains("END")) {
            return direction == DOWN;
        }
        else if (tileName.contains("VERTICAL_LOWER") && tileName.contains("END")) {
            return direction == UP;
        }
        
        return false;
    }
    
    /**
     * Checks if a connection between two tiles is valid in the specified direction
     */
    private static boolean isValidConnection(TileEnum sourceTile, int direction, TileEnum targetTile) {
        // Check specific valid connections based on direction
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
     * Checks if the tile is a road tile
     */
    private static boolean isRoadTile(TileEnum tileType) {
        String name = tileType.name();
        return name.contains("PATH") || name.contains("CORNER");
    }
}