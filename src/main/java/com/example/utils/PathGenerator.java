package com.example.utils;
import java.util.ArrayList;
import java.util.List;

public class PathGenerator {
    public static List<Point> lShapePath(Point start, Point end) {
        List<Point> path = new ArrayList<>();

        for (int x = start.x; x != end.x; x += Integer.compare(end.x, start.x)) {
            path.add(new Point(x, start.y));
        }

        for (int y = start.y; y != end.y; y += Integer.compare(end.y, start.y)) {
            path.add(new Point(end.x, y));
        }

        path.add(end);
        return path;
    }

    public static List<Point> straightPath(Point start, Point end) {
        List<Point> path = new ArrayList<>();
        int dx = Integer.compare(end.x, start.x);
        int dy = Integer.compare(end.y, start.y);
        int steps = Math.max(Math.abs(end.x - start.x), Math.abs(end.y - start.y));

        for (int i = 0; i <= steps; i++) {
            int x = start.x + i * dx;
            int y = start.y + i * dy;
            path.add(new Point(x, y));
        }

        return path;
    }

    public static List<Point> curvedPath(Point start, Point end) {
        List<Point> path = new ArrayList<>();
        int steps = 20;
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double oneMinusT = 1 - t;

            double midX = (start.x + end.x) / 2.0;
            double midY = (start.y + end.y) / 2.0 - 2; // offset for curve

            double x = oneMinusT * oneMinusT * start.x + 2 * oneMinusT * t * midX + t * t * end.x;
            double y = oneMinusT * oneMinusT * start.y + 2 * oneMinusT * t * midY + t * t * end.y;

            path.add(new Point((int)Math.round(x), (int)Math.round(y)));
        }

        return path;
    }
}
