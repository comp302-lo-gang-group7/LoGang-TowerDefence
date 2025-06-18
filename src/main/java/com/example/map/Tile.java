package com.example.map;

import javafx.scene.control.Label;

/**
 * Represents a tile in the game map. A tile consists of a visual representation,
 * a model containing its data, and an optional label indicating its level.
 */
public class Tile {

    /**
     * The visual representation of the tile.
     */
    public TileView view;

    /**
     * The model containing the data of the tile.
     */
    public TileModel model;

    /**
     * An optional label indicating the level of the tile.
     */
    public Label levelLabel;

    /**
     * Constructs a Tile object with the specified view and model.
     * The level label is initialized to null.
     *
     * @param view  The visual representation of the tile.
     * @param model The model containing the data of the tile.
     */
    public Tile(TileView view, TileModel model) {
        this.view = view;
        this.model = model;
        this.levelLabel = null;
    }
}

