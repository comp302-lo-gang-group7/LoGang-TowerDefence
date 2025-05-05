package com.example.map;

import com.example.storage_manager.Serializable;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileView extends ImageView implements Serializable {
    private TileEnum tileType;

    public TileView(Image image, TileEnum tileType) {
        super(image);
        this.tileType = tileType;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return "";
    }

    @Override
    public void fromJson(String json) throws JsonProcessingException {

    }
}
