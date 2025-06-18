package com.example.utils;

/**
 * Represents a point in a 2D coordinate system with x and y values.
 */
public record Point(int x, int y) {

    /**
     * Returns a string representation of the point in the format "{x, y}".
     *
     * @return A string representation of the point.
     */
    @Override
    public String toString() {
        return String.format("{%s, %s}", x, y);
    }
}
