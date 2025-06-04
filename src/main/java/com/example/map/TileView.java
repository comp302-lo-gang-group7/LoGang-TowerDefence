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

public class TileView extends ImageView implements Serializable {
    private TileEnum tileType;
    private String originalStyle;

    public TileView(Image image, TileEnum tileType) {
        super(image);
        this.tileType = tileType;
    }

    public void setType(TileEnum tileType) {
        this.tileType = tileType;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return this.getType().name();
    }

    @Override
    public void fromJson(String json) throws JsonProcessingException { }

    public TileEnum getType() {
        return this.tileType;
    }
    
    /**
     * Highlights this tile to indicate a road connection error
     */
    public void showErrorHighlight() {
        // Store original style if it exists
        originalStyle = this.getStyle();
        
        // Apply error effect
        DropShadow errorEffect = new DropShadow();
        errorEffect.setColor(Color.RED);
        errorEffect.setRadius(10);
        errorEffect.setSpread(0.7);
        
        this.setEffect(errorEffect);
        this.setStyle("-fx-border-color: red; -fx-border-width: 2; -fx-border-style: dashed;");
        
        // Increase visibility slightly
        this.setOpacity(0.8);
    }
    
    /**
     * Removes the error highlight
     */
    public void removeErrorHighlight() {
        this.setEffect(null);
        this.setStyle(originalStyle);
        this.setOpacity(1.0);
    }
}
