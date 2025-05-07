package com.example.utils;

import com.example.utils.Point;

import java.util.*;

public class PathFinder {
    private static final int[] DX = {0, 1, 0, -1};
    private static final int[] DY = {-1, 0, 1, 0};

    private static final int TILE_SIZE    = 64;
    private static final int PEAK_WEIGHT  = TILE_SIZE / 2;       // 32
    private static final int SPAWN_WEIGHT = PEAK_WEIGHT * 2;     // 64
    private static final int GOAL_WEIGHT  = PEAK_WEIGHT * 3;     // 96

    /**
     * Starting at spawn, at each step move to the neighbor with the highest weight,
     * stopping when you hit the GOAL_WEIGHT cell or can’t improve further.
     */
    public static List<Point> findPath(int[][] grid, Point start, Point goal) {
        int h = grid.length, w = grid[0].length;
        double[][] dist = new double[h][w];
        Point[][] prev = new Point[h][w];
        for (int y = 0; y < h; y++)
            Arrays.fill(dist[y], Double.POSITIVE_INFINITY);

        // Min-heap prioritized by lowest cumulative cost
        PriorityQueue<Point> pq = new PriorityQueue<>(
                Comparator.comparingDouble(p -> dist[p.y()][p.x()])
        );

        // Cost to enter the start cell is zero
        dist[start.y()][start.x()] = 0;
        pq.add(start);

        while (!pq.isEmpty()) {
            Point cur = pq.poll();
            if (cur.equals(goal)) break;

            int cx = cur.x(), cy = cur.y();
            double baseCost = dist[cy][cx];

            // Explore 4-neighbors
            for (int i = 0; i < 4; i++) {
                int nx = cx + DX[i], ny = cy + DY[i];
                if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue;
                int weight = grid[ny][nx];
                if (weight <= 0) continue;   // obstacle

                // Cost = how much we *lose* from the perfect path
                double stepCost = (GOAL_WEIGHT - weight);
                double nd = baseCost + stepCost;

                if (nd < dist[ny][nx]) {
                    dist[ny][nx] = nd;
                    prev[ny][nx] = cur;
                    pq.add(new Point(nx, ny));
                }
            }
        }

        // Reconstruct path if we reached goal
        if (prev[goal.y()][goal.x()] == null) return Collections.emptyList();
        List<Point> path = new ArrayList<>();
        for (Point at = goal; at != null; at = prev[at.y()][at.x()]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }


    /** Spawn detection unchanged **/
    public static Point findSpawnPoint(int[][] grid) {
        int h = grid.length, w = grid[0].length;
        for (int x = 0; x < w; x++) {
            if (grid[0][x] == SPAWN_WEIGHT)    return new Point(x, 0);
            if (grid[h-1][x] == SPAWN_WEIGHT) return new Point(x, h-1);
        }
        for (int y = 0; y < h; y++) {
            if (grid[y][0] == SPAWN_WEIGHT)    return new Point(0, y);
            if (grid[y][w-1] == SPAWN_WEIGHT) return new Point(w-1, y);
        }
        throw new IllegalStateException("No spawn point found");
    }

    /** Castle detection unchanged **/
    public static Point findCastlePoint(int[][] grid) {
        int h = grid.length, w = grid[0].length;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (grid[y][x] == GOAL_WEIGHT) return new Point(x, y);
            }
        }
        throw new IllegalStateException("No castle point found");
    }
}
