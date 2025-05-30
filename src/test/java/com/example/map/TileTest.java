package com.example.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TileTest {
    private Tile tile;
    private TileView tileView;
    private TileModel tileModel;

    @BeforeEach
    void setUp() {
        // Create real instances
        tileView = new TileView(null, TileEnum.EMPTY_TOWER_TILE);
        tileModel = new TileModel(0, 0);
        tile = new Tile(tileView, tileModel);
    }

    @Test
    void placeTower_OnBuildableTile_ShouldPlaceTower() {
        // Act
        tile.placeTower(TileEnum.ARTILLERY_TOWER, null);

        // Assert
        assertEquals(TileEnum.ARTILLERY_TOWER, tileView.getType());
        assertEquals(TileEnum.ARTILLERY_TOWER, tileModel.getTowerType());
        assertTrue(tile.hasTower());
    }

    @Test
    void placeTower_OnNonBuildableTile_ShouldNotPlaceTower() {
        // Arrange
        tileView.setType(TileEnum.GRASS);

        // Act
        tile.placeTower(TileEnum.ARTILLERY_TOWER, null);

        // Assert
        assertEquals(TileEnum.GRASS, tileView.getType());
        assertNull(tileModel.getTowerType());
        assertFalse(tile.hasTower());
    }

    @Test
    void placeTower_OnTileWithExistingTower_ShouldNotPlaceTower() {
        // Arrange - Place first tower
        tile.placeTower(TileEnum.ARTILLERY_TOWER, null);
        
        // Remember the initial state
        TileEnum initialType = tileView.getType();

        // Act - Try to place another tower
        tile.placeTower(TileEnum.MAGE_TOWER, null);

        // Assert
        assertEquals(initialType, tileView.getType());
        assertEquals(TileEnum.ARTILLERY_TOWER, tileModel.getTowerType());
        assertTrue(tile.hasTower());
    }
} 