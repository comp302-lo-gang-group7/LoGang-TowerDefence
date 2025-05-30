package com.example.map;

import com.example.utils.TileRenderer;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TileTest {
    @Mock
    private TileView mockView;
    @Mock
    private TileModel mockModel;
    @Mock
    private TileRenderer mockRenderer;
    
    private Tile tile;

    @BeforeEach
    void setUp() {
        tile = new Tile(mockView, mockModel);
    }

    @Test
    void placeTower_OnBuildableTile_ShouldPlaceTower() {
        // Arrange
        when(mockView.getType()).thenReturn(TileEnum.EMPTY_TOWER_TILE);
        TileView mockTowerView = mock(TileView.class);
        when(mockTowerView.getImage()).thenReturn(null); // Image is not relevant for this test
        when(mockRenderer.createTileView(TileEnum.ARTILLERY_TOWER)).thenReturn(mockTowerView);

        // Act
        tile.placeTower(TileEnum.ARTILLERY_TOWER, mockRenderer);

        // Assert
        verify(mockView).setImage(any());
        verify(mockView).setType(TileEnum.ARTILLERY_TOWER);
        verify(mockModel).setTowerType(TileEnum.ARTILLERY_TOWER);
        assertTrue(tile.hasTower());
    }

    @Test
    void placeTower_OnNonBuildableTile_ShouldNotPlaceTower() {
        // Arrange
        when(mockView.getType()).thenReturn(TileEnum.GRASS); // Non-buildable tile

        // Act
        tile.placeTower(TileEnum.ARTILLERY_TOWER, mockRenderer);

        // Assert
        verify(mockView, never()).setImage(any());
        verify(mockView, never()).setType(any());
        verify(mockModel, never()).setTowerType(any());
        assertFalse(tile.hasTower());
    }

    @Test
    void placeTower_OnTileWithExistingTower_ShouldNotPlaceTower() {
        // Arrange
        when(mockView.getType()).thenReturn(TileEnum.EMPTY_TOWER_TILE);
        // First placement
        TileView mockTowerView = mock(TileView.class);
        when(mockRenderer.createTileView(any())).thenReturn(mockTowerView);
        tile.placeTower(TileEnum.ARTILLERY_TOWER, mockRenderer);
        
        // Reset verification counts
        clearInvocations(mockView, mockModel);

        // Act - Try to place another tower
        tile.placeTower(TileEnum.MAGE_TOWER, mockRenderer);

        // Assert
        verify(mockView, never()).setImage(any());
        verify(mockView, never()).setType(any());
        verify(mockModel, never()).setTowerType(any());
    }
} 