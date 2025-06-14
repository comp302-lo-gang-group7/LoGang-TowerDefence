package com.example.utils;

import com.example.map.TileEnum;
import com.example.map.TileView;
import javafx.geometry.Point2D;
import java.util.*;

/**
 * Utility class for validating a game map to ensure it meets all requirements for gameplay.
 */
public class MapValidator {

    /**
     * Validates the game map by checking for disconnected roads, start and end points, path connectivity,
     * and sufficient buildable tower tiles.
     *
     * @param mapTileViews The map grid represented as a 2D array of TileView objects.
     * @return A ValidationResult object containing the validation status and error messages.
     */
    public static ValidationResult validateMap(TileView[][] mapTileViews) {
        ValidationResult result = new ValidationResult();

        List<Point2D> disconnectedRoads = RoadValidator.findDisconnectedRoads(mapTileViews);
        if (!disconnectedRoads.isEmpty()) {
            result.addError("Map contains disconnected road tiles that don't connect properly.");
            result.setDisconnectedTiles(disconnectedRoads);
            return result;
        }

        List<Point2D> startPoints = findStartPoints(mapTileViews);
        if (startPoints.isEmpty()) {
            result.addError("Missing start point! Add a path endpoint at the edge of the map.");
        }

        List<Point2D> endPoints = findEndPoints(mapTileViews);
        if (endPoints.isEmpty()) {
            result.addError("Missing end point! Add a path endpoint at the edge of the map.");
        }

        if (!startPoints.isEmpty() && !endPoints.isEmpty() && !isPathConnected(mapTileViews, startPoints, endPoints)) {
            result.addError("Path is not fully connected! Ensure there's a complete path from start to end.");
        }

        int emptyLots = countEmptyLots(mapTileViews);
        if (emptyLots < 4) {
            result.addError("Not enough tower spots! Add at least " + (4 - emptyLots) + " more empty tower lots.");
        }

        return result;
    }

    /**
     * Finds all potential start points located at the edges of the map.
     *
     * @param mapTileViews The map grid represented as a 2D array of TileView objects.
     * @return A list of Point2D objects representing the start points.
     */
    private static List<Point2D> findStartPoints(TileView[][] mapTileViews) {
        List<Point2D> startPoints = new ArrayList<>();
        int rows = mapTileViews.length;
        int cols = mapTileViews[0].length;

        for (int c = 0; c < cols; c++) {
            if (isPathEndpoint(mapTileViews[0][c].getType()) &&
                hasPathConnection(mapTileViews[0][c].getType(), 2)) {
                startPoints.add(new Point2D(c, 0));
            }
        }

        for (int c = 0; c < cols; c++) {
            if (isPathEndpoint(mapTileViews[rows - 1][c].getType()) &&
                hasPathConnection(mapTileViews[rows - 1][c].getType(), 0)) {
                startPoints.add(new Point2D(c, rows - 1));
            }
        }

        for (int r = 0; r < rows; r++) {
            if (isPathEndpoint(mapTileViews[r][0].getType()) &&
                hasPathConnection(mapTileViews[r][0].getType(), 1)) {
                startPoints.add(new Point2D(0, r));
            }
        }

        for (int r = 0; r < rows; r++) {
            if (isPathEndpoint(mapTileViews[r][cols - 1].getType()) &&
                hasPathConnection(mapTileViews[r][cols - 1].getType(), 3)) {
                startPoints.add(new Point2D(cols - 1, r));
            }
        }

        return startPoints;
    }

    /**
     * Finds all potential end points located at the edges of the map.
     *
     * @param mapTileViews The map grid represented as a 2D array of TileView objects.
     * @return A list of Point2D objects representing the end points.
     */
    private static List<Point2D> findEndPoints(TileView[][] mapTileViews) {
        List<Point2D> endPoints = new ArrayList<>();
        int rows = mapTileViews.length;
        int cols = mapTileViews[0].length;

        List<Point2D> allEndpoints = new ArrayList<>();

        for (int c = 0; c < cols; c++) {
            if (isPathEndpoint(mapTileViews[0][c].getType()) &&
                hasPathConnection(mapTileViews[0][c].getType(), 2)) {
                allEndpoints.add(new Point2D(c, 0));
            }
        }

        for (int c = 0; c < cols; c++) {
            if (isPathEndpoint(mapTileViews[rows - 1][c].getType()) &&
                hasPathConnection(mapTileViews[rows - 1][c].getType(), 0)) {
                allEndpoints.add(new Point2D(c, rows - 1));
            }
        }

        for (int r = 0; r < rows; r++) {
            if (isPathEndpoint(mapTileViews[r][0].getType()) &&
                hasPathConnection(mapTileViews[r][0].getType(), 1)) {
                allEndpoints.add(new Point2D(0, r));
            }
        }

        for (int r = 0; r < rows; r++) {
            if (isPathEndpoint(mapTileViews[r][cols - 1].getType()) &&
                hasPathConnection(mapTileViews[r][cols - 1].getType(), 3)) {
                allEndpoints.add(new Point2D(cols - 1, r));
            }
        }

        if (allEndpoints.size() >= 2) {
            for (int i = 0; i < allEndpoints.size(); i++) {
                if (i > 0) {
                    endPoints.add(allEndpoints.get(i));
                }
            }
        }

        return endPoints;
    }

