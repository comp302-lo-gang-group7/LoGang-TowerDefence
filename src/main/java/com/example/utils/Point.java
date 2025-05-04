package com.example.utils;

public record Point( int x, int y )
{
    @Override
    public String toString()
    {
        return String.format("{%s, %s}", x, y);
    }
}
