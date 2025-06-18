package com.example.config;

/**
 * Represents a level in the campaign map.
 */
public class CampaignLevel {
    /**
     * The name of the campaign level.
     */
    public String name;

    /**
     * A brief description of the campaign level.
     */
    public String description;

    /**
     * The file path or identifier for the icon representing the campaign level.
     */
    public String icon;

    /**
     * The file path to the level configuration or data file.
     */
    public String levelFile;

    /**
     * The column position of the campaign level on the map.
     */
    public int col;

    /**
     * The row position of the campaign level on the map.
     */
    public int row;
}