package com.example.map;

import com.example.utils.TileRenderer;

public class Tile {
    public TileView view;
    public TileModel model;

    public Tile(TileView view, TileModel model) {
        this.view = view;
        this.model = model;
    }

    public boolean isBuildable() {
        return model.getType() == TileEnum.EMPTY_TOWER_TILE && !model.hasTower();
    }

//    public void placeTower(TileEnum towerType, TileRenderer renderer) {
//        if (!isBuildable()) return;
//
//        view.setImage(renderer.createTileView(towerType).getImage());
//        view.setType(towerType);
//
//        this.model.setType(towerType); // optional, see below
//    }
//
//
//    public void removeTower(TileRenderer renderer) {
//        // Set back to lot
//        view.setImage(renderer.createTileView(TileEnum.EMPTY_TOWER_TILE).getImage());
//        view.setType(TileEnum.EMPTY_TOWER_TILE);
//    }
}

