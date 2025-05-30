package com.example.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.utils.TileRenderer;

@ExtendWith(MockitoExtension.class)
class TileTest {
    @Mock
    private TileModel mockModel;
    @Mock
    private TileRenderer mockRenderer;
    
    private Tile tile;
    private TileView tileView;

    @BeforeEach
    void setUp() {
        // Create a real TileView with null image (fine for testing)
        tileView = new TileView(null, TileEnum.EMPTY_TOWER_TILE);
        tile = new Tile(tileView, mockModel);
    }

    @Test
    void placeTower_OnBuildableTile_ShouldPlaceTower() {
        // Arrange
        TileView mockTowerView = new TileView(null, TileEnum.ARTILLERY_TOWER);
        when(mockRenderer.createTileView(TileEnum.ARTILLERY_TOWER)).thenReturn(mockTowerView);

        // Act
        tile.placeTower(TileEnum.ARTILLERY_TOWER, mockRenderer);

        // Assert
        assertEquals(TileEnum.ARTILLERY_TOWER, tileView.getType());
        verify(mockModel).setTowerType(TileEnum.ARTILLERY_TOWER);
        assertTrue(tile.hasTower());
    }

    @Test
    void placeTower_OnNonBuildableTile_ShouldNotPlaceTower() {
        // Arrange
        tileView.setType(TileEnum.GRASS);

        // Act
        tile.placeTower(TileEnum.ARTILLERY_TOWER, mockRenderer);

        // Assert
        assertEquals(TileEnum.GRASS, tileView.getType());
        verify(mockModel, never()).setTowerType(any());
        assertFalse(tile.hasTower());
    }

    @Test
    void placeTower_OnTileWithExistingTower_ShouldNotPlaceTower() {
        // Arrange
        // First placement
        TileView mockTowerView = new TileView(null, TileEnum.ARTILLERY_TOWER);
        when(mockRenderer.createTileView(any())).thenReturn(mockTowerView);
        tile.placeTower(TileEnum.ARTILLERY_TOWER, mockRenderer);
        
        // Reset verification counts
        clearInvocations(mockModel);

        // Act - Try to place another tower
        tile.placeTower(TileEnum.MAGE_TOWER, mockRenderer);

        // Assert
        assertEquals(TileEnum.ARTILLERY_TOWER, tileView.getType());
        verify(mockModel, never()).setTowerType(any());
    }
} 