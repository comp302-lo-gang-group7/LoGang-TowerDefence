package com.example.utils;


import com.example.utils.Point;

import java.util.*;

public class PathFinder {
    private static final int[] DX = {0, 1, 0, -1}; // up, right, down, left
    private static final int[] DY = {-1, 0, 1, 0};

    public static List<Point> findPath(int[][] grid, Point start, Point goal) {
        int height = grid.length;
        int width = grid[0].length;
        boolean[][] visited = new boolean[height][width];
        Map<Point, Point> cameFrom = new HashMap<>();

        PriorityQueue<Point> queue = new PriorityQueue<>(Comparator.comparingDouble(p -> distance(p, goal)));
        queue.add(start);
        visited[start.y()][start.x()] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            for (int i = 0; i < 4; i++) {
                int nx = current.x() + DX[i];
                int ny = current.y() + DY[i];

                if (nx >= 0 && ny >= 0 && nx < width && ny < height &&
                        !visited[ny][nx] && grid[ny][nx] != -1) {
                    Point neighbor = new Point(nx, ny);
                    queue.add(neighbor);
                    visited[ny][nx] = true;
                    cameFrom.put(neighbor, current);
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private static List<Point> reconstructPath(Map<Point, Point> cameFrom, Point end) {
        List<Point> path = new ArrayList<>();
        Point current = end;
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    private static double distance(Point a, Point b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y()); // Manhattan distance
    }

    public static Point findSpawnPoint(int[][] grid) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == 3) {
                    return new Point(x, y);
                }
            }
        }
        throw new IllegalStateException("No spawn point found in grid.");
    }

    public static Point findCastlePoint(int[][] grid) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == 2) {
                    return new Point(x, y);
                }
            }
        }
        throw new IllegalStateException("No castle point found in grid.");
    }

}