    /**
     * Checks if a valid path exists between any start point and end point.
     *
     * @param mapTileViews The map grid represented as a 2D array of TileView objects.
     * @param startPoints  A list of start points.
     * @param endPoints    A list of end points.
     * @return True if a valid path exists, false otherwise.
     */
    private static boolean isPathConnected(TileView[][] mapTileViews, List<Point2D> startPoints, List<Point2D> endPoints) {
        int rows = mapTileViews.length;
        int cols = mapTileViews[0].length;

        int[][] grid = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                TileEnum type = mapTileViews[r][c].getType();
                grid[r][c] = isRoadTile(type) ? 1 : 0;
            }
        }

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
     * Performs a breadth-first search (BFS) to check if a path exists between start and end points.
     *
     * @param grid  The grid representation of the map.
     * @param start The starting point.
     * @param end   The ending point.
     * @return True if a path exists, false otherwise.
     */
    private static boolean hasPath(int[][] grid, Point2D start, Point2D end) {
        int rows = grid.length;
        int cols = grid[0].length;

        boolean[][] visited = new boolean[rows][cols];
        Queue<Point2D> queue = new LinkedList<>();

        int startX = (int) start.getX();
        int startY = (int) start.getY();
        int endX = (int) end.getX();
        int endY = (int) end.getY();

        queue.add(new Point2D(startX, startY));
        visited[startY][startX] = true;

        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};

        while (!queue.isEmpty()) {
            Point2D current = queue.poll();
            int x = (int) current.getX();
            int y = (int) current.getY();

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
     * Counts the number of empty lots available for tower building.
     *
     * @param mapTileViews The map grid represented as a 2D array of TileView objects.
     * @return The number of empty lots.
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
     * Determines if a tile is a road tile.
     *
     * @param tileType The type of the tile.
     * @return True if the tile is a road tile, false otherwise.
     */
    private static boolean isRoadTile(TileEnum tileType) {
        String name = tileType.name();
        return name.contains("PATH") || name.contains("CORNER");
    }

    /**
     * Determines if a tile is a path endpoint (end tiles at the map edge).
     *
     * @param tileType The type of the tile.
     * @return True if the tile is a path endpoint, false otherwise.
     */
    private static boolean isPathEndpoint(TileEnum tileType) {
        return tileType == TileEnum.HORIZONTAL_LEFT_PATH_END ||
               tileType == TileEnum.HORIZONTAL_RIGHT_PATH_END ||
               tileType == TileEnum.VERTICAL_UPPER_PATH_END ||
               tileType == TileEnum.VERTICAL_LOWER_PATH_END;
    }

    /**
     * Checks if a tile has a connection in the specified direction.
     *
     * @param tileType  The type of the tile.
     * @param direction The direction to check (0: UP, 1: RIGHT, 2: DOWN, 3: LEFT).
     * @return True if the tile has a connection in the specified direction, false otherwise.
     */
    private static boolean hasPathConnection(TileEnum tileType, int direction) {
        switch (tileType) {
            case HORIZONTAL_LEFT_PATH_END:
                return direction == 1;
            case HORIZONTAL_RIGHT_PATH_END:
                return direction == 3;
            case VERTICAL_UPPER_PATH_END:
                return direction == 2;
            case VERTICAL_LOWER_PATH_END:
                return direction == 0;
            default:
                return false;
        }
    }

    /**
     * Represents the result of a map validation, including validation status and error messages.
     */
    public static class ValidationResult {
        private boolean valid = true;
        private final List<String> errors = new ArrayList<>();
        private List<Point2D> disconnectedTiles = new ArrayList<>();

        /**
         * Adds an error message to the validation result.
         *
         * @param error The error message to add.
         */
        public void addError(String error) {
            valid = false;
            errors.add(error);
        }

        /**
         * Checks if the map is valid.
         *
         * @return True if the map is valid, false otherwise.
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Retrieves the list of error messages.
         *
         * @return A list of error messages.
         */
        public List<String> getErrors() {
            return errors;
        }

        /**
         * Retrieves the list of disconnected tiles.
         *
         * @return A list of disconnected tiles as Point2D objects.
         */
        public List<Point2D> getDisconnectedTiles() {
            return disconnectedTiles;
        }

        /**
         * Sets the list of disconnected tiles.
         *
         * @param disconnectedTiles A list of disconnected tiles as Point2D objects.
         */
        public void setDisconnectedTiles(List<Point2D> disconnectedTiles) {
            this.disconnectedTiles = disconnectedTiles;
        }

        /**
         * Retrieves the error messages as a single concatenated string.
         *
         * @return A string containing all error messages separated by newlines.
         */
        public String getErrorMessage() {
            return String.join("\n", errors);
        }
    }
}