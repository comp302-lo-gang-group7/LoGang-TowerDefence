package com.example.map;

import com.example.storage_manager.Serializable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jdi.StringReference;
import javafx.animation.PauseTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Class TileView
 */
public class TileView extends ImageView implements Serializable {
    private TileEnum tileType;
    private String originalStyle;

    /**
     * TODO
     */
    public TileView(Image image, TileEnum tileType) {
        super(image);
        this.tileType = tileType;
    }

    /**
     * TODO
     */
    public void setType(TileEnum tileType) {
        this.tileType = tileType;
    }

    @Override
    /**
     * TODO
     */
    public String toJson() throws JsonProcessingException {
        return this.getType().name();
    }

    @Override
    /**
     * TODO
     */
    public void fromJson(String json) throws JsonProcessingException { }

    /**
     * TODO
     */
    public TileEnum getType() {
        return this.tileType;
    }


    /**
     * TODO
     */
    public void showErrorHighlight() {

        originalStyle = this.getStyle();


        DropShadow errorEffect = new DropShadow();
        errorEffect.setColor(Color.RED);
        errorEffect.setRadius(10);
        errorEffect.setSpread(0.7);

        this.setEffect(errorEffect);
        this.setStyle("-fx-border-color: red; -fx-border-width: 2; -fx-border-style: dashed;");


        this.setOpacity(0.8);
    }


    /**
     * TODO
     */
    public void removeErrorHighlight() {
        this.setEffect(null);
        this.setStyle(originalStyle);
        this.setOpacity(1.0);
    }
}
