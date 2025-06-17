package com.example.map;

import javafx.scene.control.Label;

/**
 * Class Tile
 */
public class Tile {
    public TileView view;
    public TileModel model;
    public Label levelLabel;

    /**
     * TODO
     */
    public Tile(TileView view, TileModel model) {
        this.view = view;
        this.model = model;
        this.levelLabel = null;
    }
}
