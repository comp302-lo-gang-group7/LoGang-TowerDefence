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
 * Represents a visual tile in the map, extending ImageView to display an image and manage tile-specific properties.
 */
public class TileView extends ImageView implements Serializable {
    private TileEnum tileType;
    private String originalStyle;

    /**
     * Constructs a TileView with the specified image and tile type.
     *
     * @param image    The image to be displayed on the tile.
     * @param tileType The type of the tile, represented by a TileEnum value.
     */
    public TileView(Image image, TileEnum tileType) {
        super(image);
        this.tileType = tileType;
    }

    /**
     * Sets the type of the tile.
     *
     * @param tileType The new type of the tile, represented by a TileEnum value.
     */
    public void setType(TileEnum tileType) {
        this.tileType = tileType;
    }

    /**
     * Converts the tile type to a JSON string representation.
     *
     * @return A JSON string representing the tile type.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
    @Override
    public String toJson() throws JsonProcessingException {
        return this.getType().name();
    }

    /**
     * Updates the tile type from a JSON string representation.
     *
     * @param json The JSON string representing the tile type.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
    @Override
    public void fromJson(String json) throws JsonProcessingException { }

    /**
     * Retrieves the type of the tile.
     *
     * @return The type of the tile, represented by a TileEnum value.
     */
    public TileEnum getType() {
        return this.tileType;
    }

    /**
     * Applies a visual highlight to indicate an error related to road connection.
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
     * Removes the visual highlight applied to indicate an error.
     */
    public void removeErrorHighlight() {
        this.setEffect(null);
        this.setStyle(originalStyle);
        this.setOpacity(1.0);
    }
}
