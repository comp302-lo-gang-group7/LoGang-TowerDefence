package com.example.utils;

import java.util.*;

/**
 * Utility for computing paths over the expanded grid produced by
 * {@link com.example.map.GameMap}. Implements a Dijkstra style search with
 * small random noise to vary routes.
 */
public class PathFinder {
    private static final int[] DX = { 0, 1, 1, 1, 0, -1, -1, -1 };
    private static final int[] DY = { -1, -1, 0, 1, 1, 1, 0, -1 };

    private static final int TILE_SIZE    = 64;
    private static final int PEAK_WEIGHT  = TILE_SIZE / 2;       // 32
    private static final int SPAWN_WEIGHT = PEAK_WEIGHT * 2;     // 64
    private static final int GOAL_WEIGHT  = PEAK_WEIGHT * 3;     // 96

    private static final Random random = new Random();
    /**
     * requires:
     *   – grid is non-null, rectangular (all rows same length), and contains only integer weights;
     *   – start and goal are non-null and lie within the bounds of grid;
     *   – positive weights denote traversable tiles, zero or negative weights denote obstacles.
     *
     * modifies:
     *   – internal priority queue, distance and predecessor arrays, and the static Random seed;
     *
     * effects:
     *   – Runs a Dijkstra/A*‐style search (with small random noise) from start to goal over 4-connected neighbors.
     *   – Returns a (possibly empty) List<Point> “trimmed” to at most 20–100 visits of GOAL_WEIGHT tiles:
     *       • if goal is reachable, the returned list begins at start, follows a valid 4-connected path through
     *         only positive-weight tiles, and ends at the goal or earlier if too many GOAL_WEIGHT tiles appear;
     *       • if goal is unreachable, returns an empty list (prev[goal] == null).
     */
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
            int[] dirs = {0, 1, 2, 3, 4, 5, 6, 7};
            shuffleArray(dirs);

            for (int i = 0; i < 8; i++) {
                int dir = dirs[i];
                int nx = cx + DX[dir], ny = cy + DY[dir];
                if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue;
                int weight = grid[ny][nx];
                if (weight <= 0) continue;

                // Add slight noise to encourage variation in paths
                double noise = random.nextDouble() * 5; // tweak range as needed
                double stepCost = (GOAL_WEIGHT - weight) + noise;
                if (Math.abs(DX[dir]) + Math.abs(DY[dir]) == 2) {
                    stepCost *= 1.4; // approx sqrt(2) for diagonals
                }
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

    /** Shuffle an int array in-place using Fisher-Yates. */
    private static void shuffleArray(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }

    /**
     * Choose a random spawn location marked by the {@code SPAWN_WEIGHT} on the grid edges.
     */
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

    /** Locate the castle goal within the grid. */
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
