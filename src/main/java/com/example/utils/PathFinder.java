package com.example.utils;

import java.util.*;

public class PathFinder {
    private static final int[] DX = {0, 1, 0, -1};
    private static final int[] DY = {-1, 0, 1, 0};

    private static final int TILE_SIZE    = 64;
    private static final int PEAK_WEIGHT  = TILE_SIZE / 2;       // 32
    private static final int SPAWN_WEIGHT = PEAK_WEIGHT * 2;     // 64
    private static final int GOAL_WEIGHT  = PEAK_WEIGHT * 3;     // 96

    private static final Random random = new Random();

    public static List<Point> findPath(int[][] grid, Point start, Point goal) {
        int h = grid.length, w = grid[0].length;
        double[][] dist = new double[h][w];
        Point[][] prev = new Point[h][w];
        for (int y = 0; y < h; y++) {
            Arrays.fill(dist[y], Double.POSITIVE_INFINITY);
        }

        PriorityQueue<Point> pq = new PriorityQueue<>(
                Comparator.comparingDouble(p -> dist[(int)p.y()][(int)p.x()])
        );
        dist[(int)start.y()][(int)start.x()] = 0;
        pq.add(start);

        while (!pq.isEmpty()) {
            Point cur = pq.poll();
            if (cur.equals(goal)) break;

            int cx = (int) cur.x(), cy = (int) cur.y();
            double baseCost = dist[cy][cx];

            // Randomize neighbor directions
            int[] dirs = {0, 1, 2, 3};
            shuffleArray(dirs);

            for (int i = 0; i < 4; i++) {
                int dir = dirs[i];
                int nx = cx + DX[dir], ny = cy + DY[dir];
                if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue;
                int weight = grid[ny][nx];
                if (weight <= 0) continue;

                // Add slight noise to encourage variation in paths
                double noise = random.nextDouble() * 5; // tweak range as needed
                double stepCost = (GOAL_WEIGHT - weight) + noise;
                double nd = baseCost + stepCost;

                if (nd < dist[ny][nx]) {
                    dist[ny][nx] = nd;
                    prev[ny][nx] = cur;
                    pq.add(new Point(nx, ny));
                }
            }
        }

        // Reconstruct path
        List<Point> fullPath = new ArrayList<>();
        if (prev[(int)goal.y()][(int)goal.x()] != null || start.equals(goal)) {
            for (Point at = goal; at != null; at = prev[(int)at.y()][(int)at.x()]) {
                fullPath.add(at);
            }
            Collections.reverse(fullPath);
        }

        // Trim after a certain number of goal-weight steps
        int maxGoalSteps = 20 + random.nextInt(81); // 20–100
        List<Point> trimmed = new ArrayList<>();
        int goalCount = 0;
        for (Point p : fullPath) {
            trimmed.add(p);
            if (grid[(int) p.y()][(int) p.x()] == GOAL_WEIGHT) {
                goalCount++;
                if (goalCount >= maxGoalSteps) break;
            }
        }

        return trimmed;
    }

    // Shuffles an int array (Fisher-Yates)
    private static void shuffleArray(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }

    public static Point findRandomSpawnPoint(int[][] grid) {
        List<Point> candidates = new ArrayList<>();
        int h = grid.length, w = grid[0].length;

        for (int x = 0; x < w; x++) {
            if (grid[0][x] == SPAWN_WEIGHT) candidates.add(new Point(x, 0));
            if (grid[h - 1][x] == SPAWN_WEIGHT) candidates.add(new Point(x, h - 1));
        }
        for (int y = 0; y < h; y++) {
            if (grid[y][0] == SPAWN_WEIGHT) candidates.add(new Point(0, y));
            if (grid[y][w - 1] == SPAWN_WEIGHT) candidates.add(new Point(w - 1, y));
        }

        if (candidates.isEmpty()) throw new IllegalStateException("No spawn point found");
        return candidates.get(random.nextInt(candidates.size()));
    }

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
