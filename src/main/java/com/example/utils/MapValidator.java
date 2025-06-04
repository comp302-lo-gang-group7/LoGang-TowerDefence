package com.example.utils;

import com.example.map.TileEnum;
import com.example.map.TileView;
import javafx.geometry.Point2D;
import java.util.*;

public class MapValidator {
    
    /**
     * Validates if the map meets all requirements for gameplay
     * @param mapTileViews The map grid
     * @return A ValidationResult object containing validation status and error messages
     */
    public static ValidationResult validateMap(TileView[][] mapTileViews) {
        ValidationResult result = new ValidationResult();
        
        // Check for disconnected roads
        List<Point2D> disconnectedRoads = RoadValidator.findDisconnectedRoads(mapTileViews);
        if (!disconnectedRoads.isEmpty()) {
            result.addError("Map contains disconnected road tiles that don't connect properly.");
            result.setDisconnectedTiles(disconnectedRoads);
            return result; // Stop validation if roads are disconnected
        }
        
        // Check for start point at map edge
        List<Point2D> startPoints = findStartPoints(mapTileViews);
        if (startPoints.isEmpty()) {
            result.addError("Missing start point! Add a path endpoint at the edge of the map.");
        }
        
        // Check for end point at map edge
        List<Point2D> endPoints = findEndPoints(mapTileViews);
        if (endPoints.isEmpty()) {
            result.addError("Missing end point! Add a path endpoint at the edge of the map.");
        }
        
        // Check path connectivity from start to end
        if (!startPoints.isEmpty() && !endPoints.isEmpty() && !isPathConnected(mapTileViews, startPoints, endPoints)) {
            result.addError("Path is not fully connected! Ensure there's a complete path from start to end.");
        }
        
        // Check for enough buildable tower tiles
        int emptyLots = countEmptyLots(mapTileViews);
        if (emptyLots < 4) {
            result.addError("Not enough tower spots! Add at least " + (4 - emptyLots) + " more empty tower lots.");
        }
        
        return result;
    }
    
    /**
     * Finds all potential start points at the edges of the map
     */
    private static List<Point2D> findStartPoints(TileView[][] mapTileViews) {
        List<Point2D> startPoints = new ArrayList<>();
        int rows = mapTileViews.length;
        int cols = mapTileViews[0].length;
        
        // Check top edge
        for (int c = 0; c < cols; c++) {
            if (isPathEndpoint(mapTileViews[0][c].getType()) && 
                hasPathConnection(mapTileViews[0][c].getType(), 2)) { // DOWN connection
                startPoints.add(new Point2D(c, 0));
            }
        }
        
        // Check bottom edge
        for (int c = 0; c < cols; c++) {
            if (isPathEndpoint(mapTileViews[rows-1][c].getType()) && 
                hasPathConnection(mapTileViews[rows-1][c].getType(), 0)) { // UP connection
                startPoints.add(new Point2D(c, rows-1));
            }
        }
        
        // Check left edge
        for (int r = 0; r < rows; r++) {
            if (isPathEndpoint(mapTileViews[r][0].getType()) && 
                hasPathConnection(mapTileViews[r][0].getType(), 1)) { // RIGHT connection
                startPoints.add(new Point2D(0, r));
            }
        }
        
        // Check right edge
        for (int r = 0; r < rows; r++) {
            if (isPathEndpoint(mapTileViews[r][cols-1].getType()) && 
                hasPathConnection(mapTileViews[r][cols-1].getType(), 3)) { // LEFT connection
                startPoints.add(new Point2D(cols-1, r));
            }
        }
        
        return startPoints;
    }
    
    /**
     * Finds all potential end points at the edges of the map
     */
    private static List<Point2D> findEndPoints(TileView[][] mapTileViews) {
        List<Point2D> endPoints = new ArrayList<>();
        int rows = mapTileViews.length;
        int cols = mapTileViews[0].length;
        
        // Check all edges similar to start points
        // Path endpoints could serve as either starts or ends depending on game flow
        // The distinction between start/end is often made at runtime
        
        // For this validation, we'll find all potential endpoints (same as startPoints)
        // but exclude any that are already identified as start points
        List<Point2D> allEndpoints = new ArrayList<>();
        
        // Check top edge
        for (int c = 0; c < cols; c++) {
            if (isPathEndpoint(mapTileViews[0][c].getType()) && 
                hasPathConnection(mapTileViews[0][c].getType(), 2)) { // DOWN connection
                allEndpoints.add(new Point2D(c, 0));
            }
        }
        
        // Check bottom edge
        for (int c = 0; c < cols; c++) {
            if (isPathEndpoint(mapTileViews[rows-1][c].getType()) && 
                hasPathConnection(mapTileViews[rows-1][c].getType(), 0)) { // UP connection
                allEndpoints.add(new Point2D(c, rows-1));
            }
        }
        
        // Check left edge
        for (int r = 0; r < rows; r++) {
            if (isPathEndpoint(mapTileViews[r][0].getType()) && 
                hasPathConnection(mapTileViews[r][0].getType(), 1)) { // RIGHT connection
                allEndpoints.add(new Point2D(0, r));
            }
        }
        
        // Check right edge
        for (int r = 0; r < rows; r++) {
            if (isPathEndpoint(mapTileViews[r][cols-1].getType()) && 
                hasPathConnection(mapTileViews[r][cols-1].getType(), 3)) { // LEFT connection
                allEndpoints.add(new Point2D(cols-1, r));
            }
        }
        
        // Need at least 2 endpoints for start and end
        if (allEndpoints.size() >= 2) {
            // Just return a different endpoint to ensure we have both start and end
            for (int i = 0; i < allEndpoints.size(); i++) {
                if (i > 0) {
                    endPoints.add(allEndpoints.get(i));
                }
            }
        }
        
        return endPoints;
    }
    
