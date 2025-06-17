package com.example.utils;

import com.example.map.TileEnum;
import com.example.map.TileView;
import javafx.geometry.Point2D;
import java.util.*;


/**
 * Class MapValidator
 */
public class MapValidator {


    /**
     * TODO
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
     * TODO
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
     * TODO
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
     * TODO
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
     * TODO
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
     * TODO
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
     * TODO
     */
    private static boolean isRoadTile(TileEnum tileType) {
        String name = tileType.name();
        return name.contains("PATH") || name.contains("CORNER");
    }


    /**
     * TODO
     */
    private static boolean isPathEndpoint(TileEnum tileType) {
        return tileType == TileEnum.HORIZONTAL_LEFT_PATH_END ||
               tileType == TileEnum.HORIZONTAL_RIGHT_PATH_END ||
               tileType == TileEnum.VERTICAL_UPPER_PATH_END ||
               tileType == TileEnum.VERTICAL_LOWER_PATH_END;
    }


    /**
     * TODO
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


    public static class ValidationResult {
        private boolean valid = true;
        /**
         * TODO
         */
        private final List<String> errors = new ArrayList<>();
        /**
         * TODO
         */
        private List<Point2D> disconnectedTiles = new ArrayList<>();


        /**
         * TODO
         */
        public void addError(String error) {
            valid = false;
            errors.add(error);
        }


        /**
         * TODO
         */
        public boolean isValid() {
            return valid;
        }


        /**
         * TODO
         */
        public List<String> getErrors() {
            return errors;
        }


        /**
         * TODO
         */
        public List<Point2D> getDisconnectedTiles() {
            return disconnectedTiles;
        }


        /**
         * TODO
         */
        public void setDisconnectedTiles(List<Point2D> disconnectedTiles) {
            this.disconnectedTiles = disconnectedTiles;
        }


        /**
         * TODO
         */
        public String getErrorMessage() {
            return String.join("\n", errors);
        }
    }
}