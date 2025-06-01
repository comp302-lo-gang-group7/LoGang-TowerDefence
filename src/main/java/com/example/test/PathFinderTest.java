package com.example.test;

import com.example.utils.PathFinder;
import com.example.utils.Point;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class PathFinderTest {

    @Test
    public void testStartEqualsGoal() {
        int[][] grid= {{96}};
        Point start= new Point(0, 0);
        Point goal= new Point(0, 0);

        List<Point> path= PathFinder.findPath(grid, start, goal);

        assertNotNull(path, "Path should not be null");
        assertEquals(1, path.size(), "Path should contain exactly one point when start equals goal");
        assertEquals(start, path.get(0), "The single point in the path should be the start/goal");
    }

    @Test
    public void testUnreachableGoal() {
        int[][] grid={
                {64, 0, 64},
                {64, 0, 64},
                {64, 0, 64}
        };
        Point start= new Point(0, 1);
        Point goal= new Point(2, 1);

        List<Point> path= PathFinder.findPath(grid, start, goal);

        assertNotNull(path, "Path should not be null even if unreachable");
        assertTrue(path.isEmpty(), "Path should be empty when the goal is unreachable");
    }

    @Test
    public void testSimpleStraightLinePath() {
        int[][] grid= {{64, 64, 64, 64}};
        Point start= new Point(0, 0);
        Point goal= new Point(3, 0);

        List<Point> path= PathFinder.findPath(grid, start, goal);

        assertNotNull(path, "Path should not be null");
        assertEquals(4, path.size(), "Path length should match number of tiles in straight line");
        for (int i=0; i<4; i++) {
            assertEquals(new Point(i, 0), path.get(i), "Path should step serially from start to goal");
        }
    }

    @Test
    public void testTrimmingOnGoalWeightAbundance() {
        final int length=200;
        int[][] grid= new int[1][length];
        for (int i=0; i<length; i++){
            grid[0][i]=96;
        }
        Point start= new Point(0, 0);
        Point goal= new Point(length-1, 0);

        List<Point> path= PathFinder.findPath(grid, start, goal);

        assertNotNull(path, "Path should not be null");
        assertFalse(path.isEmpty(), "Path should not be empty when a path exists");
        assertEquals(start, path.get(0), "Path should start at the given start point");
        assertTrue(path.size()<=100, "Path should be trimmed to at most 100 GOAL_WEIGHT steps");
    }
}