    /**
     * Checks if a valid path exists between any start point and end point
     */
    private static boolean isPathConnected(TileView[][] mapTileViews, List<Point2D> startPoints, List<Point2D> endPoints) {
        int rows = mapTileViews.length;
        int cols = mapTileViews[0].length;
        
        // Create a grid representation for path finding
        int[][] grid = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                TileEnum type = mapTileViews[r][c].getType();
                grid[r][c] = isRoadTile(type) ? 1 : 0;
            }
        }
        
        // Check if any start point can reach any end point
        for (Point2D start : startPoints) {
            for (Point2D end : endPoints) {
                if (hasPath(grid, start, end)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Simple BFS to check if a path exists between start and end points
     */
    private static boolean hasPath(int[][] grid, Point2D start, Point2D end) {
        int rows = grid.length;
        int cols = grid[0].length;
        
        boolean[][] visited = new boolean[rows][cols];
        Queue<Point2D> queue = new LinkedList<>();
        
        int startX = (int)start.getX();
        int startY = (int)start.getY();
        int endX = (int)end.getX();
        int endY = (int)end.getY();
        
        queue.add(new Point2D(startX, startY));
        visited[startY][startX] = true;
        
        int[] dx = {0, 1, 0, -1}; // UP, RIGHT, DOWN, LEFT
        int[] dy = {-1, 0, 1, 0};
        
        while (!queue.isEmpty()) {
            Point2D current = queue.poll();
            int x = (int)current.getX();
            int y = (int)current.getY();
            
            if (x == endX && y == endY) {
                return true;
            }
            
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                
                if (nx >= 0 && nx < cols && ny >= 0 && ny < rows && 
                    grid[ny][nx] == 1 && !visited[ny][nx]) {
                    visited[ny][nx] = true;
                    queue.add(new Point2D(nx, ny));
                }
            }
        }
        
        return false;
    }
    
    /**
     * Counts the number of empty lots available for tower building
     */
    private static int countEmptyLots(TileView[][] mapTileViews) {
        int count = 0;
        
        for (TileView[] row : mapTileViews) {
            for (TileView tile : row) {
                if (tile.getType() == TileEnum.EMPTY_TOWER_TILE) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    /**
     * Determines if a tile is a road tile
     */
    private static boolean isRoadTile(TileEnum tileType) {
        String name = tileType.name();
        return name.contains("PATH") || name.contains("CORNER");
    }
    
    /**
     * Determines if a tile is a path endpoint (end tiles at map edge)
     */
    private static boolean isPathEndpoint(TileEnum tileType) {
        return tileType == TileEnum.HORIZONTAL_LEFT_PATH_END || 
               tileType == TileEnum.HORIZONTAL_RIGHT_PATH_END ||
               tileType == TileEnum.VERTICAL_UPPER_PATH_END ||
               tileType == TileEnum.VERTICAL_LOWER_PATH_END;
    }
    
    /**
     * Check if a tile has a connection in the specified direction
     * 0:UP, 1:RIGHT, 2:DOWN, 3:LEFT
     */
    private static boolean hasPathConnection(TileEnum tileType, int direction) {
        switch (tileType) {
            case HORIZONTAL_LEFT_PATH_END:
                return direction == 1; // RIGHT
            case HORIZONTAL_RIGHT_PATH_END:
                return direction == 3; // LEFT  
            case VERTICAL_UPPER_PATH_END:
                return direction == 2; // DOWN
            case VERTICAL_LOWER_PATH_END:
                return direction == 0; // UP
            default:
                return false;
        }
    }
    
    /**
     * Validation result class to store validation status and messages
     */
    public static class ValidationResult {
        private boolean valid = true;
        private final List<String> errors = new ArrayList<>();
        private List<Point2D> disconnectedTiles = new ArrayList<>();
        
        public void addError(String error) {
            valid = false;
            errors.add(error);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public List<Point2D> getDisconnectedTiles() {
            return disconnectedTiles;
        }
        
        public void setDisconnectedTiles(List<Point2D> disconnectedTiles) {
            this.disconnectedTiles = disconnectedTiles;
        }
        
        public String getErrorMessage() {
            return String.join("\n", errors);
        }
    }
}