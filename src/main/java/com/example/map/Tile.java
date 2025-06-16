package com.example.map;

import javafx.scene.control.Label;

public class Tile {
    public TileView view;
    public TileModel model;
    public Label levelLabel;

    public Tile(TileView view, TileModel model) {
        this.view = view;
        this.model = model;
        this.levelLabel = null;
    }
}

