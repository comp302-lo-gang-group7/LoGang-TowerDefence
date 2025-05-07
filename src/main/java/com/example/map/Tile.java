package com.example.map;

import com.example.utils.TileRenderer;

public class Tile {
    public TileView view;
    public TileModel model;
    private TileEnum towerType;

    private boolean hasTower;

    public Tile(TileView view, TileModel model) {
        this.view = view;
        this.model = model;
        this.hasTower = false;
    }

    public boolean isBuildable() {
        return view.getType() == TileEnum.EMPTY_TOWER_TILE && !hasTower;
    }

    public void placeTower(TileEnum towerType, TileRenderer renderer) {
        if (!isBuildable()) return;

        view.setImage(renderer.createTileView(towerType).getImage());
        view.setType(towerType);

        this.model.setTowerType(towerType); // optional, see below
        hasTower = true;
    }


    public void removeTower(TileRenderer renderer) {
        // Set back to lot
        view.setImage(renderer.createTileView(TileEnum.EMPTY_TOWER_TILE).getImage());
        view.setType(TileEnum.EMPTY_TOWER_TILE);
        hasTower = false;
        towerType = null;
    }

    public boolean hasTower() {
        return hasTower;
    }


    public void setTowerType(TileEnum towerType) {
        this.towerType = towerType;
    }

    public TileEnum getTowerType() {
        return towerType;
    }

}

